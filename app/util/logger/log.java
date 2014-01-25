package util.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class log {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static Date vrijeme = new Date();
    
    private static void logging(String log) {
        vrijeme.setTime(System.currentTimeMillis());
        System.out.println(sdf.format(vrijeme) + " " + log);
    }
    

    public static void warning(String log) {
        logging("[W] " + log);        
    }

    public static void warning(String data, Object ... values) {
        warning(String.format(data, values));
    }

    public static void error(String log) {
        logging("[E] " + log);
    }

    public static void error(String data, Object ... values) {
        error(String.format(data, values));
    }

    public static String exception(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        
        logging("[X] " + sw.toString());
        
        return sw.toString();
    }

    public static void info(String log) {
        logging("[I] " + log);
    }
    public static void info(String data, Object ... values) {
        info(String.format(data, values));
    }

    public static void debug(String log) {
        logging("[D] " + log);
    }

    public static void debug(String data, Object ... values) {
        debug(String.format(data, values));
    }

    public static void trace(String log) {
        logging("[T] " + log);
    }
    
    public static void trace(String data, Object ... values) {
        debug(String.format(data, values));
    }
    
}
