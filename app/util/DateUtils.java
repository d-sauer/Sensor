package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat defaultDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getFormatedDateTime(Long miliseconds) {
        return defaultDateTimeFormat.format(new Date(miliseconds));
    }

    public static String getFormatedDateTime(Long miliseconds, String defaultOnNULL) {
        if (miliseconds == null)
            return defaultOnNULL;
        else
            return defaultDateTimeFormat.format(new Date(miliseconds));
    }

    public static String getFormatedDateTime(Date date) {
        return defaultDateTimeFormat.format(date);
    }

    public static String getFormatedDateTime(Date date, String defaultOnNULL) {
        if (date == null)
            return defaultOnNULL;
        else
            return defaultDateTimeFormat.format(date);
    }

    public static String getFormatedDate(Long milisecondDate) {
        return defaultDateFormat.format(new Date(milisecondDate));
    }

    public static String getFormatedDate(Date date) {
        return defaultDateFormat.format(date);
    }

    /**
     * Parse tima from UTC
     * 
     * @param date
     * @param format
     * @return
     * @throws Exception
     */
    public static Date getDate(String date, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    /**
     * yyyy - year (2012)
     * MM - month (04)
     * dd - day in month
     * 
     * HH - hour (05)
     * mm - minute (08)
     * ss - seconds
     * 
     * <br/>
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
     * 
     * @param format
     * @param date
     * @return
     */
    public static String getFormatedDate(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);
    }

    public static String getFormatedDate(String format, Date date, String _default) {
        if (format != null && date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            return sdf.format(date);
        } else
            return _default;
    }

    /**
     * Reset hour, minute, seconds, and milisecondt to zero
     * 21.05.2012 12:55:33.445 => 21.05.2012 00:00:00.000
     * 
     * @param date
     * @return
     */
    public static Date getBeginOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * Reset hour, minute, seconds, and milisecondt to zero
     * 21.05.2012 12:55:33.445 => 21.05.2012 23:59:59.999
     * 
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return c.getTime();
    }

    public static Long getSeconds(Long miliseconds) {
        return miliseconds / (1000);
    }

    public static Long getMinutes(Long miliseconds) {
        return miliseconds / (1000 * 60);
    }

    public static Long getHours(Long miliseconds) {
        return miliseconds / (1000 * 60 * 60);
    }

    public static Long getDays(Long miliseconds) {
        return miliseconds / (1000 * 60 * 60 * 24);
    }
}
