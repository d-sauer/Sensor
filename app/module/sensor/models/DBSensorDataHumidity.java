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
import play.db.ebean.Model.Finder;
import util.logger.log;

/**
 * vlažnost, količina vode koja se nalazi u zraku (koja je isparila)
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_data_humiditys")
public class DBSensorDataHumidity extends Model {

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
    @Column(name = "humidity")
    public Double humidity;

    @SensorTableDataField
    @Column(name = "absolute_humidity")
    public Double absoluteHumidity;

    @SensorTableDataField
    @Column(name = "relative_humidity")
    public Double relativeHumidity;

    @SensorTableDataField
    @Column(name = "specific_humidity")
    public Double specificHumidity;

    
    public static Finder<Long, DBSensorDataHumidity> find = new Finder<Long, DBSensorDataHumidity>(Long.class, DBSensorDataHumidity.class);
    
    public static DBSensorDataHumidity getLastData(DBSensor sensor) {
        DBSensorDataHumidity dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataHumidity> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataHumidity> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataHumidity> findAll(DBSensor sensor) {
        List<DBSensorDataHumidity> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
