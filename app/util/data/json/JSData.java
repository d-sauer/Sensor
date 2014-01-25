package util.data.json;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Http.Request;

public class JSData {

    private JsonNode json = null;

    public JSData() {

    }

    public JSData(JsonNode json) throws Exception {
        this.json = json;
        loadData(json);
    }

    public void loadData(Request request) throws Exception {
        JsonNode json = request.body().asJson();
        loadData(json);
    }

    /**
     * Data structure
     * 
     * <pre>
     *      {
     *          "name": "polje",
     *          "value": "vrijednost"
     *      },
     *      {
     *          "name": "polje",
     *          "value": "vrijednost"
     *      }
     * </pre>
     * 
     * @param inObj
     * @param json
     * @throws Exception
     */
    public void loadData(JsonNode json) throws Exception {
        if (this.json == null)
            this.json = json;

        Iterator<JsonNode> itJs = json.getElements();
        while (itJs.hasNext()) {
            JsonNode jsn = itJs.next();

            JsonNode jsName = jsn.get("name");
            JsonNode jsValue = jsn.get("value");

            if (jsName != null) {
                // Find filed in object
                Field[] fields = this.getClass().getFields();
                for (Field f : fields) {
                    if (f.getName().equals(jsName.asText())) {
                        setField(f, jsValue, jsn);
                        break;
                    }
                }

            }

        }
    }
    
    public JsonNode getNode(String name, String value) {
        return JSUtil.getNode(json, name, value);
    }
    
    public JsonNode getFieldNode(Object field) {
        //  TODO
        return null;
    }
    
    public String getFieldName(Object field) {
        Field [] fields = this.getClass().getDeclaredFields();
        for (Field cField: fields) {
            Object cObj = null;
            try {
                cObj = (Object)cField.get(this);
            } catch(Exception e) {
                
            }
            if (cObj!=null && cObj==field) {
                return cField.getName();
            }
        }
        return null;
    }
    
    private void setField(Field field, JsonNode jsonValue, JsonNode fieldJsonData) throws Exception {
        if (jsonValue != null && !jsonValue.getTextValue().equals("null")) {
            if (field.getType() == String.class)
                field.set(this, jsonValue.asText());
        }
    }

    public JsonNode getJSON() {
        return json;
    }

    public void addData(Object field, String dataName, String dataValue) throws Exception {
        Field[] fields = this.getClass().getFields();
        for (Field _field : fields) {
            Object o = (Object) _field.get(this);
            if (o == field) {
                Logger.debug("field name: " + _field.getName());
            }
        }
    }

}
