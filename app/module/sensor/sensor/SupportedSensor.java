package module.sensor.sensor;

import java.util.concurrent.atomic.AtomicInteger;

public class SupportedSensor {
    private static final AtomicInteger indexer = new AtomicInteger(0);
    private Class<? extends SensorInterface> sensorClass;
    private String name;
    private String description;

    public Class<? extends SensorInterface> getSensorClass() {
        return sensorClass;
    }

    public void setSensorClass(Class<? extends SensorInterface> sensorClass) {
        this.sensorClass = sensorClass;
    }

    public SensorInterface newInstance() throws Exception {
        return this.sensorClass.newInstance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Integer index;

    public SupportedSensor() {
        index = indexer.getAndIncrement();
    }

    public Integer getIndex() {
        return index;
    }
}