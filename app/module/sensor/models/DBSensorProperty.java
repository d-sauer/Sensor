package module.sensor.models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import play.db.ebean.Model;

/**
 * 
 * @author dsauer
 * 
 */
@Entity
@Table(name = "s_sensor_properties")
public class DBSensorProperty extends Model {

    @Id
    public long id;

    @ManyToOne(cascade=CascadeType.ALL)
    @Column(nullable=true, name = "sensor_id")
    public DBSensor sensor;

    @Column(length = 70, nullable = false, name = "property")
    public String property;

    @Column(length = 400, name = "value")
    public String value;

    @Column(name = "user_label")
    public String userLabel;

    @Column(length = 500, name = "user_description")
    public String userDescription;

    @Column(name = "db_table")
    public String dbTable;

    @Column(name = "db_column")
    public String dbColumn;

    @Version
    public Timestamp updated;



    public static Finder<Long, DBSensorProperty> find = new Finder<Long, DBSensorProperty>(Long.class, DBSensorProperty.class);

    public static DBSensorProperty findById(Long propertyId) {
        DBSensorProperty sen = find.byId(propertyId);

        return sen;
    }

    public static List<DBSensorProperty> findSensorProperty(Long sensorId, String property) {
        List<DBSensorProperty> lsen = find.where("sensor=" + sensorId + " AND property='" + property + "'").findList();

        return lsen;
    }

    public static List<DBSensorProperty> findGroupProperty(Long groupId, String property) {
        List<DBSensorProperty> lsen = find.where("group=" + groupId + " AND property='" + property + "'").findList();
        
        return lsen;
    }

}
