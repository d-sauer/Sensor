package util;

public class NumberUtils {

    public static Double doubleOf(String doubleValue) {
        return doubleOf(doubleValue, null);
    }

    public static Double doubleOf(String doubleValue, Double defaultValue) {
        if (doubleValue != null)
            try {
                return Double.valueOf(doubleValue);
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        else
            return null;
    }

    public static Integer integerOf(String integerValue) {
        return integerOf(integerValue, null);
    }

    public static Integer integerOf(String integerValue, Integer defaultValue) {
        if (integerValue != null)
            try {
                return Integer.valueOf(integerValue);
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        else
            return null;
    }

    public static Long longOf(String longValue) {
        return longOf(longValue, null);
    }

    public static Long longOf(String longValue, Long defaultValue) {
        if (longValue != null)
            try {
                return Long.valueOf(longValue);
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        else
            return null;
    }
}
