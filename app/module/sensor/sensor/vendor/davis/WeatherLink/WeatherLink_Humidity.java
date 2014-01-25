package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataHumidity;
import module.sensor.sensor.Sensor;
import module.sensor.sensor.SensorProperty;
import module.sensor.sensor.SensorPropertyDbDescription;
import util.Color;
import util.NumberUtils;
import util.data.WDate;
import util.logger.log;
import controllers.sensor.AllSensorDataView;
import controllers.sensor.AllSensorDataView.AllDataGrid;
import controllers.sensor.AllSensorDataView.AllDataView;

public class WeatherLink_Humidity extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataHumidity.class, modelClassColumnField = "humidity", tableName = "s_data_humiditys", columnName = "humidity")
    public SensorProperty propColHumidity = new SensorProperty("col_humidity").properties().label("Vlaga zraka")
                                                                              .defaultValue("21")
                                                                              .description("Humidity \n Vlaga zraka (col: 21/27)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColHumidityOlderOf = new SensorProperty("col_humidity_older_of").properties().label("Humidity older of (sec)")                                                                                                   
                                                                                              .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                              .get().registerProperty(sensorProperty);
    
    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Vlaga zraka (humidity)";
    }

    @Override
    public String getDescription() {
        return "Senzor vlage zraka";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug(getName() + ": %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double humidity = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHumidity));
        Integer humidityOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColHumidityOlderOf));

        if (humidity != null) {
            DBSensorDataHumidity sHum = new DBSensorDataHumidity();
            sHum.sensorData = usd;
            sHum.sensor = sensor;
            sHum.dateTime = new java.sql.Timestamp(date.getTime());
            
            sHum.humidity = humidity;
         
            sHum.save();
            isWrite = true;
        } 
        
        log.debug(getName() + ": %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }
    
    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataHumidity> lst = null;
        lst = DBSensorDataHumidity.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHumidity = mlchart.addDataSetWithTick("humidity", "#" + Color.BLUEs.DarkBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsRelHum = mlchart.addDataSetWithTick("relative Humidity", "#" + Color.BLUEs.Aqua, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsAbsHum = mlchart.addDataSetWithTick("absolute Humidity", "#" + Color.BLUEs.CadetBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Humidity";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Humidity", "Relative humidity", "Absolute humidity"); 
        
        for (DBSensorDataHumidity dd : lst) {
            MultiLineChartDataSetValue hH = dsHumidity.addValueAndTick(dd.dateTime.getTime(), dd.humidity);
            MultiLineChartDataSetValue cRH = dsRelHum.addValueAndTick(dd.dateTime.getTime(), dd.relativeHumidity);
            MultiLineChartDataSetValue cAH = dsAbsHum.addValueAndTick(dd.dateTime.getTime(), dd.absoluteHumidity);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.humidity, dd.relativeHumidity, dd.absoluteHumidity);
        }
        

        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
