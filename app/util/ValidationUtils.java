package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    /**
     * Check if CharSequence is >= min, and <= max
     * 
     * @param cs
     * @param min
     * @param max
     * @return {@code 0} - if length is ok, {@code -1} - if lenght < min, or
     *         CharSequence is null, {@code 1} if length > max
     */
    public static int isValidLength(CharSequence cs, Integer min, Integer max) {
        int r = -1;
        if (cs != null) {
            int l = cs.length();
            if (min != null && max != null) {
                if (l >= min && l <= max)
                    r = 0;
                else if (l < min)
                    r = -1;
                else if (l > max)
                    r = 1;
            }
            else if (max == null) {
                if (l < min)
                    r = -1;
            }
            else if (min == null) {
                if (l > min)
                    r = 1;
            }
        }

        return r;
    }

    public static boolean isValid(CharSequence cs, String regexPattern) {
        if (cs != null) {
            Pattern pat = Pattern.compile(regexPattern);
            Matcher mat = pat.matcher(cs);

            return mat.matches();
        } else {
            return false;
        }
    }
    
    /**
     * Return TRUE if one of objects is null
     * @param objects
     * @return
     */
    public static boolean hasNull(Object ... objects) {
        boolean isNull = false;
        for(Object o : objects) {
            if (o == null) {
                isNull = true;
                break;
            }
        }
        
        return isNull;
    }

    /**
     * Return TRUE if one of object is not null
     * @param objects
     * @return
     */
    public static boolean hasNoNull(Object ... objects) {
        boolean hasNoNull = false;
        for(Object o : objects) {
            if (o != null) {
                hasNoNull = true;
                break;
            }
        }
        
        return hasNoNull;
    }

}
