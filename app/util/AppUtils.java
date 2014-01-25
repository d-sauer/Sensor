package util;


import play.Play;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class AppUtils {

    /**
     * Get application properties from conf/application.conf
     * 
     * @param propertyName
     * @return
     */
    public static String getAppProperty(String propertyName) {
        String value = null;
        try {
            value = ConfigFactory.load().getString(propertyName);
        } catch(ConfigException ce) {
            
        }
        
        return value;
    }

    public static String getAppPath() {
        String value = Play.application().path().getAbsolutePath();

        return value;
    }

}
