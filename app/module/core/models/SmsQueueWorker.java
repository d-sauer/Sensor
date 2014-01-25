package module.core.models;

import java.util.List;

import controllers.admin.SMSGateway;

import module.core.models.DBCSmsQ.DBCSmsQAction;

import util.data.Return;
import util.logger.log;
import util.sms.BulkSmsStatus;
import util.thread.Worker;
import util.thread.WorkerPool;

public class SmsQueueWorker extends Worker {

    private static volatile SmsQueueWorker smsSceduler;

    public SmsQueueWorker() {

    }

    public static SmsQueueWorker getInstance() {
        List<Worker> lstW = WorkerPool.getWorkers();

        if (smsSceduler != null) {
            if (smsSceduler.isStopped()) {
                WorkerPool.removeJob(smsSceduler);
                smsSceduler = null;
            }
        }

        // Provjeri dali u jobPoolu postoji vec worker
        if (smsSceduler == null) {
            for (Worker w : lstW) {
                if (w instanceof SmsQueueWorker) {
                    smsSceduler = (SmsQueueWorker) w;
                    break;
                }
            }
        }

        if (smsSceduler == null) {
            smsSceduler = new SmsQueueWorker();
            smsSceduler.infiniteLoop(300000);    // 5min = 5 * 60 * 1000 = 300000
            smsSceduler.setName("SMS Q sender");
        }

        return smsSceduler;
    }

    @Override
    public void work() throws Exception {
        List<DBCSmsQ> lSQ = DBCSmsQ.findByAction(DBCSmsQAction.send);

        for (DBCSmsQ sms : lSQ) {
            try {
                log.info("Sending SMS to number: %s, user_id: %s", sms.mobileNumber, (sms.user != null ? sms.user.id : "null"));
                sms.action = DBCSmsQAction.sending;
                sms.save();

                Return<BulkSmsStatus> r = SMSGateway.sensSMS(sms.mobileNumber, sms.content);
                BulkSmsStatus bss = r.getReturnObject();
                if (bss == BulkSmsStatus.IN_PROGRESS) {
                    sms.action = DBCSmsQAction.sent;
                    sms.save();
                } else {
                    sms.action = DBCSmsQAction.not_sent;
                    sms.save();
                }
                log.info("End sending SMS to number: %s, user_id: %s", sms.mobileNumber, (sms.user != null ? sms.user.id : "null"));

            } catch (Exception e) {
                sms.action = DBCSmsQAction.not_sent;
                sms.save();

                log.exception(e);
            }
        }
    }

}
