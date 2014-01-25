package module.sensor.sensor;

import java.util.Date;
import java.util.List;

import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorProperty;
import util.logger.log;
import controllers.sensor.AllSensorDataView.AllDataView;

public abstract class Sensor implements SensorInterface {

    @Override
    public void setProperty(String property, String value) {
        SensorProperty.setProperty(getProperties(), property, value);
    }

    public void loadProperties(DBSensor sensor) {
        log.debug("load sensor properties (" + sensor.id + ") " + sensor.name);
        List<DBSensorProperty> dbSPs = sensor.properties;

        List<SensorProperty> lsp = getProperties();
        for (SensorProperty sp : lsp) {
            DBSensorProperty dbSP = sensor.findProperty(dbSPs, sp.properties().name());
            if (dbSP != null) {
                sp.properties().value(dbSP.value);
            }
        }

    }

    public void writeProperties(DBSensor sensor) {
        log.debug("write sensor group properties");
        List<DBSensorProperty> dbProperties = sensor.properties;

        List<SensorProperty> sp = getProperties();
        for (SensorProperty s : sp) {
            DBSensorProperty dbSP = sensor.findProperty(dbProperties, s.properties().name());
            if (dbSP == null)
                dbSP = new DBSensorProperty();

            dbSP.sensor = sensor;
            dbSP.property = s.properties().name();
            dbSP.value = s.properties().value();
            dbSP.userLabel = s.properties().userLabel();
            dbSP.userDescription = s.properties().userDescription();

            dbSP.save();
        }
    }

    @Override
    public AllDataView getData(DBSensor sensor, Date dateFrom, Date dateTo) throws Exception {
        return null;
    }

}
