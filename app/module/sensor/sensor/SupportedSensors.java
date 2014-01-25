package module.sensor.sensor;

import java.util.ArrayList;
import java.util.List;

import util.logger.log;

import module.sensor.sensor.vendor.davis.WeatherLink.WeatherLink;

public class SupportedSensors {

    private static final List<SupportedSensorGroup> lstSuppSensorGroup = new ArrayList<SupportedSensorGroup>();

    private static void initSupportedSensorInformation() throws Exception {
        if (lstSuppSensorGroup.size() == 0) {
            log.info("initSupportedSensorInformation");
            addSensorGroup(WeatherLink.class);
        }
    }

    private static void addSensorGroup(Class<? extends SensorGroupInterface> sensorGroup) throws Exception {
        log.debug("  sensor group:" + sensorGroup.getName());
        SupportedSensorGroup ssg = new SupportedSensorGroup();
        SensorGroupInterface sg = sensorGroup.newInstance();
        ssg.setGroupClass(sensorGroup);
        ssg.setName(sg.getName());
        ssg.setDescription(sg.getDescription());

        // sensor list
        List<Class<? extends SensorInterface>> lstS = sg.getSensorList();
        for (Class<? extends SensorInterface> cs : lstS) {
            log.debug("    sensor:" + cs.getName());
            ssg.addSensor(cs);
        }

        lstSuppSensorGroup.add(ssg);

    }

    public static List<SupportedSensorGroup> getSupportedSensors() throws Exception {
        initSupportedSensorInformation();
        
        return lstSuppSensorGroup;
    }

    public static SupportedSensorGroup getSensorsGroup(String groupClass) throws Exception {
        SupportedSensorGroup ssg = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.groupClass.getName().toString().equals(groupClass)) {
                ssg = _ssg;
                break;
            }
        }

        return ssg;
    }

    public static SupportedSensorGroup getSensorsGroupByClass(Class<? extends SensorGroupInterface> groupClass) throws Exception {
        SupportedSensorGroup ssg = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.groupClass.equals(groupClass)) {
                ssg = _ssg;
                break;
            }
        }

        return ssg;
    }

    public static SupportedSensorGroup getSensorsGroup(Integer groupIndex) throws Exception {
        SupportedSensorGroup ssg = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.getIndex().intValue() == groupIndex.intValue()) {
                ssg = _ssg;
                break;
            }
        }

        return ssg;
    }

    public static SupportedSensor getSensors(String groupClass, String sensorClass) throws Exception {
        SupportedSensor s = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.groupClass.getName().toString().equals(groupClass)) {
                for (SupportedSensor _s : _ssg.getSensorList()) {
                    if (_s.getSensorClass().getName().toString().equals(sensorClass)) {
                        s = _s;
                        break;
                    }
                }
            }
        }

        return s;
    }

    public static SupportedSensor getSensorsByClass(Class<? extends SensorGroupInterface> groupClass, Class<? extends SensorInterface> sensorClass) throws Exception {
        SupportedSensor s = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.groupClass.equals(groupClass)) {
                for (SupportedSensor _s : _ssg.getSensorList()) {
                    if (_s.equals(sensorClass)) {
                        s = _s;
                        break;
                    }
                }
            }
        }

        return s;
    }

    public static SupportedSensor getSensors(Integer groupIndex, Integer sensorIndex) throws Exception {
        SupportedSensor s = null;
        for (SupportedSensorGroup _ssg : getSupportedSensors())
        {
            if (_ssg.getIndex().intValue() == groupIndex.intValue()) {
                for (SupportedSensor _s : _ssg.getSensorList()) {
                    if (_s.getIndex().intValue() == sensorIndex.intValue()) {
                        s = _s;
                        break;
                    }
                }
            }
        }

        return s;
    }
    
}
