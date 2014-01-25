package module.sensor.sensor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;

import module.sensor.models.DBSUserSensor;
import module.sensor.models.DBSensor;
import play.db.ebean.Model;
import util.db.EbeanUtils;
import util.logger.log;

public class AvailableSensorsData {

    private Long userId;
    private Set<AvailableSensor> las = new HashSet<AvailableSensor>();

    public AvailableSensorsData(Long userId) {
        this.userId = userId;
    }

    public Set<AvailableSensor> getAvailableSensorData() throws Exception {
        searchAllTablesWithData();
        searchByProperties();

        return las;
    }

    /**
     * Pretražuje sve statističke tablice i njihova polja dali imaju vrijednosti.
     * Ako imaju vrijednosti tada se dodaju u popis kandidata koje će korisnik moći odabrati i staviti u grupu senzora
     * @throws Exception 
     */
    private void searchAllTablesWithData() throws Exception {
        log.info("search available sensor by table data for user %d", userId);
        
        List<Class<? extends Model>> lSenDataClass = DBSensor.getConnectedTables();

        // Dohvati nazive tablica
        for (Class<? extends Model> cSD : lSenDataClass) {
            String tableName = EbeanUtils.getTableName(cSD);
            if (tableName != null) {
                // Dohvati polja koja su markirana kao podatkovna
                Field[] fields = cSD.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(SensorTableDataField.class) && field.isAnnotationPresent(Column.class)) {
                        String fieldName = field.getName();
                        Column column = field.getAnnotation(Column.class);
                        if (column != null && column.name() != null) {
                            String columnName = column.name();

                            // Pretraži tablicu za poljem
                            log.debug("Search table for data: %s.%s", tableName, columnName);

                            String sql = "SELECT sd.sensor_id, s.group_id, COUNT(sd." + columnName + ") AS broj " +
                                         "FROM " + tableName + " sd, s_sensors s " +
                                         "WHERE sd.sensor_id=s.id " +
                                         "  AND s.user_id=" + userId + " " +
                                         "GROUP BY sd.sensor_id, s.group_id " +
                                         "HAVING COUNT(sd." + columnName + ") > 0";
                            
                            ResultSet rs = EbeanUtils.executeQuery(sql);
                            while(rs.next()) {
                                Long tSensorId = rs.getLong("sensor_id");
                                Long tGroupId = rs.getLong("group_id");
                                Long tBroj = rs.getLong("broj");
                                
                                if (tBroj!=null && tBroj !=0) {
                                    AvailableSensor as = new AvailableSensor();
                                    as.sensorId = tSensorId;
                                    as.groupId = tGroupId;
                                    as.className = cSD;
                                    as.tableName = tableName;
                                    as.columnName = columnName;
                                    
                                    // dohvatiti od korisnika labelu i description, ako postoji, ako ne, onda učitaj default
                                    DBSUserSensor dbUS = DBSUserSensor.findByFK(userId, tSensorId, tableName, columnName);
                                    if (dbUS!=null) {
                                        as.name = dbUS.label;
                                        as.description = dbUS.description;
                                    } else {
                                        as.loadSensorLabelAndDescription();
                                    }
                                    
                                    las.add(as);
                                    log.debug(as.toString());
                                } else {
                                    log.debug("  (no data) %s.%s = %d, sensor id: %d, group id: %d", tableName, columnName, tBroj, tSensorId, tGroupId);
                                }

                                
                            }
                            rs.close();
                        }
                    }
                }
            }
        }

    }
    
    
    private void searchByProperties() throws Exception {
        log.info("search available sensor by user properties, for user %d", userId);
        
        HashMap<Long, SensorInterface> sHash = new HashMap<Long, SensorInterface>();
        HashMap<Long, DBSensor> dbHash = new HashMap<Long, DBSensor>();
        
        String sql = "SELECT ssp.sensor_id, s.group_id, ssp.property, s.sensor_class " +
        		     "FROM s_sensor_properties ssp, s_sensors s " +
                     "WHERE ssp.sensor_id=s.id " +
                     "  AND s.user_id=" + userId + " " +
                     "  AND ssp.value IS NOT NULL";
        
        ResultSet rs = EbeanUtils.executeQuery(sql);
        while(rs.next()) {
            Long tSensorId = rs.getLong("sensor_id");
            Long tGroupId = rs.getLong("group_id");
            String tProperty = rs.getString("property");
            String tClass = rs.getString("sensor_class");
            
            boolean isFind = false;
            DBSensor s = dbHash.get(tSensorId);
            if (s == null) {
                s = DBSensor.findById(tSensorId);
                if (s != null)
                    dbHash.put(tSensorId, s);
            }
            
            SensorInterface si = sHash.get(tSensorId);
            if (si == null) {
                si = s.getSensorClass();

                if (si != null)
                    sHash.put(tSensorId, si);
            }
            
            
            Field [] fields = si.getClass().getDeclaredFields();
            for(Field field : fields) {
                if (field.isAnnotationPresent(SensorPropertyDbDescription.class) && field.getType().isAssignableFrom(SensorProperty.class)) {
                    SensorProperty sp = (SensorProperty)field.get(si);
                    String name = sp.properties().name();
                    if (name!=null && name.equals(tProperty)) {
                        SensorPropertyDbDescription spDesc = field.getAnnotation(SensorPropertyDbDescription.class);

                        AvailableSensor as = new AvailableSensor();
                        as.sensorId = tSensorId;
                        as.groupId = tGroupId;
                        as.className = si.getClass();
                        as.tableName = spDesc.tableName();
                        as.columnName = spDesc.columnName();
                        
                        DBSUserSensor dbUS = DBSUserSensor.findByFK(userId, tSensorId, as.tableName, as.columnName);
                        if (dbUS!=null) {
                            as.name = dbUS.label;
                            as.description = dbUS.description;
                        } else {
                            as.loadSensorLabelAndDescription();
                        }

                        las.add(as);
                        log.debug(as.toString());
                        isFind = true;
                    }
                }
            }
            
            
            
            if (isFind == false)
                log.debug("  (no data) sensor id: %d, group id: %d, property: %s, class: %s", tSensorId, tGroupId, tProperty, tClass);
        }
        rs.close();
        
        
    }

}
