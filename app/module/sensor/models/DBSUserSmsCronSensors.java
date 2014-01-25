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
@Table(name = "s_user_sms_cron_sensors")
public class DBSUserSmsCronSensors extends Model {

    @Id
    public long id;

    @ManyToOne(cascade=CascadeType.DETACH)
    @Column(nullable = false, name = "sms_cron_id")
    public DBSUserSmsCron smsCron;

    @ManyToOne
    @Column(nullable = false, name = "user_sensor_id")
    public DBSUserSensor userSensor;
    
    public static Finder<Long, DBSUserSmsCronSensors> find = new Finder<Long, DBSUserSmsCronSensors>(Long.class, DBSUserSmsCronSensors.class);

    public static DBSUserSmsCronSensors findById(Long id) {
        DBSUserSmsCronSensors sug = find.byId(id);

        return sug;
    }
    
    public static List<DBSUserSmsCronSensors> findByCronId(Long cronId) {
        List<DBSUserSmsCronSensors> lsug = find.where("smsCron=" + cronId).findList();
        
        return lsug;
    }
    
}
