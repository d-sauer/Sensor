package controllers;

import java.util.LinkedList;
import java.util.List;

import module.core.models.DBCUser;

import org.codehaus.jackson.JsonNode;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;
import util.hlib.html.HDiv;
import util.hlib.html.HHtml;
import util.hlib.html.HInput;
import util.hlib.html.HUtil;

public class Registracija extends Controller {

    public static Result call(String method) throws Exception {
        return CallController.call(Registracija.class, method);
    }

    public static Result registracija() {
        Html html = views.html.registracija.render("");

        return ok(html);
    }

    public static Result ajax_submit() throws Exception {
        RegistracijaData rd = new RegistracijaData(request().body().asJson());
        HDataContainer hdc = rd.doValidation();

        return ok(hdc.getJsonString());
    }

    public static class RegistracijaData {

        private HDataContainer hc = null;

        private HInput         email1;
        private HInput         email2;
        private HInput         password1;
        private HInput         password2;

        private HInput         firstName;
        private HInput         lastName;
        private HInput         phoneLand;
        private HInput         phoneMobile;

        private HInput         city;
        private HInput         postNumber;
        private HInput         address;

        public RegistracijaData(JsonNode json) throws Exception {
            hc = new HDataContainer(json);
            email1 = HTagUtil.getElementById(hc, "email1", HInput.class);
            email2 = HTagUtil.getElementById(hc, "email2", HInput.class);
            password1 = HTagUtil.getElementById(hc, "password1", HInput.class);
            password2 = HTagUtil.getElementById(hc, "password2", HInput.class);

            firstName = HTagUtil.getElementById(hc, "firstName", HInput.class);
            lastName = HTagUtil.getElementById(hc, "lastName", HInput.class);
            phoneLand = HTagUtil.getElementById(hc, "phoneLand", HInput.class);
            phoneMobile = HTagUtil.getElementById(hc, "phoneMobile", HInput.class);

            city = HTagUtil.getElementById(hc, "city", HInput.class);
            postNumber = HTagUtil.getElementById(hc, "postNumber", HInput.class);
            address = HTagUtil.getElementById(hc, "address", HInput.class);
        }

        public HDataContainer doValidation() throws Exception {
            boolean isValid = true;
            List<String> msgs = new LinkedList<String>();

            //
            // EMAIL
            //
            if (!HUtil.isSet(email1, "value") || !HUtil.isSet(email2, "value")) {
                isValid = false;
                msgs.add("Upišite e-mail adresu");

                if (!HUtil.isSet(email1, "value"))
                    email1.setCssClass().add("not_valid");
                else
                    email1.setCssClass().remove("not_valid");

                if (!HUtil.isSet(email2, "value"))
                    email2.setCssClass().add("not_valid");
                else
                    email2.setCssClass().remove("not_valid");
            }
            else if (!HUtil.equalsByValue(email1, email2, "value")) {
                isValid = false;
                msgs.add("E-mail adrese nisu jednake");

                email1.setCssClass().add("not_valid");
                email2.setCssClass().add("not_valid");
            } else {
             // Check ig email is alredy used
                List<DBCUser> ldbu = DBCUser.find.where().like("email", email1.getAttrValue()).findList();
                if (ldbu.size() != 0) {
                    isValid = false;
                    msgs.add(0, "Korisnički račun s definiranom e-mail adresom već postoji");
                    email1.setCssClass().add("not_valid");
                    email2.setCssClass().add("not_valid");
                } else {
                    email1.setCssClass().remove("not_valid");
                    email2.setCssClass().remove("not_valid");
                }
            }

            //
            // PASSWORD
            //
            if (!HUtil.isNotBlank("value", password1, password2)) {
                isValid = false;
                msgs.add("Unesite lozinke");

                password1.setCssClass().add("not_valid");
                password2.setCssClass().add("not_valid");
            } else if (!HUtil.equalsByValue(password1, password2, "value")) {
                isValid = false;
                msgs.add("Unesite identične lozinke");

                password1.setCssClass().add("not_valid");
                password2.setCssClass().add("not_valid");
            } else {
                password1.setCssClass().remove("not_valid");
                password2.setCssClass().remove("not_valid");
            }

            //
            // first name
            //
            if (HUtil.isBlank(firstName, "value")) {
                isValid = false;
                msgs.add("Unesite ime");

                firstName.setCssClass().add("not_valid");
            } else {
                firstName.setCssClass().remove("not_valid");
            }

            //
            // last name
            //
            if (HUtil.isBlank(lastName, "value")) {
                isValid = false;
                msgs.add("Unesite prezime");

                lastName.setCssClass().add("not_valid");
            } else {
                lastName.setCssClass().remove("not_valid");
            }

            //
            // mob or land phone
            //
            boolean pm = HUtil.isBlank(phoneMobile, "value");
            boolean pl = HUtil.isBlank(phoneLand, "value");
            if ((pm == true && pl == false) || (pm == false && pl == true) || (pm == false && pl == false)) {
                phoneMobile.setCssClass().remove("not_valid");
                phoneLand.setCssClass().remove("not_valid");
            } else {
                isValid = false;
                msgs.add("Unesite broj mobitela ili broj fiksne linije");

                phoneMobile.setCssClass().add("not_valid");
                phoneLand.setCssClass().add("not_valid");
            }

            //
            // city, zip code, address
            //
            if (HUtil.isBlank("value", city, postNumber, address)) {
                isValid = false;
                msgs.add("Unesite podatke o mjestu stavnovanja");

                city.setCssClass().add("not_valid");
                postNumber.setCssClass().add("not_valid");
                address.setCssClass().add("not_valid");
            } else {
                city.setCssClass().remove("not_valid");
                postNumber.setCssClass().remove("not_valid");
                address.setCssClass().remove("not_valid");
            }

            //
            // Validation OK
            //
            if (isValid == true) {
                DBCUser dbu = new DBCUser();
                dbu.email = email1.getAttrValue();
                dbu.password = password1.getAttrValue();
                dbu.firstName = firstName.getAttrValue();
                dbu.lastName = lastName.getAttrValue();
                dbu.userName = email1.getAttrValue().toLowerCase();
                
                dbu.phoneLand = phoneLand.getAttrValue();
                dbu.phoneMobile = phoneMobile.getAttrValue();
                dbu.city = city.getAttrValue();
                dbu.postNumber = postNumber.getAttrValue();
                dbu.street = address.getAttrValue();
                
                dbu.active = -1;
                
                dbu.save();

                msgs.add("Registracija uspješno završena");

                HDiv div = new HDiv("registration_form");
                div.setStyle("display: none");
                hc.add(div);
                
                HTag regButton = new HTag(hc);
                regButton.setSelector().setId("regButton");
                regButton.setStyle("display: none");
            }

            HData data = new HData("data");
            data.addValue("isValid", isValid);

            // Add validation messages
            HHtml vm = new HHtml();
            vm.setSelector().setId("validation_msg");

            Html html = views.html.registracija_validation_msg.render(msgs);
            String s = html.body();
            vm.setInnerHTML(s);
            
            hc.add(vm, data);
            // HDataContainer hc = new HDataContainer(vm, email1, email2, data);
            return hc;
        }
    }
}
