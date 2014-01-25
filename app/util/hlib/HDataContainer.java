package util.hlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ContainerNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.html.HDiv;
import util.hlib.html.HHtml;
import util.hlib.html.HInput;
import util.hlib.html.HLabel;
import util.hlib.html.HSelect;
import util.hlib.html.HTextarea;

public class HDataContainer {

    private List<HData> hdNodes = new ArrayList<HData>();

    public HDataContainer() {
    }

    /**
     * Init HDataContainer from JSON
     * 
     * @param json
     * @throws Exception
     */
    public HDataContainer(JsonNode json) throws Exception {
        loadFromJson(json);
    }

    /**
     * Add HData into container
     * 
     * @param ht
     */
    public HDataContainer(HData... hdata) {
        add(hdata);
    }

    /**
     * Add HData elements
     * 
     * @param hdata
     */
    public void add(HData... hdata) {
        for (HData hd : hdata)
            if (hd!=null)
                hdNodes.add(hd);
    }
    
    public void remove(HData...hdata) {
        for (HData hd : hdata)
            if (hd!=null)
                hdNodes.remove(hd);
    }

    /**
     * Get HDataContainer element at specific index
     * 
     * @param index
     * @return
     */
    public HData get(int index) {
        return hdNodes.get(index);
    }

    /**
     * Get HDataContainer elements
     * 
     * @return
     */
    public List<HData> get() {
        return this.hdNodes;
    }

    /**
     * Get Json String representation of HDataContainer
     * @return
     * @throws Exception
     */
    public String getJsonString() throws Exception {
        return getJson().toString();
    }

    /**
     * Get JSon object of HDataContainer
     * @return
     * @throws Exception
     */
    public ObjectNode getJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsRoot = mapper.createObjectNode();

        return getJson(jsRoot);
    }

    /**
     * Get HDataContainer Json into parent Json object
     * @param jsParent
     * @return
     * @throws Exception
     */
    public ObjectNode getJson(ObjectNode jsParent) throws Exception {
        HashMap<String, ContainerNode> hRootData = new HashMap<String, ContainerNode>();

        for (HData hdata : hdNodes) {
            String dataName = hdata.getHDataName();
            ContainerNode jsRootData = hRootData.get(dataName);

            if (jsRootData == null && hdata instanceof HHtml) {    // HTML TAG IS ALWAYS IN ARRAY
                jsRootData = jsParent.putArray(dataName);
                hRootData.put(dataName, jsRootData);
            }
            else {                                                  // CUSTOM DATA
                if (jsRootData == null) {
                    JsonNode jn = jsParent.get(dataName);

                    if (jn == null) {
                        jsRootData = jsParent.putObject(dataName);
                    }
                    else {
                        // replace objectNode with ArrayNode
                        jsRootData = jsParent.putArray(dataName);
                        ((ArrayNode) jsRootData).add(jn);
                        hRootData.put(dataName, jsRootData);
                    }
                }
            }

            if (hdata != null && jsRootData instanceof ArrayNode) {
                ObjectNode jsNode = jsParent.objectNode();

                hdata.getJson(jsNode);

                ((ArrayNode) jsRootData).add(jsNode);
            }
            else if (hdata != null && jsRootData instanceof ObjectNode) {
                hdata.getJson((ObjectNode) jsRootData);
            }
        }

        return jsParent;
    }

    /**
     * Load HDataContainer from Json
     * 
     * @param json
     * @throws Exception
     */
    public void loadFromJson(JsonNode json) throws Exception {
        //
        // Load HTML data to container from Json
        //
        JsonNode jsHtml = json.get("html");
        if (jsHtml != null) {

            Iterator<JsonNode> ijn = jsHtml.getElements();
            while (ijn.hasNext()) {
                JsonNode jsEl = ijn.next();

                JsonNode tag = jsEl.get("tag");
                HData ht = null;
                if (tag != null) {
                    String _tag = tag.getTextValue();
                    if (_tag != null) {
                        if (_tag.equals("div")) {
                            ht = new HDiv();
                            ht.loadFromJson(jsEl);
                        }
                        else if (_tag.equals("input")) {
                            ht = new HInput();
                            ht.loadFromJson(jsEl);
                        }
                        else if (_tag.equals("select")) {
                            ht = new HSelect();
                            ht.loadFromJson(jsEl);
                        }
                        else if (_tag.equals("textarea")) {
                            ht = new HTextarea();
                            ht.loadFromJson(jsEl);
                        }
                        else if (_tag.equals("label")) {
                            ht = new HLabel();
                            ht.loadFromJson(jsEl);
                        }
                    }
                } else {
                    ht = new HData();
                    ht.loadFromJson(jsEl);
                }
                add(ht);
            }
        }

        //
        // Load other data to container from Json, except html
        //
//        Iterator<Entry<String, JsonNode>> i = json.getFields();
//        Iterator<JsonNode> ijn = json.getElements();
//        while (ijn.hasNext()) {
//            JsonNode jsEl = ijn.next();
//            HData ht = new HData();
//
//            
//            
//            ht.loadFromJson(jsEl);
//            
//            add(ht);
//        }
    }
}
