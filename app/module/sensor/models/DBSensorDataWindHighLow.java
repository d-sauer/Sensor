package module.sensor.models;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import module.sensor.sensor.SensorTableDataField;
import play.db.ebean.Model;

/**
 * 
 * @author davor
 * 
 */
@Entity
@Table(name = "s_data_winds_high_low")
public class DBSensorDataWindHighLow extends Model {

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

    @SensorTableDataField
    @Column(name = "high_speed")
    public Double highSpeed;

    @SensorTableDataField
    @Column(name = "high_speed_direction")
    public String highSpeedDirection;

    @SensorTableDataField
    @Column(name = "high_speed_direction_degree")
    public Integer highSpeedDirectionDegree;

    @SensorTableDataField
    @Column(name = "low_speed")
    public Double lowSpeed;

    @SensorTableDataField
    @Column(name = "low_speed_direction")
    public String lowSpeedDirection;

    @SensorTableDataField
    @Column(name = "low_speed_direction_degree")
    public Integer lowSpeedDirectionDegree;

}
