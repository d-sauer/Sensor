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
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_data_winds")
public class DBSensorDataWind extends Model {

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
    @Column(name = "wind_speed")
    public Double windSpeed;

    @SensorTableDataField
    @Column(name = "wind_run")
    public Double windRun;

    @SensorTableDataField
    @Column(name = "direction")
    public String direction;

    @SensorTableDataField
    @Column(name = "direction_degree")
    public Integer directionDegree;

    @SensorTableDataField
    @Column(name = "wind_chill")
    public Double windChill;

    @SensorTableDataField
    @Column(name = "wind_sample")
    public Double windSample;

    @SensorTableDataField
    @Column(name = "wind_tx")
    public Double windTx;

    
    public static Finder<Long, DBSensorDataWind> find = new Finder<Long, DBSensorDataWind>(Long.class, DBSensorDataWind.class);

    public static DBSensorDataWind getLastData(DBSensor sensor) {
        DBSensorDataWind dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataWind> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataWind> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataWind> findAll(DBSensor sensor) {
        List<DBSensorDataWind> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
