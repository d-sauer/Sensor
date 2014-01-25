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

public class WeatherLink_Wetness extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWetness.class, modelClassColumnField = "wetness", tableName = "s_data_wetness", columnName = "wetness")
    public SensorProperty propColWet = new SensorProperty("col_wet").properties().label("Vlaga")
                                                                                 .defaultValue("31")
                                                                                 .description("Vlaga lista")
                                                                                 .get().registerProperty(sensorProperty);

    public SensorProperty propColWetOlderOf = new SensorProperty("col_wet_older_of").properties().label("Wet older of (sec)")                                                                                                   
                                                                                                 .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                 .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass = DBSensorDataWetness.class, modelClassColumnField = "dry", tableName = "s_data_wetness", columnName = "dry")
    public SensorProperty propColDry = new SensorProperty("col_dry").properties().label("Dry")
                                                                                    .description("Dry")
                                                                                    .get().registerProperty(sensorProperty);
    
    public SensorProperty propColDryOlderOf = new SensorProperty("col_dry_older_of").properties().label("Dry older of (sec)")                                                                                                   
                                                                                                 .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                 .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWetness.class, modelClassColumnField = "dewPoint", tableName = "s_data_wetness", columnName = "dew_point")
    public SensorProperty propColDewPoint = new SensorProperty("col_dew_point").properties().label("Dew point")
                                                                                            .description("dew point")
                                                                                            .get().registerProperty(sensorProperty);
                                                                                    
    public SensorProperty propColDewPointOlderOf = new SensorProperty("col_dew_point_older_of").properties().label("Dew point older of (sec)")                                                                                                   
                                                                                                            .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                            .get().registerProperty(sensorProperty);
    
    @SensorPropertyDbDescription(modelClass = DBSensorDataWetness.class, modelClassColumnField = "precipitation", tableName = "s_data_wetness", columnName = "precipitation")
    public SensorProperty propColPrecipitation = new SensorProperty("col_precipitation").properties().label("Precipitation")
                                                                                                    .description("Precipitation")
                                                                                                    .get().registerProperty(sensorProperty);
    
    public SensorProperty propColPrecipitationOlderOf = new SensorProperty("col_precipitation_older_of").properties().label("Precipitation older of (sec)")                                                                                                   
                                                                                                                    .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                                    .get().registerProperty(sensorProperty);
    

    
    
    
    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Vlaga lista";
    }

    @Override
    public String getDescription() {
        return "Senzor vlage lista";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Wetness: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double wet = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColWet));
        Double dry = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColDry));
        Double dewPoint = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColDewPoint));
        Double precipitation = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColPrecipitation));

        Integer wetOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColWetOlderOf));
        Integer dryOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColDryOlderOf));
        Integer dewPointOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColDewPointOlderOf));
        Integer precipitationOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColPrecipitationOlderOf));

        if (ValidationUtils.hasNoNull(wet, dry, dewPoint, precipitation)) {
            DBSensorDataWetness sWet = new DBSensorDataWetness();
            sWet.sensorData = usd;
            sWet.sensor = sensor;
            sWet.dateTime = new java.sql.Timestamp(date.getTime());
            
            sWet.wetness = wet;
            sWet.dry = dry;
            sWet.dewPoint = dewPoint;
            sWet.precipitation = precipitation;
            
            sWet.save();
            isWrite = true;
        } 
        
        log.debug("Wetness: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }
    
    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataWetness> lst = null;
        lst = DBSensorDataWetness.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsDewPoint = mlchart.addDataSetWithTick("dew point", "#" + Color.BLUEs.CadetBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsDry = mlchart.addDataSetWithTick("dry", "#" + Color.ORANGEs.LightSalmon, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsPrec = mlchart.addDataSetWithTick("Precipitation", "#" + Color.BROWNs.Maroon, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsWet = mlchart.addDataSetWithTick("Wetness", "#" + Color.GREENs.DarkGreen, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Wetness";
        
   //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Dew point", "Dry", "Precipitation", "Wetness");  
        
        for (DBSensorDataWetness dd : lst) {
            MultiLineChartDataSetValue hDP = dsDewPoint.addValueAndTick(dd.dateTime.getTime(), dd.dewPoint);
            MultiLineChartDataSetValue hD = dsDry.addValueAndTick(dd.dateTime.getTime(), dd.dry);
            MultiLineChartDataSetValue hP = dsPrec.addValueAndTick(dd.dateTime.getTime(), dd.precipitation);
            MultiLineChartDataSetValue hW = dsWet.addValueAndTick(dd.dateTime.getTime(), dd.wetness);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.dewPoint, dd.dry, dd.precipitation, dd.wetness);
        }
        

        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
