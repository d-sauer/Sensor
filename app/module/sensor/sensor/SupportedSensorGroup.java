package module.sensor.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SupportedSensorGroup {
    private static final AtomicInteger indexer = new AtomicInteger(0);
    Class<? extends SensorGroupInterface> groupClass;
    private String name;
    private List<SupportedSensor> listSuportedSensors = new ArrayList<SupportedSensor>();

    public Class<? extends SensorGroupInterface> getGroupClass() {
        return groupClass;
    }

    public void setGroupClass(Class<? extends SensorGroupInterface> groupClass) {
        this.groupClass = groupClass;
    }

    public SensorGroupInterface newInstance() throws Exception {
        return this.groupClass.newInstance();
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

    public String description;
    private Integer index;

    public SupportedSensorGroup() {
        index = indexer.getAndIncrement();
    }

    public Integer getIndex() {
        return index;
    }

    public void addSensor(Class<? extends SensorInterface> sensor) throws Exception {
        SupportedSensor ss = new SupportedSensor();
        SensorInterface s = sensor.newInstance();
        ss.setSensorClass(sensor);
        ss.setName(s.getName());
        ss.setDescription(s.getDescription());

        listSuportedSensors.add(ss);
    }

    public List<SupportedSensor> getSensorList() {
        return listSuportedSensors;
    }

    public SupportedSensor getSensorByIndex(Integer index) {
        SupportedSensor rss = null;
        for (SupportedSensor ss : getSensorList()) {
            if (ss.getIndex().intValue() == index.intValue()) {
                rss = ss;
                break;
            }
        }

        return rss;
    }

    public SupportedSensor getSensorByClassName(String className) {
        SupportedSensor rss = null;
        for (SupportedSensor ss : getSensorList()) {
            if (ss.getSensorClass().getName().toString().equals(className))
                rss = ss;
            break;
        }

        return rss;
    }

    public SupportedSensor getSensorByClass(Class<? extends SensorInterface> sensor) {
        SupportedSensor rss = null;
        for (SupportedSensor ss : getSensorList()) {
            if (ss.getSensorClass().equals(sensor))
                rss = ss;
            break;
        }

        return rss;
    }

}