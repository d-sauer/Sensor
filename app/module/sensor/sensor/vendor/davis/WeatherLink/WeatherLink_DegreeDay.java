package module.sensor.sensor.vendor.davis.WeatherLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataDegreeDay;
import module.sensor.sensor.Sensor;
import module.sensor.sensor.SensorProperty;
import module.sensor.sensor.SensorPropertyDbDescription;
import util.Color;
import util.DateUtils;
import util.NumberUtils;
import util.ValidationUtils;
import util.data.WDate;
import util.logger.log;
import controllers.sensor.AllSensorDataView;
import controllers.sensor.AllSensorDataView.AllDataGrid;
import controllers.sensor.AllSensorDataView.AllDataView;

public class WeatherLink_DegreeDay extends Sensor {

    private List<SensorProperty> sensorProperty = new ArrayList<SensorProperty>();
    
    @SensorPropertyDbDescription(modelClass=DBSensorDataDegreeDay.class, modelClassColumnField = "heat", tableName = "s_data_degree_days", columnName = "heat")
    public SensorProperty propColHeat = new SensorProperty("col_heat").properties().label("Heat")
            .defaultValue("18")
            .description("Heat D-D \n Faktor zagrijavanja. (col: 18)")
            .get().registerProperty(sensorProperty);

    public SensorProperty propColHeatOlderOf = new SensorProperty("col_heat_older_of").properties().label("Heat older of (sec)")
            .description("Spremi podatak samo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
            .get().registerProperty(sensorProperty);

    @SensorPropertyDbDescription(modelClass=DBSensorDataDegreeDay.class, modelClassColumnField = "cool", tableName = "s_data_degree_days", columnName = "cool")
    public SensorProperty propColCool = new SensorProperty("col_cool").properties().label("Cool")
            .defaultValue("19")
            .description("Cool D-D \n Faktor hlađenja. (col: 19)")
            .get().registerProperty(sensorProperty);

    public SensorProperty propColCoolOlderOf = new SensorProperty("col_cool_older_of").properties().label("Cool older of (sec)")
            .description("Spremi podataksamo ako je stariji od definiranog broja sekundi. Prazno evidentira sva učitanja.")
            .get().registerProperty(sensorProperty);

    @Override
    public List<SensorProperty> getProperties() {
        return sensorProperty;
    }

    @Override
    public String getName() {
        return "DegreeDay";
    }

    @Override
    public String getDescription() {
        return "Faktor zagrijavanja/hlađenja";
    }

    public boolean saveToDb(List<String> lineColumn, DBSensorData usd, DBSensor sensor, Date date) throws Exception {
        log.debug("DegreeDay: %s (%d) %s - start", this.getClass().getName(), sensor.id, sensor.name);
        boolean isWrite = false;

        Double heat = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeat));
        Double cool = NumberUtils.doubleOf(WeatherLinkParseFile.getColumn(lineColumn, propColCool));

        Integer heatOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColHeatOlderOf));
        Integer coolOlderOf = NumberUtils.integerOf(WeatherLinkParseFile.getColumn(lineColumn, propColCoolOlderOf));

        if (ValidationUtils.hasNoNull(heat, cool)) {
            DBSensorDataDegreeDay sDD = new DBSensorDataDegreeDay();
            sDD.sensorData = usd;
            sDD.sensor = sensor;
            sDD.dateTime = new java.sql.Timestamp(date.getTime());

            sDD.heat = heat;
            sDD.cool = cool;

            sDD.save();
            isWrite = true;
        }

        log.debug("DegreeDay: %s (%d) %s - end", this.getClass().getName(), sensor.id, sensor.name);
        return isWrite;
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        List<DBSensorDataDegreeDay> lst = null;
        dateFrom = DateUtils.getBeginOfDay(dateFrom);
        dateTo = DateUtils.getEndOfDay(dateTo);

        lst = DBSensorDataDegreeDay.findByDatePeriod(sensor, dateFrom, dateTo);


        // ObjectMapper mapper = new ObjectMapper();
        // ObjectNode rootNode = mapper.createObjectNode();
        // rootNode.put("y_axis_label", "Degree Day");
        //
        // ArrayNode ar = rootNode.putArray("data");
        // for(DBSensorDataDegreeDay dd : lst) {
        // ObjectNode jn = mapper.createObjectNode();
        // ar.add(jn);
        //
        // jn.put("dateTime", DateUtils.getFormatedDateTime(dd.dateTime.getTime()));
        // jn.put("heat", dd.heat);
        // jn.put("cool", dd.cool);
        // }

        WDate datum = new WDate(WDate.ddMMyyyy_HHmmss);
        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet dsHeat = mlchart.addDataSetWithTick("heat", "#" + Color.BLUEs.DarkBlue, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);
        MultiLineChartDataSet dsCool = mlchart.addDataSetWithTick("cool", "#" + Color.REDs.DarkRed, dateFrom, dateTo, lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = "Degree Day";

        // grid 
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", "Heat", "Cool");
        
        for (DBSensorDataDegreeDay dd : lst) {
            // graf
            MultiLineChartDataSetValue hV = dsHeat.addValueAndTick(dd.dateTime.getTime(), dd.heat);
            MultiLineChartDataSetValue cV = dsCool.addValueAndTick(dd.dateTime.getTime(), dd.cool);

            // grid
            datum.set(dd.dateTime);
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.heat, dd.cool);
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }

}
