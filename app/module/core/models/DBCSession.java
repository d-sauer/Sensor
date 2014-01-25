package module.core.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "c_sessions")
public class DBCSession extends Model {

    @Id
    public long id;

    @Column(length = 100, nullable = false, unique = true)
    public String uuid;

    @Column(length = 40, name="ip_address")
    public String ipAddress;

    public Timestamp created;

    public Timestamp expired;

    @Lob
    @Column(nullable = true, name = "session_data")
    public String sessionData;


    public static Finder<Long, DBCSession> find = new Finder<Long, DBCSession>(Long.class, DBCSession.class);
    
    public static DBCSession findByUUID(String uuid) {
        DBCSession dbSession = find.where().eq("uuid", uuid).findUnique();
        
        return dbSession;
    }

}
