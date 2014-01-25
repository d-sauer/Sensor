package util.data;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WDate {
    /** yyyy-MM-dd HH:mm:ss */
    public static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";
    /** yyyy-MM-dd */
    public static final String yyyyMMdd = "yyyy-MM-dd";
    /** dd.MM.yyyy HH:mm:ss */
    public static final String ddMMyyyy_HHmmss = "dd.MM.yyyy HH:mm:ss";
    /** dd.MM.yyyy */
    public static final String ddMMyyyy = "dd.MM.yyyy";

    private SimpleDateFormat sdfIN;
    private SimpleDateFormat sdfOUT;
    private Date date;
    private Calendar calendar;

    public WDate() {
        
    }

    public WDate(Date date) {
        this.date = date;
    }

    /**
     * Set input and output format pattern
     * 
     * @param format
     */
    public WDate(String format) {
        sdfIN = new SimpleDateFormat(format);
        sdfOUT = sdfIN;
    }

    public WDate(String inputFormat, String outputFormat) {
        sdfIN = new SimpleDateFormat(inputFormat);
        sdfOUT = new SimpleDateFormat(inputFormat);
    }

    public WDate setFormat(SimpleDateFormat format) {
        sdfIN = format;
        sdfOUT = sdfIN;
        return this;
    }

    public WDate setInFormat(SimpleDateFormat format) {
        sdfIN = format;
        return this;
    }

    public WDate setOutFormat(SimpleDateFormat format) {
        sdfOUT = format;
        return this;
    }
    /**
     * Set input and output format
     * 
     * @param format
     * @return
     */
    public WDate setFormat(String format) {
        sdfIN = new SimpleDateFormat(format);
        sdfOUT = sdfIN;
        return this;
    }

    public WDate setInputFormat(String format) {
        sdfIN = new SimpleDateFormat(format);
        return this;
    }

    public WDate setOutputFormat(String format) {
        sdfOUT = new SimpleDateFormat(format);
        return this;
    }
    
    public WDate setBeginOfDay() {
        truncate();
        
        return this;
    }

    public WDate setEndOfDay() {
        Calendar cal = getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        calendar = cal;
        date.setTime(cal.getTimeInMillis());
        
        return this;
    }
    
    

    /**
     * Set date
     * 
     * @param date
     */
    public WDate set(Date date) {
        this.date = date;
        return this;
    }

    public WDate set(WDate date) {
        this.date = new Date(date.get().getTime());
        return this;
    }

    public WDate set(java.sql.Timestamp date) {
        this.date = new Date(date.getTime());
        return this;
    }

    public WDate set(Long dateTimeMs) {
        this.date = new Date(dateTimeMs);
        return this;
    }

    /**
     * Parse and set date
     * 
     * @param date
     */
    public void set(String date) throws Exception {
        this.date = sdfIN.parse(date);
    }

    /**
     * Set date in given format
     * 
     * @param date
     * @param format
     * @throws ParseException
     */
    public void set(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        this.date = sdf.parse(date);
    }
    
    /**
     * Set current date and time
     */
    public void setCurrentTime() {
        this.date = new Date();
    }

    /**
     * Return date
     * 
     * @return
     */
    public Date get() {
        return this.date;
    }

    public java.sql.Timestamp getTimestamp() {
        java.sql.Timestamp ts = new Timestamp(this.date.getTime());
        return ts;
    }

    /**
     * Get date and time in MS
     * @return
     */
    public Long getTimeInMillis() {
        return date.getTime();
    }

    /**
     * Get date in ms, and reset time to 00:00:00.000
     * @return
     */
    public Long getDateInMillis() {
        Calendar c = getCalendar();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return c.getTimeInMillis();
    }
    
    public Calendar getCalendar() {
        if (calendar == null)
            calendar = Calendar.getInstance();
        
        calendar.setTime(date);

        return calendar;
    }
    /**
     * get formatted date
     * 
     * @return
     */
    public SimpleDateFormat getInFormat() {
        return sdfIN;
    }

    public SimpleDateFormat getOutFormat() {
        return sdfOUT;
    }

    public String getFormatted() {
        return sdfOUT.format(date);
    }

    public String getFormatted(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Truncate date time to date
     * 2012-09-25 14:55:44 => 2012-09-25 00:00:00.000
     * 
     * @return
     */
    public WDate truncate() {
        Calendar cal = getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        date.setTime(cal.getTimeInMillis());
        return this;
    }

    /**
     * add(Calendar.DAY_OF_MONTH, -5).
     * 
     * @return
     */
    public WDate add(int CalendarField, int amount) {
        Calendar cal = getCalendar();
        cal.add(CalendarField, amount);
        
        date.setTime(cal.getTimeInMillis());
        return this;
    }

    
    
    @Override
    public String toString() {
        if (sdfOUT != null && date != null) {
            return sdfOUT.format(date);
        }
        else if (sdfOUT == null && date != null) {
            return date.toString();
        }
        return "";
    }

}