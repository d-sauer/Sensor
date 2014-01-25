package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataEMC;
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

public class WeatherLink_EMC extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataEMC.class, modelClassColumnField = "emc", tableName = "s_data_emc", columnName = "emc")
    public SensorProperty propColEMC = new SensorProperty("col_emc").properties().label("EMC")
                                                                              .defaultValue("24")
                                                                              .description("Equilibrium moisture content (col: 24)")
                                                                              .get().registerProperty(sensorProperty);

    public SensorProperty propColEMCOlderOf = new SensorProperty("col_emc_older_of").properties().label("EMC older of (sec)")                                                                                                   
                                                                                    .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
                                                                                    .get().registerProperty(sensorProperty);


    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "EMC";
    }

    @Override
    public String getDescription() {
        return "Equilibrium moisture content";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("EMC: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;
        
        Double EMC = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColEMC));
        Integer EmcOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColEMCOlderOf));

        if (ValidationUtils.hasNoNull(EMC)) {
            DBSensorDataEMC sEMC = new DBSensorDataEMC();
            sEMC.sensorData = usd;
            sEMC.sensor = sensor;
            sEMC.dateTime = new java.sql.Timestamp(date.getTime());
            
            sEMC.emc = EMC;
            
            sEMC.save();
            isWrite = true;
        } 
        
        log.debug("EMC: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataEMC> lst = null;
        lst = DBSensorDataEMC.findByDatePeriod(sensor, dateFrom, dateTo);


        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("EMC", "#" + Color.BLUEs.DarkBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Equilibrium moisture content";
        
        //   grid 
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "EMC"); 
        WDate datum = new WDate(WDate.ddMMyyyy);
        
        for (DBSensorDataEMC dd : lst) {
            MultiLineChartDataSetValue hV = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.emc);

            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.emc);
        }
        

        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }
    
}
