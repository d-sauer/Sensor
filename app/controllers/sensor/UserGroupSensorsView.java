package controllers.sensor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import module.sensor.models.DBSUserGroup;
import module.sensor.models.DBSUserSensor;
import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.StringUtils;
import util.Http.CallController;
import util.data.WDate;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HDiv;
import util.hlib.html.HInput;
import util.logger.log;
import util.session.SessionData;
import controllers.ModuleController;
import controllers.sensor.AllSensorDataView.AllDataView;

public class UserGroupSensorsView extends Controller {

    public static Result refresh(Long groupId) throws Exception {
        return loadModule(groupId);
    }

    public static Result loadModule(Long groupId) throws Exception {
        return loadModule(groupId, null);
    }

    public static Result loadModule(Long groupId, Long sensorId) throws Exception {
        SessionData sd = SessionData.get();

        if (sd != null) {
            log.debug("User ID: %d", sd.getUserId());
            return showData(sd.getUserId(), groupId, sensorId);
        } else {
            log.warning("session not found");
            return redirect(controllers.routes.Application.index());
        }
    }

    private static Result showData(Long userId, Long groupId, Long sensorId) throws Exception {
        HDataContainer hdc = CallController.getRequestData();

        HDiv hDatePicker = new HDiv(hdc, "sensor_description");
        HDiv hSensorTitle = new HDiv(hdc, "sensor_title");
        HDiv hSensorDescription = new HDiv(hdc, "sensor_description");
        
        HInput hDateFrom = HTagUtil.getElementById(hdc, "date_from", HInput.class);
        HInput hDateTo = HTagUtil.getElementById(hdc, "date_to", HInput.class);

        //
        // Grupa
        //
        DBSUserGroup dbG = DBSUserGroup.findById(groupId);
        List<DBSUserSensor> lstS = dbG.getSensors();

        //
        // Podaci o senzoru
        //
        if (sensorId == null) {
            HInput hSensorId = HTagUtil.getElementById(hdc, "group_sensor_id", HInput.class);
            if (hSensorId != null && hSensorId.getAttrValue().length() != 0)
                sensorId = Long.parseLong(hSensorId.getAttrValue());
        }

        if (sensorId != null) {
            DBSUserSensor dbUS = DBSUserSensor.findById(sensorId);
            hSensorTitle.setInnerHTML(dbUS.label);
            hSensorDescription.setInnerHTML(dbUS.description);
            hSensorDescription.setStyle("display: block");
            hDatePicker.setStyle("display: block");
        } else {
            hSensorTitle.setInnerHTML("");
            hSensorDescription.setInnerHTML("");
            hSensorDescription.setStyle("display: none");
            hDatePicker.setStyle("display: none");
        }

        Html h = views.html.sensor.UserGroupSensorsView.render(dbG, lstS, sensorId);
        
        return ok(ModuleController.result(UserGroupSensorsView.class, h, hDateFrom, hDateTo));
    }

    public static Result showGraph() throws Exception {
        HDataContainer hdc = CallController.getRequestData();

        HInput hGroupId = HTagUtil.getElementById(hdc, "group_id", HInput.class);
        HDiv hDivSensorData = new HDiv(hdc, "sensor_data");
        HDiv hSensorTitle = new HDiv(hdc, "sensor_title");
        HDiv hSensorDescription = new HDiv(hdc, "sensor_description");
        HInput hDateFrom = HTagUtil.getElementById(hdc, "date_from", HInput.class);
        HInput hDateTo = HTagUtil.getElementById(hdc, "date_to", HInput.class);
        
        //
        // Podaci o senzoru
        //
        Long sensorId = null;
        HInput hSensorId = HTagUtil.getElementById(hdc, "group_sensor_id", HInput.class);
        if (hSensorId != null && hSensorId.getAttrValue().length() != 0)
            sensorId = Long.parseLong(hSensorId.getAttrValue());

        AllDataView adv = null;
        Html hTable = null;
        HData chartData = null;
        
        if (sensorId != null) {         // SHOW data
            //
            // Podaci o senzoru
            //
            DBSUserSensor dbUS = DBSUserSensor.findById(sensorId);
            hSensorTitle.setInnerHTML(dbUS.label);
            hSensorDescription.setInnerHTML(dbUS.description);
            hSensorDescription.setStyle("display: block");
            hDivSensorData.setStyle("display: block");
            
            //
            // Datumski period
            //
            WDate dateFrom = new WDate(WDate.ddMMyyyy);
            WDate dateTo = new WDate(WDate.ddMMyyyy);
            
            if(StringUtils.isNotBlank(hDateFrom.getValue().getValue()) && StringUtils.isNotBlank(hDateTo.getValue().getValue())) {
                dateFrom.set(hDateFrom.getAttrValue());
                dateTo.set(hDateTo.getAttrValue());
            }
            else if(StringUtils.isNotBlank(hDateFrom.getValue().getValue()) && !StringUtils.isNotBlank(hDateTo.getValue().getValue())) {
                dateFrom.set(hDateFrom.getAttrValue());
                dateTo.set(dateFrom);
                dateTo.add(Calendar.DATE, 7);
            }
            else if(!StringUtils.isNotBlank(hDateFrom.getValue().getValue()) && StringUtils.isNotBlank(hDateTo.getValue().getValue())) {
                dateTo.set(hDateTo.getAttrValue());
                
                dateFrom.set(dateTo);
                dateTo.add(Calendar.DATE, -7);
            } 
            else {
                dateTo.set(new Date());
                dateFrom.set(dateTo);
                dateFrom.add(Calendar.DATE, -7);
            }            

            hDateFrom.setAttrValue(dateFrom.getFormatted());
            hDateTo.setAttrValue(dateTo.getFormatted());
            
            adv = WeatherLink.getUserSensorData(dbUS, dateFrom, dateTo);
            hTable = views.html.sensor.UserGroupSensorsView_ajaxTable.render(dbUS, adv.grid);
            
            chartData = new HData("chart");
            chartData.addCustomJson("multiline", adv.jsonGraf);
            
        } else {                        // HIDE data
            hSensorTitle.setInnerHTML("");
            hSensorDescription.setInnerHTML("");
            hSensorDescription.setStyle("display: none");
            hDivSensorData.setStyle("display: none");
        }

        hdc.add(chartData);
        return ok(ModuleController.result(AllSensorDataView.class, "#ajax_data_table", hTable, hdc));
    }

}
