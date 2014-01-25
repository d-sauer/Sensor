package module.sensor.sensor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import module.sensor.models.DBSensor;
import controllers.sensor.AllSensorDataView.AllDataView;

public interface SensorInterface extends Serializable {

    public String getName();

    public String getDescription();

    public List<SensorProperty> getProperties();

    public void setProperty(String property, String value);

    public void loadProperties(DBSensor sensor);

    public void writeProperties(DBSensor sensor);

    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception;
}
