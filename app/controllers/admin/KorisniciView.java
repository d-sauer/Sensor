package controllers.admin;

import java.util.List;

import module.core.models.DBCUser;

import org.codehaus.jackson.JsonNode;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.html.HUtil;
import util.logger.log;
import controllers.ModuleController;

public class KorisniciView extends Controller {

    public static Result loadModule() throws Exception {
        KorisniciView kv = new KorisniciView();
        Html h = views.html.admin.KorisniciView.render(kv.getUserList());

        return ok(ModuleController.result(KorisniciView.class, h));
    }

    public static Result activate() throws Exception {
        return activateDeactivate(true);
    }

    public static Result deactivate() throws Exception {
        return activateDeactivate(false);
    }

    private static Result activateDeactivate(boolean activate) throws Exception {
        JsonNode jsn = CallController.getRequestJson();
        HDataContainer hdc = new HDataContainer(jsn);

        List<HData> lHd = HUtil.getAllHData(hdc, "id", "ch_(\\d+)");
        for (HData hd : lHd) {
            HDataValue<String> hdv = hd.getValue("checked");
            if (hdv != null && hdv.getValue().equals("true")) {
                String id = hd.get("id").replace("ch_", "");
                log.debug("Activate id:" + id);

                DBCUser dbu = DBCUser.find.byId(Long.parseLong(id));
                dbu.active = activate ? 1 : 0;
                dbu.save();
            }
        }

        return refresh();
    }

    public static Result refresh() throws Exception {
        KorisniciView kv = new KorisniciView();
        Html h = views.html.admin.KorisniciView.render(kv.getUserList());

        return ok(ModuleController.result(KorisniciView.class, h));
    }

    public List<DBCUser> getUserList() {
        List<DBCUser> ldbu = DBCUser.find.all();

        return ldbu;
    }

}
