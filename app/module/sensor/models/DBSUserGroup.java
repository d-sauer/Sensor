package module.sensor.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import module.core.models.DBCUser;
import play.db.ebean.Model;

@Entity
@Table(name = "s_user_groups")
public class DBSUserGroup extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(nullable = false, name = "user_id")
    public DBCUser user;

    @Column(nullable = false, length=100, name = "name")
    public String name;

    @Column(nullable = true, length=500, name = "description")
    public String description;


    public static Finder<Long, DBSUserGroup> find = new Finder<Long, DBSUserGroup>(Long.class, DBSUserGroup.class);

    public static DBSUserGroup findById(Long id) {
        DBSUserGroup sug = find.byId(id);

        return sug;
    }

    public static List<DBSUserGroup> findByUser(Long userId) {
        List<DBSUserGroup> lsug = find.where("user=" + userId).findList();
        
        return lsug;
    }
    

    public List<DBSUserSensor> getSensors() {
        List<DBSUserSensorGroup> lst = DBSUserSensorGroup.findByGroup(id);
        
        List<DBSUserSensor> lgs = new ArrayList<DBSUserSensor>();
        for(DBSUserSensorGroup dbUSG : lst) {
            DBSUserSensor dbUS = DBSUserSensor.findById(dbUSG.userSensor.id);
            lgs.add(dbUS);
        }
            
        
        return lgs;
    }

}
