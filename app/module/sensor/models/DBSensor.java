package module.sensor.models;

import java.sql.Timestamp;
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
import module.sensor.sensor.SensorInterface;
import module.sensor.sensor.SupportedSensors;
import play.db.ebean.Model;
import util.db.EbeanUtils;
import util.logger.log;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

/**
 * 
 * @author dsauer
 * 
 */
@Entity
@Table(name = "s_sensors")
public class DBSensor extends Model {

    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.DETACH) // !! - DETACH - da ne briše i FK record
    @Column(name = "group_id")
    public DBSensorGroup group;

    @ManyToOne(cascade=CascadeType.DETACH)
    @Column(nullable = false, name = "user_id")
    public DBCUser user;

    @Column(length = 100, nullable = false, name = "name")
    public String name;

    @Column(length = 400, name = "description")
    public String description;

    public Timestamp lastUpdate;

    public boolean active = true;

    public boolean visible = true;

    @Column(length = 300, name = "sensor_class")
    public String sensorClass;

    //
    // Veze prema tablicama s podacima
    //
    @OneToMany(cascade=CascadeType.ALL, mappedBy="sensor", orphanRemoval=true)
    public List<DBSensorProperty> properties;

    @Transient
    private SensorInterface sClass = null;

    @Transient
    private static final List<Class<? extends Model>> connectedTables = new ArrayList<Class<? extends Model>>();

    public static Finder<Long, DBSensor> find = new Finder<Long, DBSensor>(Long.class, DBSensor.class);

    public static DBSensor findById(Long sensorId) {
        DBSensor sen = find.byId(sensorId);

        return sen;
    }

    public static List<DBSensor> findByGroup(Long groupId) {
        List<DBSensor> lsen = find.where("group=" + groupId).orderBy("id").findList();

        return lsen;
    }

    public static List<DBSensor> findByUser(Long userId) {
        List<DBSensor> lsen = find.where().eq("user_id", userId).findList();

        return lsen;
    }

    public SensorInterface getSensorClass() throws Exception {
        if (sClass == null) {
            sClass = SupportedSensors.getSensors(group.sensorGroupClass, this.sensorClass).newInstance();
            if (sClass != null)
                sClass.loadProperties(this);
        }

        return sClass;
    }

    @Override
    public void save() {
        // Učitaj sensor properties za pohranu u bazu
        if (sClass != null)
            sClass.writeProperties(this);

        // pohrani u bazu
        super.save();
    }

    public static List<Class<? extends Model>> getConnectedTables() {
        // prepare related model class
        if (connectedTables.size() == 0) {
            connectedTables.add(DBSensorDataCustom.class);
            connectedTables.add(DBSensorDataDegreeDay.class);
            connectedTables.add(DBSensorDataDensity.class);
            connectedTables.add(DBSensorDataEMC.class);
            connectedTables.add(DBSensorDataHumidity.class);
            connectedTables.add(DBSensorDataMoisture.class);
            connectedTables.add(DBSensorDataPressure.class);
            connectedTables.add(DBSensorDataRain.class);
            connectedTables.add(DBSensorDataTemperature.class);
            connectedTables.add(DBSensorDataTemperatureHighLow.class);
            connectedTables.add(DBSensorDataWetness.class);
            connectedTables.add(DBSensorDataWind.class);
            connectedTables.add(DBSensorDataWindHighLow.class);
        }

        return connectedTables;
    }

    public static List<String> oneToManyConnectedTable() {
        List<Class<? extends Model>> lModels = getConnectedTables();

        // get table name
        List<String> l = new ArrayList<String>();
        for (Class<? extends Model> model : lModels) {
            String name = EbeanUtils.getTableName(model);
            if (name != null)
                l.add(name);
        }

        return l;
    }

    @Override
    public void delete() {
        // delete sensor data
        for (String tableName : oneToManyConnectedTable()) {
            String sql = "DELETE FROM " + tableName + " WHERE sensor_id=" + this.id;
            log.debug(sql);
            SqlUpdate su = Ebean.createSqlUpdate(sql);
            su.execute();
        }

        super.delete();

//        try {
//            DBSensorData.cleanTable();
//        } catch (Exception e) {
//            Log.exception(e);
//        }
    }
    
    public DBSensorProperty findProperty (String findProperty) {
        return findProperty(this.properties, findProperty);
    }
    
    public DBSensorProperty findProperty (List<DBSensorProperty> properties, String findProperty) {
        DBSensorProperty dbSGP = null;
        for(DBSensorProperty gp : properties) {
           if (gp.property.equals(findProperty)) {
               dbSGP = gp;
               break;
           }
        }
        
        return dbSGP;
    }
}
