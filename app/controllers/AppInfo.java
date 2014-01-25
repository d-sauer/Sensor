package controllers;

import util.logger.log;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppInfo {
    private static final Boolean isModeDebug;

    static {
        String adminUser = ConfigFactory.load().getString("app_mode");
        if (adminUser.equalsIgnoreCase("DEBUG"))
            isModeDebug = true;
        else
            isModeDebug = false;
    }

    public static boolean isModeDebug() {
        if (isModeDebug == null) {
            String adminUser = ConfigFactory.load().getString("app_mode");
            if (adminUser.equalsIgnoreCase("DEBUG"))
                return true;
            else
                return false;
        } else
            return isModeDebug;
    }

    public static String getConfigParam(String param) {
        String value = null;
        try {
            Config cfg = ConfigFactory.load();
            if (cfg != null) {
                value = cfg.getString(param);
            }
        } catch (Exception e) {
            log.exception(e);
        }
        return value;
    }
}
