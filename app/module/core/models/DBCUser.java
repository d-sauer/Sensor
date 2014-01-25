package module.core.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;
import util.StringUtils;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "c_users")
public class DBCUser extends Model {

    @Id
    public long                        id;

    @Column(length = 100, nullable = false, unique = true)
    public String                      email;

    @Column(length = 100, nullable = false, unique = true, name = "user_name")
    public String                      userName;

    @Column(length = 50)
    public String                      password;

    @Column(length = 50, name = "first_name")
    public String                      firstName;

    @Column(length = 50, name = "last_name")
    public String                      lastName;

    @Column(length = 20, name = "phone_land")
    public String                      phoneLand;

    @Column(length = 20, name = "phone_mobile")
    public String                      phoneMobile;

    @Column(length = 50)
    public String                      city;

    @Column(length = 10, name = "post_number")
    public String                      postNumber;

    @Column(length = 100)
    public String                      street;

    /**
     * -1 - neaktivan - novi
     * 0  - neaktivan
     * 1  - aktivan
     */
    @Column(columnDefinition = "integer default 0")
    public int                         active = 0;

    public static Finder<Long, DBCUser> find   = new Finder<Long, DBCUser>(Long.class, DBCUser.class);

    public static DBCUser findByUserName(String userName) {
        DBCUser dbu = Ebean.find(DBCUser.class).where().eq("userName", userName).findUnique();
        return dbu;
    }

    public static List<DBCUser> findAll() {
        List<DBCUser> ldbu = Ebean.find(DBCUser.class).findList();
        return ldbu;
    }

    public static List<DBCUser> findAllActive() {
        List<DBCUser> ldbu = Ebean.find(DBCUser.class).where().eq("active", 1).findList();
        return ldbu;
    }

    public static DBCUser findById(Long userId) {
        if (userId != null) {
            DBCUser dbu = Ebean.find(DBCUser.class, userId);
            return dbu;
        }
        else
            return null;
    }
    
    public String getBestName() {
        String name = userName;
        
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName))
            name = firstName + " " + lastName;
        else if (StringUtils.isBlank(firstName) && StringUtils.isNotBlank(lastName))
            name = lastName;
        else if (StringUtils.isNotBlank(firstName) && StringUtils.isBlank(lastName))
            name = firstName;
        
        return name;
    }
    
}
