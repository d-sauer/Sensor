package controllers.sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.core.models.DBCUser;
import module.sensor.models.DBSUserGroup;
import module.sensor.models.DBSUserSensor;
import module.sensor.models.DBSUserSensorGroup;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.AvailableSensor;
import module.sensor.sensor.AvailableSensorsData;
import module.sensor.sensor.SensorInterface;
import module.sensor.sensor.SensorProperty;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import scala.actors.threadpool.Arrays;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HInput;
import util.hlib.html.HLabel;
import util.hlib.html.HTextarea;
import util.hlib.html.HUtil;
import util.logger.log;
import util.session.SessionData;
import controllers.Application;
import controllers.ModuleController;

public class UserSensorGroupManage extends Controller {

    public static class uSensorGroup {
        public DBSensorGroup dbGroup;
        public List<AvailableSensor> sensors = new ArrayList<AvailableSensor>();
        
    }

    public static Result refresh() throws Exception {
        return loadModule();
    }

    public static Result loadModule() throws Exception {
        SessionData sd = SessionData.get();

        if (sd != null) {
            log.debug("User ID: %d, isAdmin:%s", sd.getUserId(), sd.isAdmin());
            return showData(sd.getUserId(), sd.isAdmin());
        } else {
            log.warning("session not found");
            return redirect(controllers.routes.Application.index());
        }
    }

    private static Result showData(Long userId) throws Exception {
        SessionData sd = SessionData.get();
        return showData(userId, sd.isAdmin());
    }

    
    
    private static Result showData(Long userId, boolean isAdmin) throws Exception {
        // Ako je admin dohvati popis korinsika za edit..
        List<DBCUser> dbu = null;
        if (isAdmin) {
            userId = Application.getAdminSelUser();
            dbu = DBCUser.find.all();
        }

        // dohvati popis grupa korinsika
        List<DBSUserGroup> lUG = DBSUserGroup.findByUser(userId);
        
        //
        // Dohvati popis senzora koji mogu biti u grupama za korisnika
        //
        Set<uSensorGroup> sGroup = new HashSet<UserSensorGroupManage.uSensorGroup>();
        HashMap<Long, uSensorGroup> hmSG = new HashMap<Long, UserSensorGroupManage.uSensorGroup>();
        
        if (userId!=null) {
            AvailableSensorsData asd = new AvailableSensorsData(userId);
            Set<AvailableSensor> sas = asd.getAvailableSensorData();
            
            
            for(AvailableSensor as :sas) {
                uSensorGroup sg = hmSG.get(as.groupId);
                
                if (sg == null) {
                    sg = new uSensorGroup();
                    sg.dbGroup = DBSensorGroup.findById(as.groupId);
                    
                    hmSG.put(as.groupId, sg);
                    sGroup.add(sg);
                } 
                
                sg.sensors.add(as);
            }
            
        }

        
        Html h = views.html.sensor.UserSensorGroupManage.render(lUG, dbu, Application.getAdminSelUser(), sGroup);
        return ok(ModuleController.result(UserSensorGroupManage.class, h));
    }

    public static Result addGroup() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        if (userId != null) {
            DBSUserGroup dbUG = new DBSUserGroup();
            dbUG.user = DBCUser.findById(userId);
            dbUG.name = "Nova grupa";
            dbUG.save();
        }
        return showData(userId);
    }

    public static Result groupSave(Long groupId) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();
        HInput hN = HTagUtil.getElementById(hdc, "group_name_" + groupId, HInput.class);
        HTextarea hD = HTagUtil.getElementById(hdc, "group_description_" + groupId, HTextarea.class);

        log.trace("update group id: %d, name: %s, description: %s", groupId, hN.getAttrValue(), hD.getAttrValue());
        DBSUserGroup dbUG = DBSUserGroup.findById(groupId);
        if (dbUG != null) {
            dbUG.name = hN.getAttrValue();
            dbUG.description = hD.getAttrValue();
            dbUG.save();
        }

        HData data = new HData("data");
        data.addValue("msg", "Podaci uspješno spremljeni");

        return ok(data.getJsonString());
    }

    public static Result groupDelete(Long groupId) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();
        
        log.trace("delete group id: %d", groupId);
        DBSUserGroup dbUG = DBSUserGroup.findById(groupId);
        boolean isDeleted = false;
        if (dbUG != null) {
            dbUG.delete();
            isDeleted = true;
        }
        
        HData data = new HData("data");
        data.addValue("isDeleted", isDeleted);
        
        return ok(data.getJsonString());
    }
    
    public static Result loadGroupSensors() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();
        
        HDataContainer hdc = CallController.getRequestData();
        
        // Load request data
        HInput hGroupId = HTagUtil.getElementById(hdc, "group_id", HInput.class);
        log.debug("Load group %s sensor", hGroupId.getAttrValue());
        
        if (HUtil.isSet(hGroupId, "value")) {
            Long groupId = Long.parseLong(hGroupId.getAttrValue());
            DBSUserGroup dbG = DBSUserGroup.findById(groupId);
            
            if (userId == null)
                userId = dbG.user.id;
            
            List<HData> lst = HUtil.getAllHData(hdc, "id", "sensor___.*");
            List<DBSUserSensorGroup> lSUG = DBSUserSensorGroup.findByGroup(groupId);
            
            for (HData hd : lst) {
                HInput hInput = (HInput) hd;
                hInput.setAttrChecked(false);
                
                if (hInput.getType().getValue() == HInput.InputTagType.checkbox) {
                    String[] tmp = hd.get("id").split("___");
                    
                    Long sensorId = Long.parseLong(tmp[1]);
                    String tableName = tmp[3];
                    String columnName = tmp[4];
                    
                    DBSUserSensor dbS = DBSUserSensor.findByFK(userId, sensorId, tableName, columnName);
                    
                    if (dbS != null) {
                        for(DBSUserSensorGroup dbSUG : lSUG) {
                            if (dbSUG.userSensor.id == dbS.id) {
                                hInput.setAttrChecked(true);
                                break;
                            }
                        }
                    
                    }
                    
                }
            }
        }
        
        return ok(hdc.getJson());
    }
    public static Result saveGroupSensors() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();

        // Load request data
        HInput hGroupId = HTagUtil.getElementById(hdc, "group_id", HInput.class);
        log.debug("Save group %s sensor", hGroupId.getAttrValue());

        if (HUtil.isSet(hGroupId, "value")) {
            Long groupId = Long.parseLong(hGroupId.getAttrValue());
            DBSUserGroup dbG = DBSUserGroup.findById(groupId);

            if (userId == null)
                userId = dbG.user.id;
            
            // delete all sensor in user group
            List<DBSUserSensorGroup> lusg = DBSUserSensorGroup.findByGroup(groupId);
            for(DBSUserSensorGroup usg : lusg)
                usg.delete();
            
            List<HData> lst = HUtil.getAllHData(hdc, "id", "sensor___.*");

            for (HData hd : lst) {
                HInput hInput = (HInput) hd;

                if (hInput.getType().getValue() == HInput.InputTagType.checkbox && hInput.getChecked().getValue() == true) {
                    String[] tmp = hd.get("id").split("___");

                    Long sensorId = Long.parseLong(tmp[1]);
                    String tableName = tmp[3];
                    String columnName = tmp[4];
                    
                    DBSUserSensor dbS = DBSUserSensor.findByFK(userId, sensorId, tableName, columnName);
                    if (dbS == null) {  // INSERT new user sensor 
                        DBSensor dbSen = DBSensor.findById(sensorId);
                        SensorInterface sin = dbSen.getSensorClass();
                        
                        SensorProperty sp = SensorProperty.getSensorProperty(sin, tableName, columnName);
                        
                        dbS = new DBSUserSensor();
                        dbS.user = DBCUser.findById(userId);
                        dbS.sensor = dbSen;
                        dbS.tableName = tableName;
                        dbS.tableColumn = columnName;
                        dbS.sensorClass = sin.getClass().getName();
                        dbS.label = sp.properties().label();
                        dbS.description = sp.properties().description();
                        dbS.save();
                    }

                    
                    // add sensor to user group
                    DBSUserSensorGroup dbSUG = new DBSUserSensorGroup();
                    dbSUG.userGroup = dbG;
                    dbSUG.userSensor = dbS;
                    dbSUG.save();

                    log.debug("Add sensor to group " + Arrays.asList(tmp).toString());
                }
            }
        }

        return ok(hdc.getJson());
    }

    public static Result loadSensorInfo(Long userGroupId, Long sensorId, String tableName, String columnName) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;

        HDataContainer hdc = CallController.getRequestData();
        DBSUserGroup dbG = DBSUserGroup.findById(userGroupId);

        if (dbG != null) {
            userId = dbG.user.id;

            DBSUserSensor dbS = DBSUserSensor.findByFK(userId, sensorId, tableName, columnName);

            if (dbS == null) {  // INSERT new user sensor
                DBSensor dbSen = DBSensor.findById(sensorId);
                SensorInterface sin = dbSen.getSensorClass();

                SensorProperty sp = SensorProperty.getSensorProperty(sin, tableName, columnName);

                dbS = new DBSUserSensor();
                dbS.user = DBCUser.findById(userId);
                dbS.sensor = dbSen;
                dbS.tableName = tableName;
                dbS.tableColumn = columnName;
                dbS.sensorClass = sin.getClass().getName();
                dbS.label = sp.properties().label();
                dbS.description = sp.properties().description();
                dbS.save();
            }

            HInput hUserSensorId = HTagUtil.getElementById(hdc, "sip_user_sensor_id", HInput.class);
            HInput hLabel = HTagUtil.getElementById(hdc, "sip_label", HInput.class);
            HTextarea hDesc = HTagUtil.getElementById(hdc, "sip_description", HTextarea.class);

            hUserSensorId.setAttrValue(dbS.id + "");
            hLabel.setAttrValue(dbS.label);
            hDesc.setAttrValue(dbS.description);
        }

        return ok(hdc.getJson());
    }

    public static Result saveSensorInfo() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();
        
        HDataContainer hdc = CallController.getRequestData();
        HInput h_sip_user_sensor_id = HTagUtil.getElementById(hdc, "sip_user_sensor_id", HInput.class);
        HInput h_sip_label = HTagUtil.getElementById(hdc, "sip_label", HInput.class);
        HTextarea h_sip_description = HTagUtil.getElementById(hdc, "sip_description", HTextarea.class);
        
        if (HUtil.isSet(h_sip_user_sensor_id, "value")) {
            Long userSensorId = Long.parseLong(h_sip_user_sensor_id.getAttrValue());
            DBSUserSensor dbUS = DBSUserSensor.findById(userSensorId);
            if (dbUS != null) {
                String label = h_sip_label.getAttrValue();
                if (label!=null && label.length()!=0)
                    dbUS.label = label;
                
                dbUS.description = h_sip_description.getAttrValue();
                dbUS.save();

                HLabel hLabel = new HLabel("lbl_sensor_" + dbUS.sensor.id);
                hLabel.set(label);
                hdc.add(hLabel);
            }
        }
        
        
        return ok(hdc.getJson());
    }
    
}
