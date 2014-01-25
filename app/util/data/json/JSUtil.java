package util.data.json;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

public class JSUtil {

    public static JsonNode getNode(JsonNode json, String name, String value) {
        JsonNode jsnode = null;
        
        Iterator<JsonNode> itJs = json.getElements();
        while (itJs.hasNext()) {
            JsonNode jsn = itJs.next();
            
            JsonNode jsName = jsn.get("name");
            JsonNode jsValue = jsn.get("value");
            
            if (jsName!=null) {
                String _name = jsName.getTextValue();
                if (!_name.equals("null") && _name.equals(name)) {
                    String _value = jsValue.getTextValue();
                    if (_value != null && _value.equals("null"))
                        _value = null;
                    if (_value == value) 
                        return jsn;
                }
            }
        }
        
        return jsnode;
    }

    public static void addJSONData(StringBuilder jsonData, String name, String value) {
        jsonData.append("\"" + name + "\":\"" + value + "\"");
    }
    
}
