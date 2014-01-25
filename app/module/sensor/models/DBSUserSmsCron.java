package module.sensor.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import module.core.models.DBCUser;
import module.sensor.CronSceduleRule;
import play.db.ebean.Model;

@Entity
@Table(name = "s_user_sms_cron")
public class DBSUserSmsCron extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(nullable = false, name = "user_id")
    public DBCUser user;

    @Column(nullable = false, name = "active")
    public Boolean active = false;

    @Column(nullable = true, name = "mobile_number", length = 65)
    public String mobileNumber;

    @Column(nullable = true, name = "scedule_rule")
    public String sceduleRule;

    @Column(nullable = true, name = "next_scedule")
    public Timestamp nextScedule;

    @Column(nullable = true, name = "last_scedule")
    public Timestamp lastScedule;

    @Column(nullable = false, name = "repeated")
    public Integer repeated = 0;

    public static Finder<Long, DBSUserSmsCron> find = new Finder<Long, DBSUserSmsCron>(Long.class, DBSUserSmsCron.class);

    public static DBSUserSmsCron findById(Long id) {
        DBSUserSmsCron sug = find.byId(id);

        return sug;
    }

    public static List<DBSUserSmsCron> findByUser(Long userId) {
        List<DBSUserSmsCron> lsug = find.where("user=" + userId).findList();

        return lsug;
    }

    public static List<DBSUserSmsCron> findByUser(DBCUser user) {
        List<DBSUserSmsCron> lsug = find.where().eq("user", user).findList();
        
        return lsug;
    }

    
    
    public CronSceduleRule getRule() {
        CronSceduleRule csr = new CronSceduleRule();
        csr.setDescriptor(this.sceduleRule);
        return csr;
    }
    
    public List<String> getMobiles() {
        List<String> lst = new ArrayList<String>();
        if (mobileNumber!=null && mobileNumber.length()!=0) {
            String [] tmp = mobileNumber.split(",");
            for(String m : tmp) {
                lst.add(m.trim());
            }
        }
        
        if (lst.size()==0) {
            lst.add(user.phoneMobile);
        }
        
        return lst;
    }
}
