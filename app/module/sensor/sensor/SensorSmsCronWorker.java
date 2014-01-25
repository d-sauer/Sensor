package module.sensor.sensor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import module.core.models.DBCSmsQ;
import module.core.models.DBCUser;
import module.core.models.DBCSmsQ.DBCSmsQAction;
import module.sensor.CronSceduleRule;
import module.sensor.models.DBSUserSensor;
import module.sensor.models.DBSUserSmsCron;
import module.sensor.models.DBSUserSmsCronSensors;
import module.sensor.models.DBSensorData;
import util.DateUtils;
import util.NumberUtils;
import util.data.WDate;
import util.db.EbeanUtils;
import util.logger.log;
import util.thread.Worker;
import util.thread.WorkerPool;

public class SensorSmsCronWorker extends Worker {

    private static volatile SensorSmsCronWorker smsSceduler;

    public SensorSmsCronWorker() {

    }

    public static SensorSmsCronWorker getInstance() {
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
                if (w instanceof SensorSmsCronWorker) {
                    smsSceduler = (SensorSmsCronWorker) w;
                    break;
                }
            }
        }

        if (smsSceduler == null) {
            smsSceduler = new SensorSmsCronWorker();
            smsSceduler.infiniteLoop(300000);    // 5min = 5 * 60 * 1000 = 300000
            smsSceduler.setName("Sensor SMS Cron Worker");
        }

        return smsSceduler;
    }

    @Override
    public void work() throws Exception {
        // Dohvati popis svih aktivnih korisnika
        WDate datum = new WDate(WDate.ddMMyyyy_HHmmss);
        datum.setCurrentTime();
        // datum.set("17.02.2013 17:30:00");

        List<DBCUser> ldbU = DBCUser.findAllActive();
        for (DBCUser dbU : ldbU) {
            // Za svakog korisnika pronađi definirane scedulere
            List<DBSUserSmsCron> lusc = DBSUserSmsCron.findByUser(dbU);

            for (DBSUserSmsCron usc : lusc) {
                Long nextScedule = null;
                Long lastScedule = null;
                Long currentTime = datum.getTimeInMillis();

                CronSceduleRule cron = usc.getRule();
                log.info(" user_id: %d, current time: %s, next scedule: %s", dbU.id, datum.getFormatted(), DateUtils.getFormatedDateTime(usc.nextScedule, "-"));

                if (usc.nextScedule != null && usc.lastScedule != null) {
                    WDate scedule = new WDate(WDate.ddMMyyyy_HHmmss);
                    scedule.set(usc.nextScedule);

                    if (currentTime >= usc.nextScedule.getTime()) {
                        Date d = new Date(usc.nextScedule.getTime());
                        d = cron.getNext(datum.get());
                        nextScedule = d.getTime();

                        usc.lastScedule = usc.nextScedule;
                        usc.nextScedule = new java.sql.Timestamp(nextScedule);
                        usc.save();

                        log.info(" sms to user id: %d, last scedule: %s, next scedule: %s", dbU.id, DateUtils.getFormatedDateTime(usc.lastScedule), DateUtils.getFormatedDateTime(usc.nextScedule));
                        addSmsToQueue(usc);
                    }
                }
                else {
                    Date d = cron.getNext(datum.get());
                    nextScedule = d.getTime();
                    usc.lastScedule = new java.sql.Timestamp(datum.getTimeInMillis());
                    usc.nextScedule = new java.sql.Timestamp(nextScedule);
                    usc.save();
                }

            }

        }

    }

    private void addSmsToQueue(DBSUserSmsCron usc) throws Exception {
        //
        // Pripremi SMS
        //
        StringBuilder message = new StringBuilder();

        List<DBSUserSmsCronSensors> luscs = DBSUserSmsCronSensors.findByCronId(usc.id);
        int count = 0;
        Long maxTime = 0L;
        for (DBSUserSmsCronSensors cs : luscs) {
            DBSUserSensor dbUS = cs.userSensor;

            String sql = "SELECT date_time, " + dbUS.tableColumn + " FROM " + dbUS.tableName + " ORDER BY id DESC";
            ResultSet rs = null;
            try {
                rs = EbeanUtils.executeQuery(sql);
                if (rs.next()) {
                    java.sql.Timestamp date_time = rs.getTimestamp("date_time");
                    String value = rs.getString(dbUS.tableColumn);
                    
                    Double d = NumberUtils.doubleOf(value);
                    if (d != null) {
                        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                        value = df.format(d);
                    } else {
                        Long l = NumberUtils.longOf(value);
                        if (l != null) {
                            DecimalFormat df = new DecimalFormat("#,###,###,###");
                            value = df.format(l);
                        }
                    }
                    

                    if (count++ != 0)
                        message.append(", ");

                    message.append(dbUS.label + ": " + value);

                    if (maxTime < date_time.getTime())
                        maxTime = date_time.getTime();

                }
            } catch (Exception e) {

            } finally {
                if (rs != null)
                    rs.close();
            }

        }

        WDate maxDate = new WDate("dd.MM.yyyy HH:mm");
        maxDate.set(maxTime);

        message.insert(0, "Datum:" + maxDate.getFormatted() + "\n");

        List<String> mobiles = usc.getMobiles();

        for (String mobile : mobiles) {
            Date d = new Date();
            DBCSmsQ smsQ = new DBCSmsQ();
            smsQ.user = usc.user;
            smsQ.dateCreated = new java.sql.Timestamp(d.getTime());
            smsQ.mobileNumber = mobile;
            smsQ.action = DBCSmsQAction.send;
            smsQ.content = message.toString();
            smsQ.save();
        }

    }
}
