package module.sensor.sensor.vendor.davis.WeatherLink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import module.sensor.models.DBSensor;
import module.sensor.models.DBSensorData;
import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.SensorProperty;
import util.DateUtils;
import util.logger.log;

public class WeatherLinkParseFile {

    private Long    groupId;
    private Pattern pat = Pattern.compile("(\\S+)");

    public WeatherLinkParseFile(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Return TRUE if some of data is used/writed to database
     * @param file
     * @return
     * @throws Exception
     */
    public int writeFileToDatabase(File file) throws Exception {
        int countWritedLine = 0;
        if (file != null && file.exists() && file.isFile()) {
            DBSensorGroup dbSG = DBSensorGroup.findById(groupId);
            List<DBSensor> userSen = dbSG.sensors;
            //
            // Prepare RAW data to database
            //
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            int l = 0;
            while ((line = br.readLine()) != null) {
                l++;
                if (l <= 3)
                    continue;

                // write line to database
                if(writeOneReading(line, dbSG, userSen))
                    countWritedLine++;
                else {
                    log.debug("Not writed line: " + l);
                }

            }

            log.info("Write lines " + countWritedLine + "/" + l);
        } else {
            log.debug("not exists");

        }
        
        return countWritedLine;
    }

    /**
     * Write one line of sensor reading from file to database  
     * @param line
     * @param group
     * @param userSen
     * @return
     * @throws Exception
     */
    public boolean writeOneReading(String line, DBSensorGroup group, List<DBSensor> userSen) throws Exception {
        boolean isWrited = false;
        WeatherLink mg = (WeatherLink) group.getGroupClass();

        List<String> lineColumn = getLineColumns(line);
        String fDate = lineColumn.get(0);
        
        // correct time, if time is 12:MMa  12:30a=>00:30 AM
        String fTime = lineColumn.get(1);
        if (fTime.endsWith("a")) {
            fTime = fTime.replace("a", " AM");
            if (fTime.startsWith("12"))
                fTime = "00" + fTime.substring(2);
        } else if (fTime.endsWith("p")) {
            if (fTime.startsWith("12"))     
                fTime = fTime.replace("p", " AM");  // 12:00p => 12:00 AM
            else 
                fTime = fTime.replace("p", " PM");  // 01:00p => 01:00 PM
            
        }
        log.debug("time format " + lineColumn.get(1) + " => " + fTime);
        
        String dateTime = fDate + " " + fTime;
        String parseStr = mg.propPfDateFormat.properties().value() + " " + mg.propPfTimeFormat.properties().value();
        SimpleDateFormat df = new SimpleDateFormat(parseStr);

        log.info("time: " + dateTime + " parse with:" + parseStr);

        Date date = df.parse(dateTime);

        List<DBSensorData> lstSD = DBSensorData.findByDate(group, date);
        log.info("Search sensor data for date:" + DateUtils.getFormatedDateTime(date) + " group id:" + group.id);
        if (lstSD.size() == 0) {
            log.info("Insert data");

            DBSensorData usd = new DBSensorData();
            usd.data = line;
            usd.dateTime = new java.sql.Timestamp(date.getTime());
            usd.group = group;
            usd.whenCreated = new java.sql.Timestamp(new Date().getTime());
            usd.save();

            isWrited = mg.writeSensorReadings(lineColumn, usd, userSen, date);

        } else {
            log.warning("Data exists (" + lstSD.size() + ") for " + DateUtils.getFormatedDateTime(date));

        }

        return isWrited;
    }

    private List<String> getLineColumns(String line) {
        List<String> lst = new ArrayList<String>();
        Matcher m = pat.matcher(line);

        int i = 0;
        while (m.find()) {
            String g = m.group().trim();
            lst.add(g);
        }

        return lst;
    }
    
    public static String getColumn (List<String> lineColumn, SensorProperty index) throws Exception {
        Integer i = index.properties().value(Integer.class);
        return getColumn(lineColumn, i);
    }
    
    
    public static String getColumn (List<String> lineColumn, Integer index) {
        String ret = null;
        if (index != null && index < lineColumn.size()) {
            ret = lineColumn.get(index);
        }
        
        return ret;
    }

}
