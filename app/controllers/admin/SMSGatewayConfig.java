package controllers.admin;

import java.util.Date;

import module.core.models.DBCSmsQ;
import module.core.models.SmsQueueWorker;
import module.core.models.DBCSmsQ.DBCSmsQAction;
import module.sensor.sensor.SensorSmsCronWorker;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.data.Return;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HInput;
import util.hlib.html.HLabel;
import util.hlib.html.HTextarea;
import util.sms.BulkSmsStatus;
import util.thread.Worker;
import util.thread.WorkerStatus;
import controllers.ModuleController;

public class SMSGatewayConfig extends Controller {

    public static Result refresh() throws Exception {
        return loadModule();
    }

    public static Result loadModule() throws Exception {
        //
        // SMS cron status
        //
        SmsQueueWorker sqw = SmsQueueWorker.getInstance();
        WorkerStatus wqstat = sqw.getStatus();
        String smsSenderStatus = wqstat.toString();

        
        //
        // SMS cron status
        //
        SensorSmsCronWorker sw = SensorSmsCronWorker.getInstance();
        WorkerStatus wstat = sw.getStatus();
        String smsCronStatus = wstat.toString();
        
        
        Html h = views.html.admin.SMSGatewayConfig.render(smsSenderStatus, smsCronStatus);
        return ok(ModuleController.result(SMSGatewayConfig.class, h));
    }

    public static Result sendTestSMS() throws Exception {
        HDataContainer hdc = CallController.getRequestData();

        HInput hTestSmsBroj = HTagUtil.getElementById(hdc, "test_sms_broj", HInput.class);
        HTextarea hTestSmsMsg = HTagUtil.getElementById(hdc, "test_sms_msg", HTextarea.class);
        HLabel hTestSmsStatus = new HLabel("test_sms_status");

        if (!hTestSmsBroj.isBlank("value")) {
            if (!hTestSmsMsg.isBlank("value")) {
                String mobile = hTestSmsBroj.getAttrValue();
                String message = hTestSmsMsg.getAttrValue();
                java.util.Date datum = new Date();

                DBCSmsQ dbSmsQ = new DBCSmsQ();
                try {
                    dbSmsQ.mobileNumber = hTestSmsBroj.getAttrValue();
                    dbSmsQ.content = hTestSmsMsg.getAttrValue();
                    dbSmsQ.dateCreated = new java.sql.Timestamp(datum.getTime());
                    dbSmsQ.dateSend = new java.sql.Timestamp(datum.getTime());
                    dbSmsQ.action = DBCSmsQAction.sending;

                    dbSmsQ.save();

                    Return<BulkSmsStatus> ret = SMSGateway.sensSMS(mobile, message);

                    datum = new Date();
                    dbSmsQ.dateConfirm = new java.sql.Timestamp(datum.getTime());
                    dbSmsQ.action = DBCSmsQAction.sent;
                    dbSmsQ.update();

                    hTestSmsStatus.set("Status:" + ret.getReturnObject());
                } catch (Exception e) {
                    dbSmsQ.action = DBCSmsQAction.not_sent;
                    dbSmsQ.update();

                    throw e;
                }
            } else {
                hTestSmsStatus.set("Unesite poruku");
            }
        } else {
            hTestSmsStatus.set("Unesite broj mobitela 385xxYYYNNNN");
        }

        HData data = new HData();
        data.addNode(hTestSmsStatus);

        return ok(data.getJsonString());
    }

    public static Result cronPause() throws Exception {
        HData data = new HData();
        HLabel lStatus = new HLabel("status");
        data.addNode(lStatus);

        Worker sw = SensorSmsCronWorker.getInstance();
        if (!sw.isPaused()) {
            sw.doPause();
            lStatus.set("SMS Cron paused");
        }

        return ok(data.getJsonString());
    }

    public static Result cronStart() throws Exception {
        HData data = new HData();
        HLabel lStatus = new HLabel("cron_status");
        data.addNode(lStatus);
        
//        SensorSmsCronWorker sw = new SensorSmsCronWorker();
//        sw.work();
        
        Worker sw = SensorSmsCronWorker.getInstance();
        if (sw.isPaused()) {
            sw.doResume();
            lStatus.set("SMS Cron resumed");
        } else if (sw.isNew()) {
            sw.start();
            lStatus.set("SMS Cron created");
        }

        return ok(data.getJsonString());
    }

    public static Result senderPause() throws Exception {
        HData data = new HData();
        HLabel lStatus = new HLabel("sender_status");
        data.addNode(lStatus);
        
        Worker sw = SmsQueueWorker.getInstance();
        if (!sw.isPaused()) {
            sw.doPause();
            lStatus.set("SMS sender paused");
        }
        
        return ok(data.getJsonString());
    }
    
    public static Result senderStart() throws Exception {
        HData data = new HData();
        HLabel lStatus = new HLabel("sender_status");
        data.addNode(lStatus);
        
//        SmsQueueWorker sw = new SmsQueueWorker();
//        sw.work();
        
        Worker sw = SmsQueueWorker.getInstance();
        if (sw.isPaused()) {
            sw.doResume();
            lStatus.set("SMS sender resumed");
        } else if (sw.isNew()) {
            sw.start();
            lStatus.set("SMS sender created");
        }
        
        return ok(data.getJsonString());
    }

}
