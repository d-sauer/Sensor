package module.sensor.sensor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SensorProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    private String defaultValue;

    private String label;
    private String description;
    private boolean useDefaultValue;
    
    private String userLabel;
    private String userDescription;

    private Properties properties = new Properties(this);

    public SensorProperty(String name) {
        this.name = name;
    }

    public static class Properties {
        private SensorProperty sp;

        public Properties(SensorProperty sp) {
            this.sp = sp;
        }

        public Properties name(String name) {
            sp.name = name;
            return this;
        }

        public String name() {
            return sp.name;
        }

        public String value() {
            if ((sp.value == null || sp.value.length() == 0) && sp.useDefaultValue == true)
                return sp.defaultValue;
            else if (sp.value == null || sp.value.length() == 0)
                return null;
            else
                return sp.value.trim();
        }

        public <T> T value(Class<T> type) throws Exception {
            String value = value();
            if (value != null) {
                if (type.isAssignableFrom(Integer.class)) {
                    return (T) Integer.valueOf(value);
                }
                else if (type.isAssignableFrom(Double.class)) {
                    return (T) Double.valueOf(value);
                }
                else {
                    throw new Exception("Unknown type " + type.getClass().getName() + " to parse value '" + value + "'");
                }
            } else {
                return null;
            }
        }

        public Properties value(String value) {
            sp.value = value;
            return this;
        }

        public void valueToDefault() {
            sp.value = sp.defaultValue;
        }

        public String defaultValue() {
            return sp.defaultValue;
        }

        public Properties defaultValue(String defaultValue, Boolean useDefaultValue) {
            sp.defaultValue = defaultValue;
            sp.useDefaultValue = useDefaultValue;
            return this;
        }

        public Properties defaultValue(String defaultValue) {
            sp.defaultValue = defaultValue;
            return this;
        }

        public String label() {
            return sp.label;
        }

        public Properties label(String label) {
            sp.label = label;
            return this;
        }

        public String description() {
            return sp.description;
        }

        public Properties description(String description) {
            sp.description = description;
            return this;
        }

        public String userLabel() {
            return sp.userLabel;
        }
        
        public Properties userLabel(String label) {
            sp.userLabel = label;
            return this;
        }

        public String userDescription() {
            return sp.userDescription;
        }
        
        public Properties userDescription(String description) {
            sp.userDescription = description;
            return this;
        }

        public SensorProperty get() {
            return sp;
        }

    }

    public Properties properties() {
        return properties;
    }

    public static Properties builder(String name) {
        SensorProperty sp = new SensorProperty(name);
        return sp.properties();
    }

    public SensorProperty registerProperty(List<SensorProperty> lsp) {
        if (lsp == null)
            lsp = new ArrayList<SensorProperty>();

        lsp.add(this);

        return this;
    }

    public static void setProperty(List<SensorProperty> properties, String property, String value) {
        for (SensorProperty sp : properties) {
            if (sp.properties().name().equals(property)) {
                sp.properties().value(value);
                break;
            }
        }
    }

    public static void loadGroupProperties(SensorGroupInterface sg, String data) {
        HashMap<String, String> hm = loadProperties(data);
        for (Entry<String, String> e : hm.entrySet()) {
            sg.setProperty(e.getKey(), e.getValue());
        }
    }

    public static void loadSensorProperties(SensorInterface s, String data) {
        HashMap<String, String> hm = loadProperties(data);
        for (Entry<String, String> e : hm.entrySet()) {
            s.setProperty(e.getKey(), e.getValue());
        }
    }

    public static HashMap<String, String> loadProperties(String data) {
        HashMap<String, String> hm = new HashMap<String, String>();

        if (data != null) {
            String s = "]=\\[";
            String[] props = data.split("]\\[");

            for (String prop : props) {
                String name = null;
                String value = null;

                if (prop.startsWith("["))
                    prop = prop.substring(1);
                if (prop.endsWith("]"))
                    prop = prop.substring(0, prop.length() - 1);

                String[] tmp = prop.split(s);
                if (tmp.length == 2) {
                    name = tmp[0];
                    value = (tmp.length == 2 && tmp[1].length() != 0) ? tmp[1] : null;

                } else if (tmp.length == 1) {
                    name = tmp[0];
                }

                if (name != null)
                    hm.put(name, value);
            }
        }

        return hm;
    }

    public static String writeGroupProperties(SensorGroupInterface sg) {
        return writeProperties(sg.getProperties());
    }

    public static String writeSensorProperties(SensorInterface s) {
        return writeProperties(s.getProperties());
    }

    public static String writeProperties(List<SensorProperty> properties) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < properties.size(); i++) {
            SensorProperty sp = properties.get(i);

            String encode = sp.properties().value();
            sb.append("[" + sp.properties.name() + "]=[" + encode + "]");
        }

        return sb.toString();
    }
    
    
    public static SensorProperty getSensorProperty(SensorInterface sensor, String tableName, String columnName) throws Exception {
        SensorProperty sp = null;
        
        Field [] fields = sensor.getClass().getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(SensorPropertyDbDescription.class)) {
                SensorPropertyDbDescription spDesc = field.getAnnotation(SensorPropertyDbDescription.class);
                
                if (spDesc.tableName().equals(tableName) && (spDesc.columnName().equals(columnName) || spDesc.modelClassColumnField().equalsIgnoreCase(columnName))) {
                    sp = (SensorProperty)field.get(sensor);
                }
                
            }
        }
        
        
        return sp;
    }

    public static SensorPropertyDbDescription getSensorPropertyDescription(Class<? extends SensorInterface> sensorClass, String fieldName) throws Exception {
        SensorPropertyDbDescription sp = null;
        
        Field [] fields = sensorClass.getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(SensorPropertyDbDescription.class) && field.getName().equals(fieldName)) {
                sp = field.getAnnotation(SensorPropertyDbDescription.class);
            }
        }
        
        return sp;
    }
    
    
    
}
