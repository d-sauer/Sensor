package util;

import java.util.Collection;
import java.util.List;

public class StringUtils {

    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     * 
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(CharSequence ... lcs) {
        boolean isEmpty = false;
        for(CharSequence cs : lcs) {
            isEmpty = isEmpty(cs);
            
            if (isEmpty == true)
                break;
        }
        return isEmpty;
    }

    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     * 
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty ("") and not null.
     * </p>
     * 
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    /**
     * <p>
     * Checks if a CharSequence is whitespace, empty ("") or null.
     * </p>
     * 
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty (""), not null and not whitespace
     * only.
     * </p>
     * 
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     *         not empty and not null and not whitespace
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null)
            return true;
        else if (str1 == null && str2 != null)
            return false;
        else if (str1 != null && str2 == null)
            return false;
        else if (str1 != null && str2 != null) {
            return str1.equals(str2);
        } else {
            return false;
        }
    }

    public static String toString(List<String> lst, String separator) {
        StringBuilder sb = new StringBuilder();
        int size = lst.size();
        for(int i = 0; i < size; i++) {
            sb.append(lst.get(i));
            
            if (i < (size - 1) && separator!=null) 
                sb.append(separator);            
        }
        
        return sb.toString();
    }
    
    public static String nvl(Object obj) {
        return nvl(obj, "null");
    }
    
    public static String nvl(Object obj, String defaultValue) {
        if (obj==null)
            return defaultValue;
        else
            return obj.toString();
    }
    
    public static String replaceChars(String value, String regex, String replacement) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for(int c = 0; c < value.length(); c++) {
                String ch = "" + value.charAt(c);
                if (ch.matches(regex))
                    sb.append(replacement);
            }
        }

        return sb.toString();
    }
    
    public static String joinCollections(Collection<String> collection, String separator) {
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for (String str : collection) {
            if (c++ != 0)
                sb.append(separator);

            sb.append(str);
        }

        return sb.toString();
    }
    
}
