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
 * Prisutnost vode u nekoj tvari
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_data_wetness")
public class DBSensorDataWetness extends Model {

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
    @Column(name = "wetness")
    public Double wetness;

    @SensorTableDataField
    @Column(name = "dry")
    public Double dry;
    
    @SensorTableDataField
    @Column(name = "dew_point")
    public Double dewPoint;

    @SensorTableDataField
    @Column(name = "precipitation")
    public Double precipitation;

    public static Finder<Long, DBSensorDataWetness> find = new Finder<Long, DBSensorDataWetness>(Long.class, DBSensorDataWetness.class);

    public static DBSensorDataWetness getLastData(DBSensor sensor) {
        DBSensorDataWetness dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataWetness> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataWetness> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataWetness> findAll(DBSensor sensor) {
        List<DBSensorDataWetness> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
