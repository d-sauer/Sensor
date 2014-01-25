package controllers.sensor;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import module.core.models.DBCUser;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.Sensor;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_DegreeDay;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Density;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_EMC;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Humidity;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Moisture;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Pressure;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Rain;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Temperature;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Wetness;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink_Wind;

import org.codehaus.jackson.node.ObjectNode;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.DateUtils;
import util.NumberUtils;
import util.StringUtils;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HInput;
import util.hlib.html.HSelect;
import util.logger.log;
import util.session.SessionData;
import controllers.ModuleController;

public class AllSensorDataView extends Controller {

    public static Result refresh() throws Exception {
        return loadModule();
    }
    
    public static Result loadModule() throws Exception {
        SessionData sd = SessionData.get();

        if (sd != null) {
            if (sd.isAdmin()) {
                log.debug("User admin");

                HDataContainer hdc = CallController.getRequestData();
                HSelect hs = HTagUtil.getElementById(hdc, "admin_select_user", HSelect.class);

                if (hs != null) {
                    Long userId = NumberUtils.longOf(hs.getSelectedValue());
                    log.trace("select user: %s", userId);
                    return showData(userId, true);
                }
                else
                    return showData(null, true);
            } else {
                log.debug("User ID: %d", sd.getUserId());
                return showData(sd.getUserId(), false);
            }
        } else {
            log.warning("session not found");
            return redirect(controllers.routes.Application.index());
        }
    }

    public static Result showData(Long userId, boolean isAdmin) throws Exception {
        List<DBCUser> dbu = null;
        if (isAdmin)
            dbu = DBCUser.find.all();

        // user sensor group
        List<DBSensorGroup> lSG = null;
        if (userId != null)
            lSG = DBSensorGroup.findByUser(userId);

        Html h = views.html.sensor.AllSensorDataView.render(lSG, dbu, userId);
        return ok(ModuleController.result(AllSensorDataView.class, h));
    }
    
    
    public static Result getSensorData() throws Exception {
        HDataContainer hdc = CallController.getRequestData();
        HSelect selSensor = HTagUtil.getElementById(hdc, "sensor", HSelect.class);
        HInput iDateFrom = HTagUtil.getElementById(hdc, "date_from", HInput.class);
        HInput iDateTo = HTagUtil.getElementById(hdc, "date_to", HInput.class);
        DBSensor sensor = null;
        AllDataView adv = null;
        
        //
        // Datumski period
        //
        Date dateFrom = new Date();
        Date dateTo = new Date();
        
        if(StringUtils.isNotBlank(iDateFrom.getValue().getValue()) && StringUtils.isNotBlank(iDateTo.getValue().getValue())) {
            dateFrom = DateUtils.getDate(iDateFrom.getValue().getValue(), "dd.MM.yyyy");
            dateTo = DateUtils.getDate(iDateTo.getValue().getValue(), "dd.MM.yyyy");
        }
        else if(StringUtils.isNotBlank(iDateFrom.getValue().getValue()) && !StringUtils.isNotBlank(iDateTo.getValue().getValue())) {
            dateFrom = DateUtils.getDate(iDateFrom.getValue().getValue(), "dd.MM.yyyy");
            
            Calendar cTo = Calendar.getInstance();
            cTo.setTime(dateFrom);
            
            cTo.add(Calendar.DATE, 7); // +7 dana
            dateTo.setTime(cTo.getTimeInMillis());
        }
        else if(!StringUtils.isNotBlank(iDateFrom.getValue().getValue()) && StringUtils.isNotBlank(iDateTo.getValue().getValue())) {
            dateTo = DateUtils.getDate(iDateTo.getValue().getValue(), "dd.MM.yyyy");
            
            Calendar cTo = Calendar.getInstance();
            cTo.setTime(dateTo);
            
            cTo.add(Calendar.DATE, -7); // -7 dana
            dateFrom.setTime(cTo.getTimeInMillis());
        } 
        else {
            Calendar cTo = Calendar.getInstance();
            cTo.setTime(new Date());
            dateTo.setTime(cTo.getTimeInMillis());
            
            cTo.add(Calendar.DATE, -7); // -7 dana
            dateFrom.setTime(cTo.getTimeInMillis());
        }
        
        
        //
        // Dohvat podataka grafova
        //
        if (selSensor!=null) {
            Long sensorId = NumberUtils.longOf(selSensor.getSelectedValue());
            sensor = DBSensor.findById(sensorId);
            
            Sensor s = (Sensor) sensor.getSensorClass();
            
            if (s instanceof WeatherLink_DegreeDay) {
                adv = ((WeatherLink_DegreeDay)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Density) {
                adv = ((WeatherLink_Density)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_EMC) {
                adv = ((WeatherLink_EMC)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Humidity) {
                adv = ((WeatherLink_Humidity)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Moisture) {
                adv = ((WeatherLink_Moisture)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Pressure) {
                adv = ((WeatherLink_Pressure)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Rain) {
                adv = ((WeatherLink_Rain)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Temperature) {
                adv = ((WeatherLink_Temperature)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Wetness) {
                adv = ((WeatherLink_Wetness)s).getData(sensor, dateFrom, dateTo);
            }
            else if (s instanceof WeatherLink_Wind) {
                adv = ((WeatherLink_Wind)s).getData(sensor, dateFrom, dateTo);
            }
        }
        
        HData data = new HData("chart");
        data.addCustomJson("multiline", adv.jsonGraf);
        
        log.trace("hdata :: %s", data!=null ? data.toString() : null);
        Html h = views.html.sensor.AllSensorDataView_data.render(sensor, null, adv.grid);
        return ok(ModuleController.result(AllSensorDataView.class, "#ajax_sensor_data", h, data));
    }
    
    public static class AllDataView {
        public ObjectNode jsonGraf;
        public AllDataGrid grid;
    }
    
    public static class AllDataGrid {
        public List<String> header = new LinkedList<String>();
        public List<List<String>> rows = new LinkedList<List<String>>();
        
        public void addHeader(String ... headerTitle) {
            for(String title : headerTitle)
                header.add(title);
        }
        
        public void insertRow(Object ... values) {
            List<String> row = new LinkedList<String>();
            addRow(row, true, values);
        }
        
        public void addRow(List<String> row, boolean insert, Object ... values) {
            if (row == null)
                row = new LinkedList<String>();
            
            for(Object value : values) {
                if (value == null)
                    row.add("");
                else if (value instanceof String)
                    row.add((String)value);
                else if (value instanceof Double) {
                    Double d = (Double)value;
                    row.add(d.toString());
                }
                else if (value instanceof Integer) {
                    Integer d = (Integer)value;
                    row.add(d.toString());
                } else {
                    row.add("-?-");
                }
            }

            if(insert)
                rows.add(row);
        }
        
        
        
        public List<String> getRowWith(String matchValue, int columnFromZero) {
            List<String> _row = null;
            
            for(List<String> row : rows) {
                String value = row.get(columnFromZero);
                if (matchValue.equals(value)) {
                    _row = row;
                    break;
                }
            }
            
            return _row;
        }
        
    }
}
