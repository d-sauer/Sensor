package module.sensor.sensor;

import java.io.Serializable;
import java.util.List;

import module.sensor.models.DBSensorGroup;
import module.sensor.models.DBSensorGroupProperty;
import module.sensor.sensor.SensorWorker.SensorWorkerInfo;

public interface SensorGroupInterface extends Serializable {

    public List<Class<? extends SensorInterface>> getSensorList();

    public Class<? extends SensorInterface> getSensorClass(Class<? extends SensorInterface> sensorClass) throws Exception;

    public SensorInterface getSensor(Class<? extends SensorInterface> sensorClass) throws Exception;

    public String getName();

    public String getDescription();

    public String getVersion();

    public List<SensorProperty> getProperties();

    public void setProperty(String property, String value);

    public SensorWorkerInfo getWorker();

    /**
     * Load properties value from database
     * @param xml
     */
    public void loadProperties(DBSensorGroup sensorGroup);

    /**
     * Prepare properties value to save in database
     * @return
     */
    public void writeProperties(DBSensorGroup sensorGroup);

}
