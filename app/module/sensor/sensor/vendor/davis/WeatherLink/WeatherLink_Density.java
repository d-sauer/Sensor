package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataDensity;
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

public class WeatherLink_Density extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataDensity.class, modelClassColumnField = "density", tableName = "s_data_densitys", columnName = "density")
    public SensorProperty propColDensity = new SensorProperty("col_density").properties().label("Density")
                                                                              .defaultValue("25")
                                                                              .description("Density \n Prosjećna gustoća tijela  (col: 25)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColDensityOlderOf = new SensorProperty("col_density_older_of").properties().label("Density older of (sec)")                                                                                                   
                                                                                            .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                            .get().registerProperty(sensorProperty);


    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "Density";
    }

    @Override
    public String getDescription() {
        return "Prosjećna gustoća";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("Density: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double density = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColDensity));
        Integer densityOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColDensityOlderOf));

        if (ValidationUtils.hasNoNull(density)) {
            DBSensorDataDensity sDensity = new DBSensorDataDensity();
            sDensity.sensorData = usd;
            sDensity.sensor = sensor;
            sDensity.dateTime = new java.sql.Timestamp(date.getTime());
            
            sDensity.density = density;
            
            sDensity.save();
            isWrite = true;
        } 
        
        log.debug("Density: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataDensity> lst = null;
        lst = DBSensorDataDensity.findByDatePeriod(sensor, dateFrom, dateTo);


        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("density", "#" + Color.BLUEs.DeepSkyBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Density";
        
        //   grid 
        WDate datum = new WDate(WDate.ddMMyyyy);
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Density");        
        
        for (DBSensorDataDensity dd : lst) {
            MultiLineChartDataSetValue hV = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.density);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.density);
        }
        

        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
}
