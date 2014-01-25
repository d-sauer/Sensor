package module.sensor.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import module.core.models.DBCUser;
import module.sensor.sensor.SensorGroupInterface;
import module.sensor.sensor.SupportedSensor;
import module.sensor.sensor.SupportedSensors;
import play.db.ebean.Model;
import util.logger.log;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

/**
 * Grupe senzora koje korisnik ima uključene, definirane...
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_sensor_groups")
public class DBSensorGroup extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(nullable = false, name = "user_id")
    public DBCUser user;

    @Column(length = 100, nullable = false)
    public String name;

    @Column(length = 400, nullable = true)
    public String description;

    @Column(name = "active")
    public Boolean active = true;

    @Column(name = "visible")
    public Boolean visible = true;

    @Column(length = 300, name = "sensor_group_class")
    public String sensorGroupClass;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="group", orphanRemoval=true)
    public List<DBSensor> sensors;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="group", orphanRemoval=true)
    public List<DBSensorGroupProperty> properties;
    
    /**
     * Veza na java klasu za konfiguraciju senzora
     */
    @Transient
    private SensorGroupInterface groupClass = null;

    public static Finder<Long, DBSensorGroup> find = new Finder<Long, DBSensorGroup>(Long.class, DBSensorGroup.class);

    public static DBSensorGroup findById(Long groupId) {
        DBSensorGroup sg = find.byId(groupId);

        return sg;
    }

    public static List<DBSensorGroup> findByUser(Long userId) {
        List<DBSensorGroup> lsg = find.where("user=" + userId).findList();

        return lsg;
    }

    public static List<DBSensorGroup> findActive(boolean isActive) {
        List<DBSensorGroup> lsg = null;
        if (isActive == true)
            lsg = find.where("active=true").findList();
        else {
            lsg = find.where("(active=false OR active IS NULL)").findList();
        }

        if (lsg == null)
            lsg = new ArrayList<DBSensorGroup>();
        ;

        return lsg;
    }

    public List<DBSensor> getSensors() {
        List<DBSensor> ls = DBSensor.findByGroup(this.id);

        return ls;
    }

    public SensorGroupInterface getGroupClass() throws Exception {
        if (this.groupClass == null) {
            groupClass = SupportedSensors.getSensorsGroup(this.sensorGroupClass).newInstance();
            if (groupClass != null)
                groupClass.loadProperties(this);
        }

        return groupClass;
    }
    
    
    public List<SupportedSensor> getSupportedSensors() throws Exception {
        return SupportedSensors.getSensorsGroup(this.sensorGroupClass).getSensorList();
    }

    @Override
    public void save() {
//         Učitaj sensor properties za pohranu u bazu
        if (groupClass != null)
            groupClass.writeProperties(this);

        // pohrani u bazu
        super.save();
    }
    
    @Override
    public void delete() {
        // Sensor
        String sql = "DELETE FROM s_sensors WHERE group_id=" + this.id;
        log.debug(sql);
        SqlUpdate su = Ebean.createSqlUpdate(sql);
        su.execute();

        //
        
        
        //
        
        // delete this
        super.delete();
    }
    
    public DBSensorGroupProperty findProperty (String findProperty) {
        return findProperty(this.properties, findProperty);
    }
    
    public DBSensorGroupProperty findProperty (List<DBSensorGroupProperty> groupProperties, String findProperty) {
        DBSensorGroupProperty dbSGP = null;
        for(DBSensorGroupProperty gp : groupProperties) {
           if (gp.property.equals(findProperty)) {
               dbSGP = gp;
               break;
           }
        }
        
        return dbSGP;
    }
    
    
}
