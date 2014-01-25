package module.sensor.sensor;

import module.sensor.models.DBSensor;

public class AvailableSensor {

    public Long sensorId;
    public Long groupId;
    public Class className;
    public String tableName;
    public String columnName;
    public String name;
    public String description;

    public void loadSensorLabelAndDescription() throws Exception {
        boolean isFind = false;
        if (sensorId != null) {
            DBSensor dbS = DBSensor.findById(sensorId);
            if (dbS != null) {
                
                SensorInterface s = dbS.getSensorClass();
                if (s != null) {
                    SensorProperty sp = SensorProperty.getSensorProperty(s, tableName, columnName);
                    if (sp!=null) {
                        isFind = true;

                        name = sp.properties().label();
                        description = sp.properties().description();
                    }
                }
            }

        }

        if (isFind == false) {
            name = "Nepoznat naziv senzora";
            description = "Nepoznat opis senzora";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AvailableSensor) {
            AvailableSensor as = (AvailableSensor) obj;

            if (this.sensorId.equals(as.sensorId) && tableName.equals(as.tableName) && columnName.equals(as.columnName))
                return true;
            else
                return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int a = sensorId != null ? sensorId.intValue() : 1;
        int b = tableName.length();
        int c = columnName.length();

        return (int) ((a * b) / c);
    }
    
    @Override
    public String toString() {
        return String.format("{sensor id: %d, group id: %d, tableName: %s, columnName: %s, name: %s, description: %s}", sensorId, groupId, tableName, columnName, name, description);
    }

}
