package controllers.sensor;

import module.core.models.DBCUser;
import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.StringUtils;
import util.Http.CallController;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HInput;
import util.logger.log;
import util.session.SessionData;
import controllers.ModuleController;

public class UserData extends Controller {

    public static Result refresh() throws Exception {
        return loadModule();
    }

    public static Result loadModule() throws Exception {
        SessionData sd = SessionData.get();

        if (sd != null) {
            log.debug("User ID: %d", sd.getUserId());
            return showData(sd.getUserId());
        } else {
            log.warning("session not found");
            return redirect(controllers.routes.Application.index());
        }
    }

    private static Result showData(Long userId) throws Exception {
        DBCUser dbu = DBCUser.findById(userId);


        Html h = views.html.sensor.UserData.render(dbu);
        return ok(ModuleController.result(UserData.class, h));
    }

    public static Result save() throws Exception {
        HDataContainer hdc = CallController.getRequestData();
        HInput hEmail = HTagUtil.getElementById(hdc, "email", HInput.class);
        HInput hPassword1 = HTagUtil.getElementById(hdc, "password1", HInput.class);
        HInput hPassword2 = HTagUtil.getElementById(hdc, "password2", HInput.class);
        HInput hFirstName = HTagUtil.getElementById(hdc, "firstName", HInput.class);
        HInput hLastName = HTagUtil.getElementById(hdc, "lastName", HInput.class);
        HInput hPhoneLand = HTagUtil.getElementById(hdc, "phoneLand", HInput.class);
        HInput hPhoneMobile = HTagUtil.getElementById(hdc, "phoneMobile", HInput.class);
        HInput hCity = HTagUtil.getElementById(hdc, "city", HInput.class);
        HInput hPostNumber = HTagUtil.getElementById(hdc, "postNumber", HInput.class);
        HInput hStreet = HTagUtil.getElementById(hdc, "street", HInput.class);
        
        
        //
        // Provjera dali su sva potrebna polja popunjena
        //
        boolean isValid = true;
        boolean isPassValid = false;
        if (hEmail.isBlank("value")) {
            isValid = false;
            hEmail.setCssClass().add("not_valid");
        } else {
            hEmail.setCssClass().remove("not_valid");
        }
        
        if (hPassword1.isBlank("value") || hPassword2.isBlank("value")) {
            isValid = false;
            hEmail.setCssClass().add("not_valid");
            
        } else {
            String pass1 = hPassword1.getAttrValue(); 
            String pass2 = hPassword2.getAttrValue();
            
            if (pass1.equals(pass2)) {
                isPassValid = true;
                hPassword1.setCssClass().remove("not_valid");
                hPassword2.setCssClass().remove("not_valid");
                
            } else {
                isValid = false;
                isPassValid = false;

                hPassword1.setCssClass().add("not_valid");
                hPassword2.setCssClass().add("not_valid");
            }
        }
        
        
        // Ako je lozinka validna, nemoj vraćati nazad podatke o lozinci, neka ostane na client strani nepromijenjeno
        if (isValid == false && isPassValid == true)
            hdc.remove(hPassword1, hPassword2);
        
        
        if (isValid == true) {
            SessionData sd = SessionData.get();
            Long userId = sd.getUserId();
            DBCUser dbu = DBCUser.findById(userId);
            
            // Provjeri dali je korisnik mijenjao lozinku
            if (isPassValid) {
                String pass = hPassword1.getAttrValue();
                String dbPass = StringUtils.replaceChars(dbu.password, ".", "*");
                
                if (!dbPass.equals(pass))
                    dbu.password = hPassword1.getAttrValue();
            }
            
            if (isValid == true) {
                dbu.email = hEmail.getAttrValue();
                dbu.firstName = hFirstName.getAttrValue();
                dbu.lastName = hLastName.getAttrValue();
                dbu.phoneLand = hPhoneLand.getAttrValue();
                dbu.phoneMobile = hPhoneMobile.getAttrValue();
                dbu.city = hCity.getAttrValue();
                dbu.postNumber = hPostNumber.getAttrValue();
                dbu.street = hStreet.getAttrValue();
                dbu.save();
            }
        }
        
        
        return ok(hdc.getJsonString());
    }
    
}
