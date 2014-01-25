package util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import util.logger.log;

public class FileUtils {

    private static final AtomicInteger    fileIndexer  = new AtomicInteger(0);
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFSPath() {
        String value = AppUtils.getAppProperty("temp_fs");

        if (value == null) {
            value = AppUtils.getAppPath() + File.separator + "FS";
        }

        // chack if FS exists
        File f = new File(value);
        if (!f.exists()) {
            log.info("Create dir:" + value);
            f.mkdirs();
        }

        return value;
    }

    public static String getNextFileNamePrefix() {
        String name = fileDateFormat.format(new Date()) + " " + fileIndexer.getAndIncrement();

        return name;
    }
    
    /**
     * Connect given fileFoldes with system specific path separator from java.io.File
     * @param fileFolder
     * @return
     */
    public static String path(String ... fileFolder) {
        StringBuilder path = new StringBuilder();
        for(int i = 0; i < fileFolder.length; i++) {
            String tmp = fileFolder[i];
            
            path.append(tmp);
            
            if (i < (fileFolder.length - 1) && !tmp.endsWith(File.separator))
                path.append(File.separator);
        }
        
        return path.toString();
    }

}
