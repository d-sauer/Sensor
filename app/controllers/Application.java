package controllers;

import module.sensor.Login;
import module.sensor.Login.LoginData;
import module.sensor.Login.LoginStatus;
import play.mvc.Controller;
import play.mvc.Result;
import util.NumberUtils;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTagUtil;
import util.hlib.html.HHtml;
import util.hlib.html.HInput;
import util.hlib.html.HSelect;
import util.hlib.html.HUtil;
import util.session.SessionData;
import views.html.index;

public class Application extends Controller {
    
    public static Result call(String method) throws Exception {
        return CallController.call(Application.class, method);
    }

    public static Result index() {
        return ok(index.render("Aplikacija NIJE sprema.."));
    }

    public static Result login() throws Exception {
        HDataContainer hdc = new HDataContainer(request().body().asJson());

        HInput email = HInput.load(hdc, "userName");
        HInput password = HInput.load(hdc, "userPass");

        email.setCssClass().remove("not_valid");
        password.setCssClass().remove("not_valid");

        String msg = null;
        HData hd = new HData(hdc, "data");

        if (HUtil.isNotBlank("value", email, password)) {
            String userName = email.getAttrValue();
            char[] userPass = password.getAttrValue().toCharArray();

            password.setAttrValue("");
            hdc.remove(email);

            Login login = new Login();
            LoginData loginData = login.login(userName, userPass);
            if (loginData.loginStat == LoginStatus.USER_NOT_EXISTS) {
                msg = "Korisnički račun s unesenom email adresom nije pronađen";
                email.setCssClass("not_valid");
            }
            else if (loginData.loginStat == LoginStatus.INCORECT_PASSWORD) {
                msg = "Lozinka nije ispravna";
                password.setCssClass("not_valid");
            }

            if (loginData.loginStat == LoginStatus.LOGIN_GRANTED) {
                hd.addValue("login", loginData.loginStat.toString());
                HHtml hPrijava = new HHtml(hdc, "#open_prijava");
                hPrijava.setInnerHTML("Odjava");

                SessionData sd = new SessionData();
                Application.session().clear();
                Application.session().put("user", userName);
                Application.session().put("uuid", sd.getUUID());

                sd.setSession(Application.session());
                sd.setUserId(loginData.user!=null ? loginData.user.id : null);
                sd.isAdmin(loginData.isAdmin);
                sd.save();
            }

        } else {
            if (HUtil.isBlank(email, "value")) {
                msg = "Unesite email adresu";
                email.setCssClass("not_valid");
            } else if (HUtil.isBlank(password, "value")) {
                msg = "Unesite lozinku";
                password.setCssClass("not_valid");
            }
        }

        if (msg != null) {
            hd.addValue("validation_msg", msg);
        }

        return ok(hdc.getJsonString());
    }

    public static Result logout() throws Exception {
        // Clear session
        Application.session().clear();

        return redirect("/");
    }

    public static Long getAdminSelUser() throws Exception {
        Long userId = null;
        HDataContainer hdc = CallController.getRequestData();
        HSelect hs = HTagUtil.getElementById(hdc, "admin_select_user", HSelect.class);
    
        if (hs != null)
            userId = NumberUtils.longOf(hs.getSelectedValue());
    
        return userId;
    }

}