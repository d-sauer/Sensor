package module.sensor.models;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;
import util.logger.log;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

@Entity
@Table(name = "s_sensor_raw_data")
public class DBSensorData extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(nullable = false, name = "group_id")
    public DBSensorGroup group;

    @Column(nullable = false, name = "when_created")
    public Timestamp whenCreated;

    @Column(nullable = false, name = "date_time")
    public Timestamp dateTime;

    @Lob
    @Column(name = "data")
    public String data;

    public List<DBSensorDataTemperature> temperatures;

    public static Finder<Long, DBSensorData> find = new Finder<Long, DBSensorData>(Long.class, DBSensorData.class);

    public static List<DBSensorData> findByDate(DBSensorGroup group, Date datum) {
        java.sql.Timestamp ts = new Timestamp(datum.getTime());
        List<DBSensorData> lst = find.where().eq("group", group).eq("dateTime", ts).findList();

        return lst;
    }

    /**
     * Delete unused data, clean table of data that is never used
     * @throws Exception 
     */
    public static void cleanTable() throws Exception {
        // za svaki record, provjeri dali se koristi u svim vezanim tablicama
        // ako se ne koristi ni u jednoj tablici obriši record
        List<String>tables = DBSensor.oneToManyConnectedTable();
        
        Connection conn = Ebean.beginTransaction().getConnection();
        conn.prepareStatement("");
        
        conn.commit();
        conn.close();
        for (String table : tables) {
            
        }
    }
    
    @Override
    public void delete() {
        for (String tableName :DBSensor.oneToManyConnectedTable()) {
            String sql = "DELETE FROM " + tableName + " WHERE data_id=" + this.id;
            log.debug(sql);
            SqlUpdate su = Ebean.createSqlUpdate(sql);
            su.execute();
        }

        // delete this
        super.delete();
    }
}
