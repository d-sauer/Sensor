package controllers.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import module.core.models.DBCUser;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.SensorGroupInterface;
import module.sensor.sensor.SensorInterface;
import module.sensor.sensor.SensorWorker;
import module.sensor.sensor.SupportedSensors;

import org.codehaus.jackson.JsonNode;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.html.HInput;
import util.hlib.html.HTextarea;
import util.logger.log;
import util.thread.Worker;
import util.thread.WorkerPool;
import util.thread.WorkerStatus;
import controllers.ModuleController;

public class KorisnikEditSensors extends Controller {

    public static class UserSensorGroup {
        public DBSensorGroup sg;
        public SensorWorker sw;
    }
    
    private static List<UserSensorGroup> getUserSensors(Long userId) {
        List<UserSensorGroup> lusg = new ArrayList<KorisnikEditSensors.UserSensorGroup>();
        
        List<DBSensorGroup> lsg = DBSensorGroup.findByUser(userId);
        for(DBSensorGroup sg : lsg) {
            SensorWorker sw = SensorWorker.getSensorWorker(sg.id);
            
            UserSensorGroup usg = new UserSensorGroup();
            usg.sg = sg;
            usg.sw = sw;
            lusg.add(usg);
        }
        
        return lusg;
    }
    
    public static Result loadModule(Long userId) throws Exception {
        log.debug("User ID: " + userId);
        DBCUser dbu = DBCUser.findById(userId);

        JsonNode jn = CallController.getCallJson();
        JsonNode jsContent = jn.get("content");
        String content = jsContent.asText();

        // Dohvati grupe senzora korisnika i senzore u pojedinoj grupi za
        // korisnika
        List<UserSensorGroup> lusg = getUserSensors(userId);

        Html h = views.html.admin.KorisnikEditSensors.render(SupportedSensors.getSupportedSensors(), userId, lusg);

        return ok(ModuleController.result(KorisnikEditSensors.class, content, h));
    }

    public static Result refresh() throws Exception {
        HDataContainer hdc = CallController.getRequestData();

        HInput hUserId = HInput.load(hdc, "user_id");
        Long userId = Long.parseLong(hUserId.getAttrValue());
        log.debug("User id:" + userId);

        return loadModule(userId);
    }

    public static Result refreshSensorTab(Long userId) throws Exception {
        if (userId != null) {
            List<UserSensorGroup> lusg = getUserSensors(userId);
            Html h = views.html.admin.KorisnikEditSensors.render(SupportedSensors.getSupportedSensors(), userId, lusg);

            return ok(ModuleController.result(KorisnikEditSensors.class, "#senzori_korisnika", h));
        } else {
            return ok();
        }
    }

    public static Result addSelected(Long userId) throws Exception {
        HDataContainer hdc = CallController.getRequestData();
        HashMap<Integer, List<SensorInterface>> hmSGid_Sensor = new HashMap<Integer, List<SensorInterface>>();

        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;

                HDataValue<Boolean> hdv = hi.getChecked();
                if (hdv != null && hdv.getValue() == true) {
                    log.debug("checked:" + hi.getId());

                    String[] tmp = hi.getId().split(":");

                    Integer sensorGroupId = Integer.parseInt(tmp[1]);
                    Integer sensorId = Integer.parseInt(tmp[2]);

                    SensorInterface s = SupportedSensors.getSensors(sensorGroupId, sensorId).newInstance();

                    List<SensorInterface> lsg = hmSGid_Sensor.get(sensorGroupId);
                    if (lsg == null) {
                        lsg = new ArrayList<SensorInterface>();
                        hmSGid_Sensor.put(sensorGroupId, lsg);
                    }

                    lsg.add(s);
                }
            }
        }

        // Add sensor group and selected sensors to DB
        if (!hmSGid_Sensor.isEmpty()) {
            DBCUser dbu = DBCUser.findById(userId);

            for (Entry<Integer, List<SensorInterface>> en : hmSGid_Sensor.entrySet()) {
                Integer sgId = en.getKey();
                List<SensorInterface> ls = en.getValue();
                SensorGroupInterface sg = SupportedSensors.getSensorsGroup(sgId).newInstance();
                
                // Add group
                DBSensorGroup dbSG = new DBSensorGroup();
                dbSG.user = dbu;
                dbSG.name = sg.getName();
                dbSG.sensorGroupClass = sg.getClass().getName();

                dbSG.save();

                // Add sensors from group
                for (SensorInterface s : ls) {
                    DBSensor dbS = new DBSensor();
                    dbS.group = dbSG;
                    dbS.user = dbu;
                    dbS.name = s.getName();
                    dbS.sensorClass = s.getClass().getName();
                    dbS.description = s.getDescription();
                    dbS.active = true;
                    dbS.visible = true;

                    dbS.save();
                }

            }
        }

        return refreshSensorTab(userId);
    }

    public static Result DelSelGroup(Long userId) throws Exception {
        HDataContainer hdc = CallController.getRequestData();
        HashMap<SensorGroupInterface, List<SensorInterface>> hmInserSensor = new HashMap<SensorGroupInterface, List<SensorInterface>>();
        
        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;
                
                String id = hi.getId();
                if (id!=null && id.startsWith("ch_sensor_group_") && hi.getChecked()!=null && hi.getChecked().getValue()==true) {
                    log.debug("checked:" + hi.getId());
                    String sgId = id.substring("ch_sensor_group_".length());
                    DBSensorGroup dbSG = DBSensorGroup.findById(Long.parseLong(sgId));
                    if (dbSG != null) {
                        dbSG.delete();
                    }
                }
            }
        }
        
        
        return refreshSensorTab(userId);
    }

    public static Result SaveGroupOptions(Long groupId) throws Exception {
        log.debug("group:" + groupId);
        HDataContainer hdc = CallController.getRequestData();

        DBSensorGroup dbSG = DBSensorGroup.findById(groupId);
        SensorGroupInterface sg = dbSG.getGroupClass();
        Long userId = dbSG.user.id;

        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;
                log.debug(hi.toString());

                if ("name".equals(hi.getId())) {
                    dbSG.name = hi.getAttrValue();
                }
                // properties
                else if (hi.getId().startsWith("prop_")) {
                    sg.setProperty(hi.getId().substring("prop_".length()), hi.getAttrValue());
                }
            }
            else if (hd instanceof HTextarea) {
                HTextarea ht = (HTextarea) hd;
                log.debug(ht.toString());

                if ("description".equals(ht.getId())) {
                    dbSG.description = ht.getAttrValue();
                }

            }
        }

        dbSG.save();

        return refreshSensorTab(userId);
    }

    public static Result SaveSensorOptions(Long sensorId) throws Exception {
        log.debug("sensor:" + sensorId);
        HDataContainer hdc = CallController.getRequestData();

        DBSensor dbS = DBSensor.findById(sensorId);
        SensorInterface s = dbS.getSensorClass();
        Long userId = dbS.user.id;

        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;
                log.debug(hi.toString());

                if ("name".equals(hi.getId())) {
                    dbS.name = hi.getAttrValue();
                }
                // properties
                else if (hi.getId().startsWith("prop_")) {
                    s.setProperty(hi.getId().substring("prop_".length()), hi.getAttrValue());
                }
            }
            else if (hd instanceof HTextarea) {
                HTextarea ht = (HTextarea) hd;
                log.debug(ht.toString());

                if ("description".equals(ht.getId())) {
                    dbS.description = ht.getAttrValue();
                }

            }
        }

        dbS.save();

        return refreshSensorTab(userId);
    }

    public static Result AddSensor(Long groupId) throws Exception {
        log.debug("group:" + groupId);
        HDataContainer hdc = CallController.getRequestData();

        DBSensorGroup dbSG = DBSensorGroup.findById(groupId);
        Long userId = null;

        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;

                if (hi.getChecked() != null && hi.getChecked().getValue() == true) {
                    log.debug("sensor: " + hi.toString());

                    SensorInterface s = SupportedSensors.getSensorsGroup(dbSG.sensorGroupClass).getSensorByIndex(new Integer(hi.getId())).newInstance();
                    if (s != null) {
                        DBSensor dbS = new DBSensor();
                        dbS.group = dbSG;
                        dbS.user = dbSG.user;
                        dbS.name = s.getName();
                        dbS.sensorClass = s.getClass().getName();
                        dbS.description = s.getDescription();
                        dbS.active = true;
                        dbS.visible = true;

                        dbS.save();

                        userId = dbSG.user.id;
                    }
                }
            }
        }

        return refreshSensorTab(userId);
    }

    public static Result DelSensor(Long groupId) throws Exception {
        log.debug("group:" + groupId);
        HDataContainer hdc = CallController.getRequestData();
        Long userId = null;

        for (HData hd : hdc.get()) {
            if (hd instanceof HInput) {
                HInput hi = (HInput) hd;

                if (hi.getChecked() != null && hi.getChecked().getValue() == true) {
                    log.debug("sensor delete: " + hi.toString());
                    Long sId = Long.parseLong(hi.getId());
                    
                    DBSensor dbS = DBSensor.findById(sId);
                    userId = dbS.user.id;

                    dbS.delete();
                }
            }
        }
        
        return refreshSensorTab(userId);
    }

    public static Result workerPause(Integer workerId, Long userId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker!=null) {
            log.info("SensorWorker id %d found - do pause", workerId);
            
            worker.doPause();
            
        } else {
            log.warning("SensorWorker id %d not found!", workerId);
        }
        
        
        return refreshSensorTab(userId);
    }

    public static Result workerResume(Integer workerId, Long userId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker!=null) {
            log.info("SensorWorker id %d found - do resume", workerId);
            
            worker.doResume();
            
        } else {
            log.warning("SensorWorker id %d not found!", workerId);
        }
        
        
        return refreshSensorTab(userId);
    }

    public static Result workerStop(Integer workerId, Long userId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker != null) {
            log.info("SensorWorker id %d found - do stop", workerId);
            
            worker.keepInPoolAfterFinish(false);
            if (worker.getStatus()==WorkerStatus.SLEEP)
                worker.doForceStop();
            else
                worker.doStop();
            
        } else {
            log.warning("SensorWorker id %d not found!", workerId);
        }
        
        
        return refreshSensorTab(userId);
    }

    public static Result workerNew(Long groupId, Long userId) throws Exception {
        SensorWorker sw = SensorWorker.getSensorWorker(groupId);
        if (sw == null) {
            SensorWorker.createSensorWorker(groupId);
        }
        
        return refreshSensorTab(userId);
    }
    
}
