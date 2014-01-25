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
 * Heat is the total energy of molecular motion in a substance while temperature is a measure of the average energy of molecular motion in a substance.<br/> 
 * Heat energy depends on the speed of the particles, the number of particles (the size or mass), and the type of particles in an object. <br/>
 * Temperature does not depend on the size or type of object. <br/><br/>
 * For example, the temperature of a small cup of water might be the same as the temperature of a large tub of water,<br/> 
 * but the tub of water has more heat because it has more water and thus more total thermal energy.<br/>
 * 
 * @author davor
 *
 */

@Entity
@Table(name = "s_data_temperatures")
public class DBSensorDataTemperature extends Model {

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
    @Column(name = "temperature")
    public Double temperature;

    @SensorTableDataField
    @Column(name = "heat")
    public Double heat;

    /**
     * Heat Index (HI) - humiture. Kombinacija temperature i relativne vlažnosti, pokazuje kolika je temperatura iz ljudske perspektive
     * felt air temperature
     */
    @SensorTableDataField
    @Column(name = "heat_index")
    public Double heatIndex;

    /**
     * temeprature humidity with index - kombinacija hladnoće vjetra, heat indexa da se dobije vidljiva temperatura.
     * temperature feel when are out of the sun
     */
    @SensorTableDataField
    @Column(name = "thw_index")
    public Double thwIndex;
    
    public static Finder<Long, DBSensorDataTemperature> find = new Finder<Long, DBSensorDataTemperature>(Long.class, DBSensorDataTemperature.class);

    public static DBSensorDataTemperature getLastData(DBSensor sensor) {
        DBSensorDataTemperature dd = null;
        
//        find.where().eq("sensor", sensor).
        
        
        
        return dd;
    }
    
    public static List<DBSensorDataTemperature> findByDatePeriod(DBSensor sensor, Date dateFrom, Date dateTo) {
        List<DBSensorDataTemperature> lst = null;
        log.trace("dateFrom: %s, dateTo: %s", dateFrom, dateTo);
        java.sql.Timestamp sDateFrom = new java.sql.Timestamp(dateFrom.getTime());
        java.sql.Timestamp sDateTo = new java.sql.Timestamp(dateTo.getTime());
        log.trace("change to dateFrom: %s, dateTo: %s", sDateFrom, sDateTo);
        
        lst = find.where().eq("sensor", sensor).between("dateTime", sDateFrom, sDateTo).orderBy("dateTime").findList();
        
        return lst;
    }

    public static List<DBSensorDataTemperature> findAll(DBSensor sensor) {
        List<DBSensorDataTemperature> lst = null;
        lst = find.where().eq("sensor", sensor).orderBy("dateTime").findList();
        
        return lst;
    }

}
