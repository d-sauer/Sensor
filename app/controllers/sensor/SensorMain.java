package controllers.sensor;

import java.util.List;

import module.sensor.menu.Menu;
import module.sensor.models.DBSUserGroup;
import play.mvc.Controller;
import play.mvc.Result;
import util.session.SessionData;
import controllers.admin.KorisniciView;
import controllers.admin.SMSGatewayConfig;
import controllers.admin.WorkersView;

public class SensorMain extends Controller {

    public static Result sensorMain() throws Exception {
        String user = SensorMain.session("user");
        if (user != null) {
            Menu menu = getMenu();
            return ok(views.html.sensor.SensorMain.render("My sensors", menu));
        } 
        else
            return redirect(controllers.routes.Application.index());
    }

    public static Menu getMenu() throws Exception {
        Menu mainContainer = new Menu();

        String userName = SensorMain.session("user");
        if (userName != null) {
            
            //
            // Admin menu
            //
            if (userName.equals("admin")) {
                Menu mainMenu = mainContainer.newSubmenuItem();
                mainMenu.setCaption("Administracija");
                
                //
                // SUBMENUs
                //
                Menu subMenu = mainMenu.newSubmenuItem();
                subMenu.setCaption("Korisnici");
                subMenu.setModule(KorisniciView.class);

                subMenu = mainMenu.newSubmenuItem();
                subMenu.setCaption("Workers");
                subMenu.setModule(WorkersView.class);

                subMenu = mainMenu.newSubmenuItem();
                subMenu.setCaption("SMS Gateway");
                subMenu.setModule(SMSGatewayConfig.class);
            }

            
            //
            // User sensor groups
            //
            Menu mUserGroup = mainContainer.newSubmenuItem();
            mUserGroup.setCaption("Očitanja");
            mUserGroup.setModule(UserSensorGroupView.class);

            //
            // ..popis grupa senzora koje korisnik definira...
            SessionData sd = SessionData.get();
            if (sd != null && !sd.isAdmin()) {
                List<DBSUserGroup> lUG = DBSUserGroup.findByUser(sd.getUserId());
                
                for(DBSUserGroup group : lUG) {
                    Menu smUserGroup = mUserGroup.newSubmenuItem();
                    smUserGroup.setCaption(group.name);
                    smUserGroup.setTitle(group.description);
                    smUserGroup.setModule(UserGroupSensorsView.class, "loadModule", group.id);
                }
            }
            
            if (mUserGroup.getMenuElements().size() != 0)
                mUserGroup.setExpand(true); 
            
            
            
            //
            // Sva očitanja
            //
            
            Menu mainAllData = mainContainer.newSubmenuItem();
            mainAllData.setCaption("Sva očitanja");
            mainAllData.setModule(AllSensorDataView.class);
            
            
            
            
            //
            // Profil
            //
            
            Menu mainMenu = mainContainer.newSubmenuItem();
            mainMenu.setCaption("Profil");

            // SUBMENUs
            Menu subMenu = mainMenu.newSubmenuItem();
            subMenu.setCaption("Moji podaci");
            subMenu.setModule(UserData.class);

            subMenu = mainMenu.newSubmenuItem();
            subMenu.setCaption("Grupe senzora");
            subMenu.setModule(UserSensorGroupManage.class);

            subMenu = mainMenu.newSubmenuItem();
            subMenu.setCaption("SMS info");
            subMenu.setModule(UserSMSInfo.class);
        }

        return mainContainer;
    }

}
