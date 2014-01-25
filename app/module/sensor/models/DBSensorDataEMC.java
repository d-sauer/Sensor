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
 * Equilibrium moisture content
 * http://en.wikipedia.org/wiki/Equilibrium_moisture_content
 * @author davor
 *
 */

@Entity
@Table(name = "s_data_emc")
public class DBSensorDataEMC extends Model {

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
    
    /**
     * Equilibrium moisture content
     */
    @SensorTableDataField
    @Column(name = "emc")
    public Double emc;

    public static Finder<Long, DBSensorDataEMC> find = new Finder<Long, DBSensorDataEMC>(Long.class, DBSensorDataEMC.class);

    public static DBSensorDataEMC getLastData(DBSensor sensor) {
        DBSensorDataEMC dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataEMC> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataEMC> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataEMC> findAll(DBSensor sensor) {
        List<DBSensorDataEMC> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }
}
