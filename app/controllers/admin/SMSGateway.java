package controllers.admin;

import util.data.Return;
import util.sms.BulkSms;
import util.sms.BulkSmsStatus;
import controllers.AppInfo;

public class SMSGateway {

    public static Return<BulkSmsStatus> sensSMS(String mobileNumber, String message) throws Exception {
        Return<BulkSmsStatus> ret = null;
        
        String user = AppInfo.getConfigParam("bulksms_user");
        String pass = AppInfo.getConfigParam("bulksms_pass");

        if (user != null && pass != null && user.length() != 0 && pass.length() != 0) {
            BulkSms sms = new BulkSms(user, pass);
            ret = sms.sendSMS(mobileNumber, message);
            
            return ret;
        }

        ret = new Return<BulkSmsStatus>();
        BulkSmsStatus bss = BulkSmsStatus.NO_SENDING;
        ret.setReturnObject(bss);
        
        return ret;
    }
}
