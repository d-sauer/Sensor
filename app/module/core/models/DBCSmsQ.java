package module.core.models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "c_sms_q")
public class DBCSmsQ extends Model {

    public static enum DBCSmsQAction {
        prepare, send, sending, sent, received, not_sent;
    }

    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @Column(name = "user_id", nullable = true)
    public DBCUser user;

    @Column(name = "date_created")
    public Timestamp dateCreated;

    @Column(name = "date_send")
    public Timestamp dateSend;

    @Column(name = "date_confirm")
    public Timestamp dateConfirm;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = true)
    public DBCSmsQAction action;

    @Column(length = 20, nullable = false, name = "mobile_number")
    public String mobileNumber;

    @Column(length = 600, nullable = false, name = "content")
    public String content;

    public static Finder<Long, DBCSmsQ> find = new Finder<Long, DBCSmsQ>(Long.class, DBCSmsQ.class);

    public static List<DBCSmsQ> findByAction(DBCSmsQAction action) {
        List<DBCSmsQ> lst = find.where().eq("action", action).findList();
        return lst;
    }
}
