package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataRain;
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

public class WeatherLink_Rain extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataRain.class, modelClassColumnField = "rain", tableName = "s_data_rains", columnName = "rain")
    public SensorProperty propColRain = new SensorProperty("col_rain").properties().label("Rain")
                                                                              .defaultValue("16")
                                                                              .description("Rain \n Količina padalina (stupci 16)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColRainOlderOf = new SensorProperty("col_rain_older_of").properties().label("Rain older of (sec)")                                                                                                   
                                                                                        .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                        .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass=DBSensorDataRain.class, modelClassColumnField = "rainRate", tableName = "s_data_rains", columnName = "rain_rate")
    public SensorProperty propColRainRate = new SensorProperty("col_rain_rate").properties().label("Rain rate")
                                                                                            .defaultValue("17")
                                                                                            .description("Rain rate \n Proesječna količina padalina (col: 17)")
                                                                                            .get().registerProperty(sensorProperty);
    
    public SensorProperty propColRainRateOlderOf = new SensorProperty("col_rain_rate_older_of").properties().label("Rain rate older of (sec)")                                                                                                   
                                                                                                            .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                                            .get().registerProperty(sensorProperty);

    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Padaline";
    }

    @Override
    public String getDescription() {
        return "Senzor količine padalina";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Rain: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double rain = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColRain));
        Double rainRate = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColRainRate));
        Integer rainOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColRainOlderOf));
        Integer rainRateOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColRainRateOlderOf));

        if (ValidationUtils.hasNoNull(rain, rainRate)) {
            DBSensorDataRain sRain = new DBSensorDataRain();
            sRain.sensorData = usd;
            sRain.sensor = sensor;
            sRain.dateTime = new java.sql.Timestamp(date.getTime());
            
            sRain.rain = rain;
            sRain.rainRate = rainRate;
            
            sRain.save();
            isWrite = true;
        } 
        
        log.debug("Rain: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    
    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataRain> lst = null;
        lst = DBSensorDataRain.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("Raint", "#" + Color.BLUEs.DarkBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsCool = mlchart.addDataSetWithTick("Raint rate", "#" + Color.BLUEs.LightBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Rain";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Rain", "Rain rate");
        
        for (DBSensorDataRain dd : lst) {
            MultiLineChartDataSetValue hR = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.rain);
            MultiLineChartDataSetValue cRR = dsCool.addValueAndTick(dd.dateTime.getTime(), dd.rainRate);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.rain, dd.rainRate);
        }
        

        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
