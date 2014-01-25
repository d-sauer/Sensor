package controllers.sensor;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.core.models.DBCUser;
import module.sensor.CronSceduleRule;
import module.sensor.models.DBSUserSensor;
import module.sensor.models.DBSUserSmsCron;
import module.sensor.models.DBSUserSmsCronSensors;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.AvailableSensor;
import module.sensor.sensor.AvailableSensorsData;
import module.sensor.sensor.SensorInterface;
import module.sensor.sensor.SensorProperty;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.StringUtils;
import util.Http.CallController;
import util.data.WDate;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.HTagUtil;
import util.hlib.html.HInput;
import util.hlib.html.HLabel;
import util.hlib.html.HSelect;
import util.hlib.html.HUtil;
import util.logger.log;
import util.session.SessionData;
import controllers.Application;
import controllers.ModuleController;
import controllers.sensor.UserSensorGroupManage.uSensorGroup;

public class UserSMSInfo extends Controller {

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
        List<DBSUserSmsCron> lUG = DBSUserSmsCron.findByUser(userId);

        //
        // Dohvati popis senzora koji mogu biti u grupama za korisnika
        //
        Set<uSensorGroup> sGroup = new HashSet<UserSensorGroupManage.uSensorGroup>();
        HashMap<Long, uSensorGroup> hmSG = new HashMap<Long, UserSensorGroupManage.uSensorGroup>();

        if (userId != null) {
            AvailableSensorsData asd = new AvailableSensorsData(userId);
            Set<AvailableSensor> sas = asd.getAvailableSensorData();

            for (AvailableSensor as : sas) {
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

        Html h = views.html.sensor.UserSMSInfo.render(lUG, dbu, Application.getAdminSelUser(), sGroup);
        return ok(ModuleController.result(UserSMSInfo.class, h));
    }

    public static Result addCron() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        if (userId != null) {
            DBSUserSmsCron dbUC = new DBSUserSmsCron();
            dbUC.user = DBCUser.findById(userId);
            dbUC.save();
        }
        return showData(userId);
    }

    public static Result cronSave(Long cronId) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();
        HInput hN = HTagUtil.getElementById(hdc, "cron_name_" + cronId, HInput.class);

        DBSUserSmsCron dbUC = DBSUserSmsCron.findById(cronId);
        if (dbUC != null) {
            // br mobitela
            Set<String> mobiles = new HashSet<String>();
            for(HData hd : hdc.get()) {
                HDataValue<String> hdv = hd.getValue("id");
                if (hdv != null && hdv.getValue().equals("mobitel")) {
                    HDataValue<String> mv = hd.getValue("value");
                    if (mv!=null && mv.getValue().trim().length()!=0) {
                        mobiles.add(mv.getValue().trim());
                    }
                }
            }
            
            // postavke crona
            HSelect hDan = HTagUtil.getElementById(hdc, "dan", HSelect.class);
            HSelect hMjesec = HTagUtil.getElementById(hdc, "mjesec", HSelect.class);
            HSelect hGodina = HTagUtil.getElementById(hdc, "godina", HSelect.class);
            HSelect hSat = HTagUtil.getElementById(hdc, "sat", HSelect.class);
            HSelect hMinuta = HTagUtil.getElementById(hdc, "minuta", HSelect.class);

            log.debug("dan: %s, mjesec: %s, godina: %s, sat: %s, minuta: %s", hDan.getSelectedValue(), hMjesec.getSelectedValue(), hGodina.getSelectedValue(), hSat.getSelectedValue(), hMinuta.getSelectedValue());

            CronSceduleRule csr = new CronSceduleRule();
            csr.dan = hDan.getSelectedValue();
            csr.mjesec = hMjesec.getSelectedValue();
            csr.godina = hGodina.getSelectedValue();
            csr.sat = hSat.getSelectedValue();
            csr.minuta = hMinuta.getSelectedValue();

            dbUC.mobileNumber = StringUtils.joinCollections(mobiles, ",");
            dbUC.sceduleRule = csr.getDescriptor();
            dbUC.nextScedule = new java.sql.Timestamp(csr.getNext().getTime());
            dbUC.save();
        }

        HData data = new HData("data");
        data.addValue("msg", "Podaci uspješno spremljeni");

        return ok(data.getJsonString());
    }

    public static Result cronCalculate(Long cronId) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();
        HInput hN = HTagUtil.getElementById(hdc, "cron_name_" + cronId, HInput.class);

        HSelect hDan = HTagUtil.getElementById(hdc, "dan", HSelect.class);
        HSelect hMjesec = HTagUtil.getElementById(hdc, "mjesec", HSelect.class);
        HSelect hGodina = HTagUtil.getElementById(hdc, "godina", HSelect.class);
        HSelect hSat = HTagUtil.getElementById(hdc, "sat", HSelect.class);
        HSelect hMinuta = HTagUtil.getElementById(hdc, "minuta", HSelect.class);

        CronSceduleRule csr = new CronSceduleRule();
        csr.dan = hDan.getSelectedValue();
        csr.mjesec = hMjesec.getSelectedValue();
        csr.godina = hGodina.getSelectedValue();
        csr.sat = hSat.getSelectedValue();
        csr.minuta = hMinuta.getSelectedValue();

        HData data = new HData("data");
        HLabel hNextScedule = new HLabel("next_scedule");
        Date datum = csr.getNext();
        WDate wdate = new WDate(datum);
        hNextScedule.set(wdate.getFormatted("dd.MM.yyyy HH:mm"));
        data.addNode(hNextScedule);
        
        return ok(data.getJsonString());
    }

    public static Result cronDelete(Long cronId) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        log.trace("delete cron id: %d", cronId);
        DBSUserSmsCron dbUG = DBSUserSmsCron.findById(cronId);
        boolean isDeleted = false;
        if (dbUG != null) {
            List<DBSUserSmsCronSensors> lcs = DBSUserSmsCronSensors.findByCronId(cronId);
            for(DBSUserSmsCronSensors cs : lcs)
                cs.delete();
            
            dbUG.delete();
            isDeleted = true;
        }

        HData data = new HData("data");
        data.addValue("isDeleted", isDeleted);

        return ok(data.getJsonString());
    }

    public static Result loadCronSensors() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();

        // Load request data
        HInput hGroupId = HTagUtil.getElementById(hdc, "cron_id", HInput.class);
        log.debug("Load cron %s sensor", hGroupId.getAttrValue());

        if (HUtil.isSet(hGroupId, "value")) {
            Long cronId = Long.parseLong(hGroupId.getAttrValue());
            DBSUserSmsCron dbC = DBSUserSmsCron.findById(cronId);

            if (userId == null)
                userId = dbC.user.id;

            List<HData> lst = HUtil.getAllHData(hdc, "id", "sensor___.*");
            List<DBSUserSmsCronSensors> lUCS = DBSUserSmsCronSensors.findByCronId(cronId);

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
                        for (DBSUserSmsCronSensors dbCS : lUCS) {
                            if (dbCS != null && dbCS.userSensor != null && dbCS.userSensor.id == dbS.id) {
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

    public static Result saveCronSensors() throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;
        if (sd.isAdmin())
            userId = Application.getAdminSelUser();
        else
            userId = sd.getUserId();

        HDataContainer hdc = CallController.getRequestData();

        // Load request data
        HInput hCronId = HTagUtil.getElementById(hdc, "cron_id", HInput.class);
        log.debug("Save cron %s sensor", hCronId.getAttrValue());

        if (HUtil.isSet(hCronId, "value")) {
            Long cronId = Long.parseLong(hCronId.getAttrValue());
            DBSUserSmsCron dbC = DBSUserSmsCron.findById(cronId);

            if (userId == null)
                userId = dbC.user.id;

            // delete all sensor in user group
            List<DBSUserSmsCronSensors> lusg = DBSUserSmsCronSensors.findByCronId(cronId);
            for (DBSUserSmsCronSensors usg : lusg)
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
                    // insert cron sensor
                    if (dbS != null) {
                        DBSUserSmsCronSensors dbCS = new DBSUserSmsCronSensors();
                        dbCS.smsCron = dbC;
                        dbCS.userSensor = dbS;
                        dbCS.save();
                    }
                }
            }
        }

        return ok(hdc.getJson());
    }

    public static Result loadSensorInfo(Long cronId, Long sensorId, String tableName, String columnName) throws Exception {
        SessionData sd = SessionData.get();
        Long userId = null;

        HDataContainer hdc = CallController.getRequestData();
        DBSUserSmsCron dbC = DBSUserSmsCron.findById(cronId);

        if (dbC != null) {
            userId = dbC.user.id;

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
            HLabel hLabel = new HLabel("sip_label");
            hLabel.set(dbS.label);
            HLabel hDesc = new HLabel("sip_description");
            hDesc.set(dbS.description);

            hdc.add(hLabel, hDesc);
            hUserSensorId.setAttrValue(dbS.id + "");
        }

        return ok(hdc.getJson());
    }

}
