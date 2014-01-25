package module.sensor.models;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

/**
 * Mjeranje koliko je faktor zagrijavanja / hlađenja
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_data_customs")
public class DBSensorDataCustom extends Model {

    @Id
    public long id;

    @ManyToOne
    @Column(name = "sensor_id")
    public DBSensor sensor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "data_id")
    public DBSensorData sensorData;

    @Column(nullable = false, name = "date_time")
    public Timestamp dateTime;

    //
    // Data ..
    //

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type")
    public DBSensorDataCustomType dataType;

    @Column(name = "cool")
    public Double cool;

}
