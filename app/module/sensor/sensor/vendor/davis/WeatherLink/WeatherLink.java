package module.sensor.sensor.vendor.davis.WeatherLink;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.db.ebean.Model.Finder;

import util.Color;
import util.DateUtils;
import util.NumberUtils;
import util.data.WDate;
import util.db.EbeanUtils;
import controllers.sensor.AllSensorDataView;
import controllers.sensor.AllSensorDataView.AllDataGrid;
import controllers.sensor.AllSensorDataView.AllDataView;

import module.charts.MultiLineChart;
import module.charts.MultiLineChart.MultiLineChartDataSet;
import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;
import module.sensor.models.DBSUserSensor;
import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorDataDegreeDay;
import module.sensor.sensor.SensorGroup;
import module.sensor.sensor.SensorInterface;
import module.sensor.sensor.SensorProperty;
import module.sensor.sensor.SensorWorker.SensorWorkerInfo;

/**
 * <a href="http://www.weatherlink.com">WeatherLink</a>
 * @author davor
 *
 */
public class WeatherLink extends SensorGroup {

    private static final List<Class<? extends SensorInterface>> supportedSensors = new ArrayList<Class<? extends SensorInterface>>();
    
    /**
     * SUPPORTED SENSORS
     */
    static {
        supportedSensors.add(WeatherLink_DegreeDay.class);
        supportedSensors.add(WeatherLink_Density.class);
        supportedSensors.add(WeatherLink_EMC.class);
        supportedSensors.add(WeatherLink_Humidity.class);
        supportedSensors.add(WeatherLink_Moisture.class);
        supportedSensors.add(WeatherLink_Pressure.class);
        supportedSensors.add(WeatherLink_Rain.class);
        supportedSensors.add(WeatherLink_Temperature.class);
        supportedSensors.add(WeatherLink_Wetness.class);
        supportedSensors.add(WeatherLink_Wind.class);
    }

    
    private static final long serialVersionUID = 1L;
    public static final String groupVersion = "1.0";
    public static final String groupName = "WeatherLink";
    public static final String groupId = "weather_link_01";

    
    private List<SensorProperty> groupProperty = new ArrayList<SensorProperty>();
    // FTP
    public SensorProperty propFtp = new SensorProperty("ftp").properties().label("FTP Server").description("FTP server s datotekom s podacima senzora").get().registerProperty(groupProperty);
    public SensorProperty propUserName = new SensorProperty("userName").properties().label("Korisničko ime").description("korisničko ime za spajanje na FTP server").get().registerProperty(groupProperty);
    public SensorProperty propUserPass = new SensorProperty("userPass").properties().label("Korisnička lozinka").description("Korisnička lozinka za spajanje na FTP server").get().registerProperty(groupProperty);
    public SensorProperty propFtpFile1 = new SensorProperty("ftp_file1").properties().label("FTP File path 1").description("Lokacija 1. datoteke na FTP serveru").get().registerProperty(groupProperty);
    public SensorProperty propFtpFile2 = new SensorProperty("ftp_file2").properties().label("FTP File path 2").description("Lokacija 2. datoteke na FTP serveru").get().registerProperty(groupProperty);
    public SensorProperty propLocalFile1 = new SensorProperty("localFile1").properties().label("Lokalna datoteka 1").description("Čitaj 1. lokalnu datoteku umjesto FTP servera").get().registerProperty(groupProperty);
    public SensorProperty propLocalFile2 = new SensorProperty("localFile2").properties().label("Lokalna datoteka 2").description("Čitaj 2. lokalnu datoteku umjesto FTP servera").get().registerProperty(groupProperty);

    // File parser
    public SensorProperty propPfDateFormat = new SensorProperty("pfDateFormat").properties()
                                                                                .defaultValue("MM/dd/yy", true)
                                                                                .label("Format datuma")
                                                                                .description("Format datuma u datoteci s podacima o senzorima")
                                                                                .get().registerProperty(groupProperty);
    public SensorProperty propPfTimeFormat = new SensorProperty("pfTimeFormat").properties()
                                                                                .defaultValue("K:mm a", true)
                                                                                .label("Format vremena")
                                                                                .description("Format vremena u datoteci s podacima o senzorima")
                                                                                .get().registerProperty(groupProperty);

    // Thread
    public SensorProperty propThreadSleep = new SensorProperty("threadSleep").properties().defaultValue("350", true).label("Interval provjere (sec)").description("Interval skidanja i unosa podataka s FTPa u sekundama").get().registerProperty(groupProperty);

    // -----------------

    @Override
    public List<Class<? extends SensorInterface>> getSensorList() {
        return supportedSensors;
    }

    @Override
    public String getName() {
        return groupName;
    }

    @Override
    public String getDescription() {
        return "MeteoHub stanica";
    }

    @Override
    public List<SensorProperty> getProperties() {
        return groupProperty;
    }

    @Override
    public void setProperty(String property, String value) {
        SensorProperty.setProperty(groupProperty, property, value);
    }


    @Override
    public Class<? extends SensorInterface> getSensorClass(Class<? extends SensorInterface> sensorClass) {
        Class<? extends SensorInterface> rs = null;
        for (Class<? extends SensorInterface> s : getSensorList()) {
            if (s.equals(sensorClass)) {
                rs = s;
                break;
            }
        }

        return rs;
    }

    public SensorInterface getSensor(Class<? extends SensorInterface> sensorClass) throws Exception {
        Class<? extends SensorInterface> rsc = getSensorClass(sensorClass);
        
        return rsc.newInstance();
    }

    @Override
    public String getVersion() {
        return groupVersion;
    }

    @Override
    public SensorWorkerInfo getWorker() {
        SensorWorkerInfo swi = new SensorWorkerInfo();
        swi.swClass = WeatherLinkWorker.class;
        String sleep = propThreadSleep.properties().value();
        if (sleep == null || sleep == "")
            swi.swSleep = 60000L;
        else
            swi.swSleep = Long.parseLong(sleep) * 1000; // sec * 1000

        return swi;
    }

    
    public boolean writeSensorReadings(List<String> lineColumn, DBSensorData sensorData, List<DBSensor> sensors, Date date) throws Exception {
        boolean isWrited = false;
        for (DBSensor sensor : sensors) {
            Object so = sensor.getSensorClass();

            if (so instanceof WeatherLink_DegreeDay) {
                WeatherLink_DegreeDay sDD = (WeatherLink_DegreeDay) so;
                sDD.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Density) {
                WeatherLink_Density sDen = (WeatherLink_Density) so;
                sDen.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_EMC) {
                WeatherLink_EMC sHum = (WeatherLink_EMC) so;
                sHum.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Humidity) {
                WeatherLink_Humidity sHum = (WeatherLink_Humidity) so;
                sHum.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Moisture) {
                WeatherLink_Moisture sMois = (WeatherLink_Moisture) so;
                sMois.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Pressure) {
                WeatherLink_Pressure sPre = (WeatherLink_Pressure) so;
                sPre.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Rain) {
                WeatherLink_Rain sRain = (WeatherLink_Rain) so;
                sRain.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Temperature) {
                WeatherLink_Temperature sTemp = (WeatherLink_Temperature) so;
                isWrited = sTemp.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Wetness) {
                WeatherLink_Wetness sLeafW = (WeatherLink_Wetness) so;
                sLeafW.saveToDb(lineColumn, sensorData, sensor, date);
            }
            else if (so instanceof WeatherLink_Wind) {
                WeatherLink_Wind sMeteoS = (WeatherLink_Wind) so;
                sMeteoS.saveToDb(lineColumn, sensorData, sensor, date);
            }
        }

        return isWrited;
    }
    
    public static class CustomSensorData {
        private WDate date = new WDate("dd.MM.yyyy HH:mm:ss");
        private Double value;
        private String strValue;

        public Double value() {
            return value;
        }

        public String formatValue() {
            Double d = NumberUtils.doubleOf(strValue);

            if (d != null) {
                DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                return df.format(d);
            } else {
                Long l = NumberUtils.longOf(strValue);
                if (l != null) {
                    DecimalFormat df = new DecimalFormat("#,###,###,###");
                    return df.format(l);
                }
            }

            if (strValue != null)
                return strValue;
            else
                return " - ";
        }
        
        public WDate date() {
            return date;
        }
        
        public void set(java.sql.Timestamp dateTime, String value) {
            date.set(dateTime);
            
            strValue = value;
            Double d = NumberUtils.doubleOf(value);

            if (d == null) {
                Long l = NumberUtils.longOf(value);
                if (l != null)
                    d = l.doubleValue();
            }

            if (d != null)
                this.value = d;
            else
                this.value = 0d;
        }

    }
    
    
    public static AllDataView getUserSensorData(DBSUserSensor sensor, WDate dateFrom, WDate dateTo) throws Exception {
        List<CustomSensorData> lst = new ArrayList<WeatherLink.CustomSensorData>();
        dateFrom.setBeginOfDay();
        dateTo.setEndOfDay();
        
        String sDateFrom = dateFrom.getFormatted(WDate.ddMMyyyy_HHmmss);
        String sDateTo = dateTo.getFormatted(WDate.ddMMyyyy_HHmmss);

        String sql ="SELECT date_time, " + sensor.tableColumn + " FROM " + sensor.tableName +
                    " WHERE sensor_id = " + sensor.sensor.id +
                    "   AND date_time BETWEEN to_date('" + sDateFrom + "','DD.MM.YYYY HH24:MI:SS:MS') " +
                    "                     AND to_date('" + sDateTo + "','DD.MM.YYYY HH24:MI:SS:MS')" +
                    " ORDER BY date_time";
        
        ResultSet rs = EbeanUtils.executeQuery(sql);
        while(rs.next()) {
            CustomSensorData csd = new CustomSensorData();
            csd.set(rs.getTimestamp("date_time"), rs.getString(sensor.tableColumn));
            lst.add(csd);
        }
        rs.close();
        


        WDate datum = new WDate(WDate.ddMMyyyy_HHmmss);
        MultiLineChart mlchart = new MultiLineChart();
        MultiLineChartDataSet ds = mlchart.addDataSetWithTick("", "#" + Color.BLUEs.DarkBlue, dateFrom.get(), dateTo.get(), lst.size(), WDate.ddMMyyyy_HHmmss);

        mlchart.yAxisLabel = sensor.label;

        // grid 
        AllDataGrid grid = new AllDataGrid();
        grid.addHeader("Datum", sensor.label);
        
        for (CustomSensorData dd : lst) {
            // graf
            MultiLineChartDataSetValue hV = ds.addValueAndTick(dd.date().getTimeInMillis(), dd.value());

            // grid
            datum.set(dd.date());
            grid.insertRow(datum.getFormatted("dd.MM.yyyy HH:mm"), dd.formatValue());
        }
        
        AllSensorDataView.AllDataView adv = new AllDataView();
        adv.jsonGraf = mlchart.getJSON();
        adv.grid = grid;
        
        return adv;
    }

}
