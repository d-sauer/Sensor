package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataPressure;
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

public class WeatherLink_Pressure extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataPressure.class, modelClassColumnField = "pressureBar", tableName = "s_data_pressures", columnName = "pressureBar")
    public SensorProperty propColBar = new SensorProperty("col_bar").properties().label("Bar")
                                                                              .defaultValue("15")
                                                                              .description("Bar \n Pritisak  (col: 15)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColBarOlderOf = new SensorProperty("col_bar_older_of").properties().label("Bar older of (sec)")                                                                                                   
                                                                                      .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                      .get().registerProperty(sensorProperty);


    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Pressure";
    }

    @Override
    public String getDescription() {
        return "Senzor pritiska";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("DegreeDay: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double bar = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColBar));
        Integer barOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColBarOlderOf));

        if (ValidationUtils.hasNoNull(bar)) {
            DBSensorDataPressure sPressure = new DBSensorDataPressure();
            sPressure.sensorData = usd;
            sPressure.sensor = sensor;
            sPressure.dateTime = new java.sql.Timestamp(date.getTime());
            
            sPressure.pressureBar = bar;
            
            sPressure.save();
            isWrite = true;
        } 
        
        log.debug("DegreeDay: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataPressure> lst = null;
        lst = DBSensorDataPressure.findByDatePeriod(sensor, dateFrom, dateTo);

        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsBar = mlchart.addDataSetWithTick("bar", "#" + Color.BLUEs.DarkBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsPasc = mlchart.addDataSetWithTick("pascal", "#" + Color.BLUEs.DeepSkyBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Pressure";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Bar", "Pascal");    
        
        for (DBSensorDataPressure dd : lst) {
            MultiLineChartDataSetValue hB = dsBar.addValueAndTick(dd.dateTime.getTime(), dd.pressureBar);
            MultiLineChartDataSetValue hP = dsPasc.addValueAndTick(dd.dateTime.getTime(), dd.pressurePascal);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.pressureBar, dd.pressurePascal);
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
