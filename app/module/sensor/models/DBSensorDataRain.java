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

/**
 * http://en.wikipedia.org/wiki/Rain_gauge
 * 
 */
@Entity
@Table(name = "s_data_rains")
public class DBSensorDataRain extends Model {

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
    @Column(name = "rain")
    public Double rain;

    @SensorTableDataField
    @Column(name = "rain_rate")
    public Double rainRate;

    public static Finder<Long, DBSensorDataRain> find = new Finder<Long, DBSensorDataRain>(Long.class, DBSensorDataRain.class);

    public static DBSensorDataRain getLastData(DBSensor sensor) {
        DBSensorDataRain dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataRain> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataRain> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataRain> findAll(DBSensor sensor) {
        List<DBSensorDataRain> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
