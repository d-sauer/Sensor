package controllers.admin;

import java.lang.reflect.Field;

import module.core.models.DBCUser;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.html.HInput;
import util.hlib.html.HUtil;
import util.logger.log;
import controllers.ModuleController;

public class KorisnikEdit extends Controller {

    public static Result loadModule(Long userId) throws Exception {
        log.debug("User ID: " + userId);
        DBCUser dbu = DBCUser.findById(userId);

        Html h = views.html.admin.KorisnikEdit.render(dbu, null);

        return ok(ModuleController.result(KorisnikEdit.class, h));
    }

    public static Result refresh() throws Exception {
        HDataContainer hdc = CallController.getRequestData();

        HInput hUserId = HInput.load(hdc, "user_id");
        Long userId = Long.parseLong(hUserId.getAttrValue());
        log.debug("User id:" + userId);

        return loadModule(userId);
    }

    public static Result saveUserData(Long userId) throws Exception {
        log.debug("User id:" + userId);
        HDataContainer hdc = CallController.getRequestData();

        DBCUser dbu = DBCUser.findById(userId);
        Field[] fields = DBCUser.class.getFields();

        boolean hasChanges = false;
        for (Field field : fields) {
            String fieldName = field.getName();

            HData hd = HUtil.getHData(hdc, "id", fieldName);

            if (hd != null) {
                boolean hasChange = false;
                String value = hd.get("value");
                log.debug(fieldName + " (" + field.getType().toString() + ")" + " = " + value);

                if ("email".equals(fieldName)) {
                    hasChange = true;
                    dbu.email = value;
                }
                else if ("userName".equals(fieldName)) {
                    hasChange = true;
                    dbu.userName = value;
                }
                else if ("password".equals(fieldName)) {
                    hasChange = true;
                    dbu.password = value;
                }
                else if ("firstName".equals(fieldName)) {
                    hasChange = true;
                    dbu.firstName = value;
                }
                else if ("lastName".equals(fieldName)) {
                    hasChange = true;
                    dbu.lastName = value;
                }
                else if ("phoneLand".equals(fieldName)) {
                    hasChange = true;
                    dbu.phoneLand = value;
                }
                else if ("phoneMobile".equals(fieldName)) {
                    hasChange = true;
                    dbu.phoneMobile = value;
                }
                else if ("city".equals(fieldName)) {
                    hasChange = true;
                    dbu.city = value;
                }
                else if ("postNumber".equals(fieldName)) {
                    hasChange = true;
                    dbu.postNumber = value;
                }
                else if ("street".equals(fieldName)) {
                    hasChange = true;
                    dbu.street = value;
                }
                else if ("active".equals(fieldName)) {
                    hasChange = true;
                    dbu.active = Integer.parseInt(value);
                }

                
                if (hasChange == true) {
                    hasChanges = true;
                    log.debug("update " + fieldName + " = " + value);
                }
            }
        }

         if (hasChanges == true) {
         dbu.update();
         }

        return loadModule(userId);
    }
}
