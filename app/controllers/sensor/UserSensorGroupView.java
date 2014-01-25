package controllers.sensor;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import module.core.models.DBCUser;
import module.sensor.models.DBSUserGroup;
import module.sensor.models.DBSUserSensor;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.NumberUtils;
import util.data.WDate;
import util.db.EbeanUtils;
import util.logger.log;
import util.session.SessionData;
import controllers.Application;
import controllers.ModuleController;

public class UserSensorGroupView extends Controller {

    public static class UGroupSensors {
        public DBSUserGroup group;
        public List<USensor> sensors = new ArrayList<UserSensorGroupView.USensor>();
        public boolean newLine = false;
    }

    public static class USensor {
        public DBSUserSensor sensor;
        private WDate ocitanje = new WDate("dd.MM.yyyy HH:mm");
        private String value;

        public void setOcitanje(Long miliseconds, String value) {
            ocitanje.set(miliseconds);
            this.value = value;
        }

        public String datum() {
            return ocitanje.getFormatted();
        }

        public String value() {
            Double d = NumberUtils.doubleOf(value);

            if (d != null) {
                DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                return df.format(d);
            } else {
                Long l = NumberUtils.longOf(value);
                if (l != null) {
                    DecimalFormat df = new DecimalFormat("#,###,###,###");
                    return df.format(l);
                }
            }

            if (value != null)
                return value;
            else
                return " - ";
        }
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

    public static Result showData(Long userId, boolean isAdmin) throws Exception {
        // Ako je admin dohvati popis korinsika za edit..
        List<DBCUser> dbu = null;
        if (isAdmin) {
            userId = Application.getAdminSelUser();
            dbu = DBCUser.find.all();
        }

        // dohvati popis grupa korinsika
        List<DBSUserGroup> lUG = DBSUserGroup.findByUser(userId);

        List<UGroupSensors> lstGS = new ArrayList<UserSensorGroupView.UGroupSensors>();
        int count = 0;
        for (DBSUserGroup dbUG : lUG) {
            if (dbUG == null)
                continue;

            UGroupSensors gs = new UGroupSensors();
            lstGS.add(gs);

            // Grupa
            gs.group = dbUG;
            gs.newLine = (++count % 2 == 0 ? true : false);

            // senzori grupe
            List<DBSUserSensor> lstUS = dbUG.getSensors();
            for (DBSUserSensor dbUS : lstUS) {
                if (dbUS == null)
                    continue;

                USensor us = new USensor();
                gs.sensors.add(us);

                us.sensor = dbUS;

                // podaci o citanja
                String sql = "SELECT date_time, " + dbUS.tableColumn + " FROM " + dbUS.tableName + " ORDER BY id DESC";
                ResultSet rs = EbeanUtils.executeQuery(sql);
                if (rs.next()) {
                    java.sql.Timestamp time = rs.getTimestamp("date_time");
                    String value = rs.getString(dbUS.tableColumn);

                    us.setOcitanje(time.getTime(), value);
                }
                rs.close();

            }

        }

        Html h = views.html.sensor.UserSensorGroupView.render(lstGS, dbu, Application.getAdminSelUser());
        return ok(ModuleController.result(UserSensorGroupView.class, h));
    }

}
