package module.sensor.sensor;

import java.util.List;

import module.sensor.models.DBSensorGroup;
import module.sensor.models.DBSensorGroupProperty;
import util.logger.log;

public abstract class SensorGroup implements SensorGroupInterface {

    public void loadProperties(DBSensorGroup sensorGroup) {
        log.debug("load sensor group properties (" + sensorGroup.id + ") " + sensorGroup.name);
        List<DBSensorGroupProperty> dbSGPs = sensorGroup.properties;

        List<SensorProperty> lsp = getProperties();
        for (SensorProperty sp : lsp) {
            DBSensorGroupProperty dbSP = sensorGroup.findProperty(dbSGPs, sp.properties().name());
            if (dbSP != null) {
                sp.properties().value(dbSP.value);
            }
        }

    }

    public void writeProperties(DBSensorGroup sensorGroup) {
        log.debug("write sensor group properties");
        List<DBSensorGroupProperty> dbGroupProperties = sensorGroup.properties;

        List<SensorProperty> sp = getProperties();
        for (SensorProperty s : sp) {
            DBSensorGroupProperty dbSGP = sensorGroup.findProperty(dbGroupProperties, s.properties().name());
            if (dbSGP == null)
                dbSGP = new DBSensorGroupProperty();

            dbSGP.group = sensorGroup;
            dbSGP.property = s.properties().name();
            dbSGP.value = s.properties().value();
            dbSGP.userLabel = s.properties().userLabel();
            dbSGP.userDescription = s.properties().userDescription();

            dbSGP.save();
        }
    }

}
