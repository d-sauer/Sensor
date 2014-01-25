package module.sensor.models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import module.sensor.sensor.SensorTableDataField;
import play.db.ebean.Model;
import util.logger.log;

@Entity
@Table(name = "s_data_temperatures_high_low")
public class DBSensorDataTemperatureHighLow extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(name = "sensor_id")
    public DBSensor sensor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "data_id")
    public DBSensorData sensorData;

    @Column(nullable = false, name = "date_time")
    public Timestamp dateTime;

    //
    // Data ..
    //

    @SensorTableDataField
    @Column(name = "heat_high")
    public Double heatHigh;

    @SensorTableDataField
    @Column(name = "heat_low")
    public Double heatLow;

    @SensorTableDataField
    @Column(name = "temperature_high")
    public Double temperatureHigh;

    @SensorTableDataField
    @Column(name = "temperature_low")
    public Double temperatureLow;

    
    public static Finder<Long, DBSensorDataTemperatureHighLow> find = new Finder<Long, DBSensorDataTemperatureHighLow>(Long.class, DBSensorDataTemperatureHighLow.class);

    public static DBSensorDataTemperature getLastData(DBSensor sensor) {
        DBSensorDataTemperature dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataTemperatureHighLow> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataTemperatureHighLow> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataTemperatureHighLow> findAll(DBSensor sensor) {
        List<DBSensorDataTemperatureHighLow> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
