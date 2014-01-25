package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataTemperature;
import module.sensor.models.DBSensorDataTemperatureHighLow;
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

public class WeatherLink_Temperature extends Sensor {

    public static final String version = "1.0";
    public static final String name = "Temperature";
    public static final String sensorId = "temperature";

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperature.class, modelClassColumnField = "temperature", tableName = "s_data_temperatures", columnName = "temperature")
    public SensorProperty propColTemp = new SensorProperty("col_temp").properties().label("Temperature")
                                                                                   .defaultValue("2")
                                                                                   .description("Temperatura (col: 2, 20, 26, 30)")
                                                                                   .get().registerProperty(sensorProperty);
    
    
    public SensorProperty propColTempOlderOf = new SensorProperty("col_temp_older_of").properties().label("Temperature older of (sec)")                                                                                                   
                                                                                                    .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                    .get().registerProperty(sensorProperty); 
   
    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperature.class, modelClassColumnField = "heat", tableName = "s_data_temperatures", columnName = "heat")
    public SensorProperty propColHeat = new SensorProperty("col_heat").properties().label("Heat")
                                                                                    .description("Heat")
                                                                                    .get().registerProperty(sensorProperty);


    public SensorProperty propColHeatOlderOf = new SensorProperty("col_heat_older_of").properties().label("Heat older of (sec)")                                                                                                   
                                                                                                     .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                     .get().registerProperty(sensorProperty); 

    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperature.class, modelClassColumnField = "heatIndex", tableName = "s_data_temperatures", columnName = "heat_index")
    public SensorProperty propColHeatIndex = new SensorProperty("col_heat_index").properties().label("Heat index")
                                                                                              .description("Heat")
                                                                                              .get().registerProperty(sensorProperty);
    
    
    public SensorProperty propColHeatIndexOlderOf = new SensorProperty("col_heat_index_older_of").properties().label("Heat Index older of (sec)")                                                                                                   
                                                                                                              .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                              .get().registerProperty(sensorProperty); 
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperature.class, modelClassColumnField = "thwIndex", tableName = "s_data_temperatures", columnName = "thw_index")
    public SensorProperty propColThwIndex = new SensorProperty("col_thw_index").properties().label("THW Index")
                                                                                            .description("temeprature humidity with index")
                                                                                            .get().registerProperty(sensorProperty);
    
    
    public SensorProperty propColThwIndexOlderOf = new SensorProperty("col_thw_index_older_of").properties().label("THW Index older of (sec)")                                                                                                   
                                                                                                            .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                            .get().registerProperty(sensorProperty); 
                                                                                                    

    // -------
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperatureHighLow.class, modelClassColumnField = "temperatureHigh", tableName = "s_data_temperatures_high_low", columnName = "temperature_high")
    public SensorProperty propColTempHigh = new SensorProperty("col_temp_high").properties().label("Temperature High")
                                                                                            .defaultValue("3")
                                                                                            .description("High Temperatura (col: 3)")
                                                                                            .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperatureHighLow.class, modelClassColumnField = "temperatureLow", tableName = "s_data_temperatures_high_low", columnName = "temperature_low")
    public SensorProperty propColTempLow = new SensorProperty("col_temp_low").properties().label("Temperature Low")
                                                                                            .defaultValue("4")
                                                                                            .description("Low Temperatura (col:4)")
                                                                                            .get().registerProperty(sensorProperty);
    
    
    public SensorProperty propColTempHighLowOlderOf = new SensorProperty("col_temp_high_low_older_of").properties().label("Temperature High/Low older of (sec)")                                                                                                   
                                                                                                        .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                        .get().registerProperty(sensorProperty); 
    

    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperatureHighLow.class, modelClassColumnField = "heatHigh", tableName = "s_data_temperatures_high_low", columnName = "heat_high")
    public SensorProperty propColHeatHigh = new SensorProperty("col_heat_high").properties().label("Heat High")
                                                                                            .description("High Heat")
                                                                                            .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataTemperatureHighLow.class, modelClassColumnField = "heatLow", tableName = "s_data_temperatures_high_low", columnName = "heat_low")
    public SensorProperty propColHeatLow = new SensorProperty("col_heat_low").properties().label("Heat Low")
                                                                                            .description("Low Heat")
                                                                                            .get().registerProperty(sensorProperty);
    
    
    public SensorProperty propColHeatHighLowOlderOf = new SensorProperty("col_heat_high_low_older_of").properties().label("Heat High/Low older of (sec)")                                                                                                   
                                                                                                                    .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                                    .get().registerProperty(sensorProperty); 
    
    
    
    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Temperature";
    }

    @Override
    public String getDescription() {
        return "Senzor temperature";
    }

    /**
     * Save one line of recieved data from FTP to database
     * 
     * @return
     * @throws Exception 
     */
    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Temperature: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double temp = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColTemp));
        Double heat = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeat));
        Double heatIndex = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatIndex));
        Double thwIndex = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColThwIndex));

        Integer tempOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColTempOlderOf));
        Integer heatOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatOlderOf));
        Integer heatIndexOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatIndexOlderOf));
        Integer thwIndexOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColThwIndexOlderOf));

        if (ValidationUtils.hasNoNull(temp, heat, heatIndex, thwIndex)) {
            DBSensorDataTemperature sTemp = new DBSensorDataTemperature();
            sTemp.sensorData = usd;
            sTemp.sensor = sensor;
            sTemp.dateTime = new java.sql.Timestamp(date.getTime());
            
            sTemp.temperature = temp;
            sTemp.heat = heat;
            sTemp.heatIndex = heatIndex;
            sTemp.thwIndex = thwIndex;

            sTemp.save();
            isWrite = true;
        } 
        
        
        //
        // High / Low
        //
        
        Double tempHigh = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColTempHigh));
        Double tempLow = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColTempLow));
        Double heatHigh = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatHigh));
        Double heatLow = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatLow));
        
        Integer tempHighLowOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColTempHighLowOlderOf));
        Integer heatHighLowOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatHighLowOlderOf));
        
        if (ValidationUtils.hasNoNull(tempHigh, tempLow, heatHigh, heatLow)) {
            DBSensorDataTemperatureHighLow sTemp = new DBSensorDataTemperatureHighLow();
            sTemp.sensorData = usd;
            sTemp.sensor = sensor;
            sTemp.dateTime = new java.sql.Timestamp(date.getTime());
            
            sTemp.temperatureHigh = tempHigh;
            sTemp.temperatureLow = tempLow;
            sTemp.heatHigh = heatHigh;
            sTemp.heatLow = heatLow;
            
            sTemp.save();
            isWrite = true;
        } 
        

        log.debug("Temperature: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }
    
    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        //
        // Temperature
        //
        List<DBSensorDataTemperature> lst = null;
        lst = DBSensorDataTemperature.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsTemp = mlchart.addDataSetWithTick("Temperature", "#" + Color.REDs.DarkRed, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("Heat", "#" + Color.BROWNs.Chocolate, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsHeatInd = mlchart.addDataSetWithTick("Heat index", "#" + Color.BROWNs.SandyBrown, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsThwInd = mlchart.addDataSetWithTick("THW index", "#" + Color.BROWNs.SandyBrown, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Temperature";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Temperature", "Heat", "Heat index", "THW index", "High heat", "Low heat");  
        
        for (DBSensorDataTemperature dd : lst) {
            MultiLineChartDataSetValue hTemp = dsTemp.addValueAndTick(dd.dateTime.getTime(), dd.temperature);
            MultiLineChartDataSetValue hHeat = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.heat);
            MultiLineChartDataSetValue hHeatInd = dsHeatInd.addValueAndTick(dd.dateTime.getTime(), dd.heatIndex);
            MultiLineChartDataSetValue hThwInd = dsThwInd.addValueAndTick(dd.dateTime.getTime(), dd.thwIndex);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.temperature, dd.heat, dd.heatIndex, dd.thwIndex);
        }
        

        //
        // Temperature HIGH/LOW
        //
        List<DBSensorDataTemperatureHighLow> lstHL = null;
        lstHL = DBSensorDataTemperatureHighLow.findByDatePeriod(sensor, dateFrom, dateTo);

        WDate prevTHL = new WDate(WDate.ddMMyyyy);
        datum = new WDate(WDate.ddMMyyyy);

        MultiLineChartDataSet dsHeatHigh = mlchart.addDataSetWithTick("High heat", "#" + Color.REDs.LightCoral, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsHeatLow = mlchart.addDataSetWithTick("Low heat", "#" + Color.BLUEs.LightBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        for (DBSensorDataTemperatureHighLow dd : lstHL) {
            MultiLineChartDataSetValue hHeatH = dsHeatHigh.addValueAndTick(dd.dateTime.getTime(), dd.heatLow);
            MultiLineChartDataSetValue hHeatL = dsHeatLow.addValueAndTick(dd.dateTime.getTime(), dd.heatLow);

            datum.set(dd.dateTime);
            List<String> row = grid.getRowWith(datum.getFormatted("dd.MM.yyyy HH:mm"), 0);
            grid.addRow(row, false, dd.heatHigh, dd.heatLow);
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }

}
