package module.sensor.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import module.core.models.DBCUser;
import play.db.ebean.Model;

@Entity
@Table(name = "s_user_sensors")
public class DBSUserSensor extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(nullable = false, name = "sensor_id")
    public DBSensor sensor;
    
    @ManyToOne
    @Column(nullable = false, name = "user_id")
    public DBCUser user;

    @Column(length = 300, name = "sensor_class")
    public String sensorClass;

    @Column(length = 50, name = "table_name")
    public String tableName;

    @Column(length = 50, name = "table_column")
    public String tableColumn;

    
    @Column(nullable = false, length=100, name = "label")
    public String label;

    @Column(nullable = true, length=500, name = "description")
    public String description;


    public static Finder<Long, DBSUserSensor> find = new Finder<Long, DBSUserSensor>(Long.class, DBSUserSensor.class);

    public static DBSUserSensor findById(Long id) {
        DBSUserSensor sug = find.byId(id);

        return sug;
    }

    public static DBSUserSensor findByFK(Long userId, Long sensorId, String tableName, String columnName) {
        List<DBSUserSensor> lsug = find.where("user=" + userId + " AND sensor=" + sensorId + " AND tableName='" + tableName + "' AND tableColumn='" + columnName + "'").orderBy().asc("id").findList();
                
        DBSUserSensor sug = null;
        if (lsug.size()!=0) 
            sug = lsug.get(0);
        
        return sug;
    }
    
}
