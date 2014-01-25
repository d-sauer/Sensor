package module.sensor.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "s_user_sensor_groups")
public class DBSUserSensorGroup extends Model {

    @Id
    public long id;

    @ManyToOne(cascade=CascadeType.DETACH, targetEntity=DBSUserGroup.class)
    @Column(nullable = false, name = "user_group_id")
    public DBSUserGroup userGroup;

    @ManyToOne(cascade=CascadeType.DETACH, targetEntity=DBSUserSensor.class)
    @Column(nullable = false, name = "user_sensor_id")
    public DBSUserSensor userSensor;


    public static Finder<Long, DBSUserSensorGroup> find = new Finder<Long, DBSUserSensorGroup>(Long.class, DBSUserSensorGroup.class);

    public static DBSUserSensorGroup findById(Long id) {
        DBSUserSensorGroup sug = find.byId(id);

        return sug;
    }

    public static List<DBSUserSensorGroup> findByGroup(Long groupId) {
        List<DBSUserSensorGroup> lsug = find.where("userGroup=" + groupId).findList();
        
        return lsug;
    }

}
