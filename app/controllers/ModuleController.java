package controllers;

import org.codehaus.jackson.JsonNode;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.Http.CallController;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.html.HHtml;
import util.logger.log;

public class ModuleController extends Controller {

    public static final String MODULE_CONTENT     = "#sensor_content";
    public static final String LOAD_MODULE_METHOD = "loadModule";

    public static Result module(String method) throws Exception {
        try {
            return CallController.call(method);
        } catch (Exception e) {
            String error = log.exception(e);

            if (AppInfo.isModeDebug()) {
                error = error.replace("\n", "<br/>");
                Html h = new Html(error);

                return internalServerError(views.html.error_show.render(h));
            } else
                return internalServerError(error);
        }
    }

    public static JsonNode result(Html html, HData... hdata) throws Exception {
        HHtml jsHtml = new HHtml(ModuleController.MODULE_CONTENT);
        jsHtml.setInnerHTML(html.body());

        HDataContainer hdc = new HDataContainer(jsHtml);
        hdc.add(hdata);

        return hdc.getJson();
    }

    public static JsonNode result(Class module, Html html, HData... hdata) throws Exception {
        HHtml jsHtml = new HHtml(ModuleController.MODULE_CONTENT);
        jsHtml.setInnerHTML(html.body());

        jsHtml.addValue("module", module.getName());

        HDataContainer hdc = new HDataContainer(jsHtml);
        hdc.add(hdata);

        return hdc.getJson();
    }

    public static JsonNode result(Class module, String htmlContainer, Html html, HData... hdata) throws Exception {
        HHtml jsHtml = new HHtml(htmlContainer);
        jsHtml.setInnerHTML(html.body());

        jsHtml.addValue("module", module.getName());

        HDataContainer hdc = new HDataContainer(jsHtml);
        hdc.add(hdata);

        return hdc.getJson();
    }

    public static JsonNode result(Class module, String htmlContainer, Html html, HDataContainer hdc) throws Exception {
        HHtml jsHtml = new HHtml(htmlContainer);
        jsHtml.setInnerHTML(html.body());
        
        jsHtml.addValue("module", module.getName());
        
        hdc.add(jsHtml);
        
        return hdc.getJson();
    }
}
