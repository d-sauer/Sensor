package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataMoisture;
import module.sensor.sensor.Sensor;
import module.sensor.sensor.SensorProperty;
import module.sensor.sensor.SensorPropertyDbDescription;
import util.Color;
import util.NumberUtils;
import util.ValidationUtils;
import util.data.WDate;
import util.logger.log;
import controllers.sensor.AllSensorDataView;
import controllers.sensor.AllSensorDataView.AllDataGrid;
import controllers.sensor.AllSensorDataView.AllDataView;

public class WeatherLink_Moisture extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataMoisture.class, modelClassColumnField = "moisture", tableName = "s_data_moistures", columnName = "moisture")
    public SensorProperty propColMoist = new SensorProperty("col_moist").properties().label("Moisture")
                                                                              .defaultValue("28")
                                                                              .description("Moisture \n Količina vlage u određenoj supstanci (col: 28/29)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColMoistOlderOf = new SensorProperty("col_moist_older_of").properties().label("Moisture older of (sec)")                                                                                                   
                                                                                        .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                        .get().registerProperty(sensorProperty);



    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Vlaga materijala (moisture)";
    }

    @Override
    public String getDescription() {
        return "Senzor vlage u nekom materijalu";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Moisture: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double moisture = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColMoist));
        Integer moistureOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColMoistOlderOf));

        if (ValidationUtils.hasNoNull(moisture)) {
            DBSensorDataMoisture sMoisture = new DBSensorDataMoisture();
            sMoisture.sensorData = usd;
            sMoisture.sensor = sensor;
            sMoisture.dateTime = new java.sql.Timestamp(date.getTime());
            
            sMoisture.moisture = moisture;
            
            sMoisture.save();
            isWrite = true;
        } 
        
        log.debug("Moisture: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    
    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataMoisture> lst = null;
        lst = DBSensorDataMoisture.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("moisture", "#" + Color.GREENs.DarkGreen, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Moisture";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Moisture"); 
        
        for (DBSensorDataMoisture dd : lst) {
            MultiLineChartDataSetValue hM = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.moisture);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.moisture);
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
