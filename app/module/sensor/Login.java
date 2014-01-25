package module.sensor;

import util.logger.log;
import module.core.models.DBCUser;

import com.typesafe.config.ConfigFactory;

public class Login {

    public static enum LoginStatus {
        LOGIN_GRANTED,
        LOGIN_NOT_GRANTED,
        INCORECT_PASSWORD,
        USER_NOT_EXISTS
    }
    
    public static class LoginData {
        public LoginStatus loginStat;
        public DBCUser user;
        public boolean isAdmin = false;
    }

    public LoginData login(String userName, char[] userPass) {
        LoginData returnCode = check(userName, userPass);
        return returnCode;
    }

    /**
     * Return:
     * 1 - Login granted
     * 0 - Login not granted
     * -1 - Incorect password
     * -2 - User name not exists
     * 
     * @param userName
     * @param userPass
     * @return
     */
    public LoginData check(String userName, char[] userPass) {
        log.info("Login check for user: %s", userName);
        
        LoginData ld = new LoginData();
        ld.loginStat = LoginStatus.LOGIN_NOT_GRANTED;

        String msg = "";

        DBCUser dbu = DBCUser.findByUserName(userName);
        if (dbu != null) {
            // provjeri lozinku
            boolean isEq = Login.isPasswordEquals(dbu.password.toCharArray(), userPass);
            if (isEq == false) {
                msg = "Lozinka nije ispravna";
                ld.loginStat = LoginStatus.INCORECT_PASSWORD;
            } else {
                ld.loginStat = LoginStatus.LOGIN_GRANTED;
                ld.user = dbu;
            }
        } else {
            if (isAdmin(userName, userPass)) {
                ld.loginStat = LoginStatus.LOGIN_GRANTED;
                ld.isAdmin = true;
            } else {
                msg = "Korisnički račun s unesenom email adresom nije pronađen";
                ld.loginStat = LoginStatus.USER_NOT_EXISTS;
            }
        }

        return ld;
    }

    private boolean isAdmin(String userName, char[] userPass) {
        // Configuration conf = Play.current().configuration();
        String adminUser = ConfigFactory.load().getString("admin_user");
        String adminPass = ConfigFactory.load().getString("admin_pass");

        if (adminUser != null && adminPass != null) {
            if (adminUser.equals(userName) && isPasswordEquals(adminPass.toCharArray(), userPass))
                return true;
        }

        return false;
    }

    public static boolean isPasswordEquals(char[] pass1, char[] pass2) {
        boolean isEq = false;
        if (pass1 != null && pass2 != null) {
            for (int i = 0; i < pass1.length; i++) {
                if (pass1[i] != pass2[i]) {
                    isEq = false;
                    break;
                } else {
                    isEq = true;
                }
            }
        }

        return isEq;
    }
}
