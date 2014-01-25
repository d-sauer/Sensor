package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataWetness;
import module.sensor.models.DBSensorDataWind;
import module.sensor.models.DBSensorDataWindHighLow;
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

public class WeatherLink_Wind extends Sensor {

    public static final String version = "1.0";
    public static final String name = "Temperature";
    public static final String sensorId = "temperature";

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "windSpeed", tableName = "s_data_winds", columnName = "wind_speed")
    public SensorProperty propColWindSpeed = new SensorProperty("col_wind_speed").properties().label("Wind speed")
                                                                                              .description("Brzina vjetra (Wind direction)")
                                                                                              .defaultValue("7")
                                                                                              .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "windRun", tableName = "s_data_winds", columnName = "wind_run")
    public SensorProperty propColWindRun = new SensorProperty("col_wind_run").properties().label("Wind run")
                                                                                            .description("Wind run")
                                                                                            .defaultValue("9")
                                                                                            .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "direction", tableName = "s_data_winds", columnName = "direction")
    public SensorProperty propColWindDirection = new SensorProperty("col_wind_dir").properties().label("Smjer vjetra")
                                                                                          .description("Smjer vjetra (Wind direction)")
                                                                                          .defaultValue("8")
                                                                                          .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "directionDegree", tableName = "s_data_winds", columnName = "direction_degree")
    public SensorProperty propColWindDirDegree = new SensorProperty("col_wind_dir_degree").properties().label("Wind direction degree")
                                                                                                        .description("Smjer vjetra u stupnjevima")
                                                                                                        .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "windChill", tableName = "s_data_winds", columnName = "wind_chill")
    public SensorProperty propColWindChill = new SensorProperty("col_wind_chill").properties().label("Wind chill")
                                                                                                .defaultValue("12")
                                                                                                .description("Wind chill")
                                                                                                .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "windSample", tableName = "s_data_winds", columnName = "wind_sample")
    public SensorProperty propColWindSample = new SensorProperty("col_wind_sample").properties().label("Wind sample")
                                                                                                .defaultValue("32")
                                                                                                .description("Wind sample")
                                                                                                .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWind.class, modelClassColumnField = "windTx", tableName = "s_data_winds", columnName = "wind_tx")
    public SensorProperty propColWindTx = new SensorProperty("col_wind_tx").properties().label("Wind TX")
                                                                                        .defaultValue("33")
                                                                                        .description("Wind TX")
                                                                                        .get().registerProperty(sensorProperty);
    
    public SensorProperty propColWindOlderOf = new SensorProperty("col_wind_older_of").properties().label("Wind older of (sec)")                                                                                                   
                                                                                                    .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                    .get().registerProperty(sensorProperty);

    
    
    // --
    
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "highSpeed", tableName = "s_data_winds_high_low", columnName = "high_speed")
    public SensorProperty propColWindHighSpeed = new SensorProperty("col_wind_hi_speed").properties().label("Wind high speed")
                                                                                                   .description("Maksimalna brzina vjetra")
                                                                                                   .defaultValue("10")
                                                                                                   .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "highSpeedDirection", tableName = "s_data_winds_high_low", columnName = "high_speed_direction")
    public SensorProperty propColWindHighSpeedDirection = new SensorProperty("col_wind_hi_speed_dir").properties().label("Wind high speed direction")
                                                                                                                .description("Smjer maksimalne brzine vjetra")
                                                                                                                .defaultValue("10")
                                                                                                                .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "highSpeedDirectionDegree", tableName = "s_data_winds_high_low", columnName = "high_speed_direction_degree")
    public SensorProperty propColWindHighSpeedDirDegree = new SensorProperty("col_wind_hi_speed_dir_degree").properties().label("Wind high speed")
                                                                                                                .description("Maksimalna brzina vjetra")
                                                                                                                .defaultValue("11")
                                                                                                                .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "lowSpeed", tableName = "s_data_winds_high_low", columnName = "low_speed")
    public SensorProperty propColWindLowSpeed = new SensorProperty("col_wind_low_speed").properties().label("Wind low speed")
                                                                                                    .description("Minimalna brzina vjetra")
                                                                                                    .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "lowSpeedDirection", tableName = "s_data_winds_high_low", columnName = "low_speed_direction")
    public SensorProperty propColWindLowSpeedDirection = new SensorProperty("col_wind_low_speed_dir").properties().label("Wind low speed")
                                                                                                            .description("Minimalna brzina vjetra")
                                                                                                            .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWindHighLow.class, modelClassColumnField = "lowSpeedDirectionDegree", tableName = "s_data_winds_high_low", columnName = "low_speed_direction_degree")
    public SensorProperty propColWindLowSpeedDirDegree = new SensorProperty("col_wind_low_dir_degree").properties().label("Wind low speed")
                                                                                                            .description("Maksimalna brzina vjetra")
                                                                                                            .get().registerProperty(sensorProperty);
    
    public SensorProperty propColWindHighLowOlderOf = new SensorProperty("col_wind_high_low_older_of").properties().label("Cool older of (sec)")                                                                                                   
                                                                                                        .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                        .get().registerProperty(sensorProperty);


    
    
    
    

    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Wind";
    }

    @Override
    public String getDescription() {
        return "Sensor za vjetar";
    }

    @Override
    public void setProperty(String property, String value) {
        SensorProperty.setProperty(sensorProperty, property, value);
    }


    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Wind: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double windSpeed = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindSpeed));
        Double windRun = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindRun));
        String windDirection = WeatherLinkParseFile.getColumn(lineColumn, propColWindDirection);
        Integer windDirectionDegree = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindDirDegree));
        Double windChill = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindChill));
        Double windSample = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindSample));
        Double windTx = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindTx));

        Integer windOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindOlderOf));
        
        //--
        
        Double windHighSpeed = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindHighSpeed));
        String windHighSpeedDirection = WeatherLinkParseFile.getColumn(lineColumn, propColWindHighSpeedDirection);
        Integer windHighSpeedDirectionDegree = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindHighSpeedDirDegree));
        Double windLowSpeed = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindLowSpeed));
        String windLowSpeedDirection = WeatherLinkParseFile.getColumn(lineColumn, propColWindLowSpeedDirection);
        Integer windLowSpeedDirectionDegree = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindLowSpeedDirDegree));

        Integer windHighLowOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWindHighLowOlderOf));

        
        if (ValidationUtils.hasNoNull(windSpeed, windRun, windDirection, windDirectionDegree, windChill, windSample, windTx)) {
            DBSensorDataWind sWind = new DBSensorDataWind();
            sWind.sensorData = usd;
            sWind.sensor = sensor;
            sWind.dateTime = new java.sql.Timestamp(date.getTime());
            
            sWind.windSpeed = windSpeed;
            sWind.windRun = windRun;
            sWind.direction = windDirection;
            sWind.directionDegree = windDirectionDegree;
            sWind.windChill = windChill;
            sWind.windSample = windSample;
            sWind.windTx = windTx;
            
            sWind.save();
            isWrite = true;
        }

        if (ValidationUtils.hasNoNull(windHighSpeed, windHighSpeedDirection, windHighSpeedDirectionDegree, windLowSpeed, windLowSpeedDirection, windLowSpeedDirectionDegree)) {
            DBSensorDataWindHighLow sWindHL = new DBSensorDataWindHighLow();
            sWindHL.sensorData = usd;
            sWindHL.sensor = sensor;
            sWindHL.dateTime = new java.sql.Timestamp(date.getTime());
            
            sWindHL.highSpeed = windHighSpeed;
            sWindHL.highSpeedDirection = windHighSpeedDirection;
            sWindHL.highSpeedDirectionDegree = windHighSpeedDirectionDegree;
            sWindHL.lowSpeed = windLowSpeed;
            sWindHL.lowSpeedDirection = windLowSpeedDirection;
            sWindHL.lowSpeedDirectionDegree = windLowSpeedDirectionDegree;
            
            sWindHL.save();
            isWrite = true;
        }
        
        
        log.debug("Wind: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataWind> lst = null;
        lst = DBSensorDataWind.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsTemp = mlchart.addDataSetWithTick("Speed", "#" + Color.REDs.DarkRed, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Speed";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Speed");  
        
        for (DBSensorDataWind dd : lst) {
            MultiLineChartDataSetValue hSpeed = dsTemp.addValueAndTick(dd.dateTime.getTime(), dd.windSpeed);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.windSpeed);
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
