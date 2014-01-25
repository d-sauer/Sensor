package util.hlib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.element.HDataValueException;

public class HData {

    /**
     * Key: Attribute name
     * Value: HtmlTagAttribute
     */
    private HashMap<String, HDataValue> registerValues = new HashMap<String, HDataValue>();
    private HDataContainer              nodes;
    private String                      dataName       = "data";
    private HashMap<String, ObjectNode> customJson = new HashMap<String, ObjectNode>();

    public HData() {
    }

    public HData(HDataContainer hdc) {
        hdc.add(this);
    }

    public HData(HDataContainer hdc, String dataName) {
        this(hdc);
        this.dataName = dataName;
    }

    public HData(String dataName) {
        this.dataName = dataName;
    }

    public String getHDataName() {
        return this.dataName;
    }

    public void setHDataName(String dataName) {
        this.dataName = dataName;
    }

    public void addCustomJson(String nodeName, ObjectNode json) {
        customJson.put(nodeName, json);
    }
    
    /**
     * Get child nodes of this element
     * 
     * @return
     */
    public List<HData> getNodes() {
        return this.nodes.get();
    }

    /**
     * Add child HData node to this element
     * 
     * @param hdata
     */
    public void addNode(HData... hdata) {
        if (this.nodes == null)
            this.nodes = new HDataContainer();

        for (HData hd : hdata)
            this.nodes.add(hd);
    }

    /**
     * Add attribute with value
     * 
     * @param tagName
     * @param tagValue
     * @throws Exception
     */
    public void addValue(String tagName, String tagValue) throws Exception {
        HDataValue<String> attr = new HDataValue<String>(this, tagName, tagValue);
    }

    public void addValue(String tagName, boolean tagValue) throws Exception {
        HDataValue<Boolean> attr = new HDataValue<Boolean>(this, tagName, tagValue);
    }

    public void addValue(HDataValue... dataValue) throws HDataValueException {
        for (HDataValue hta : dataValue)
            registerValues.put(hta.getName(), hta);
    }

    /**
     * Get attribute by name
     * 
     * @param dataValueName
     * @return
     */
    public HDataValue getValue(String dataValueName) {
        HDataValue hta = registerValues.get(dataValueName);

        return hta;
    }

    public String get(String dataValueName) {
        HDataValue<String> hdv = getValue(dataValueName);
        if (hdv != null) {
            String value = hdv.getValue();
            if (value != null)
                return value;
            else
                return null;
        } else {
            return null;
        }
    }

    public HashMap<String, HDataValue> getValues() {
        return this.registerValues;
    }

    /**
     * Get JSON String
     * 
     * @return
     * @throws Exception
     */
    public String getJsonString() throws Exception {
        return getJson().toString();
    }

    /**
     * Get Json representation of HData object
     * 
     * @return
     * @throws Exception
     */
    public ObjectNode getJson() throws Exception {
        ObjectNode jsonData = new ObjectMapper().createObjectNode();

        return getJson(jsonData);
    }

    /**
     * Load HData from Json
     * 
     * @param json
     * @throws Exception
     */
    public void loadFromJson(JsonNode json) throws Exception {
        Iterator<Entry<String, JsonNode>> itTags = json.getFields();
        while (itTags.hasNext()) {
            Entry<String, JsonNode> jsTag = itTags.next();
            String tagName = jsTag.getKey();
            JsonNode jsValue = jsTag.getValue();

            boolean isLoaded = loadFromJson(tagName, jsValue);

            // custom attribute
            if (isLoaded == false) {
                HDataValue<String> hta = new HDataValue<String>(this, tagName, jsValue.asText(), true);
            }
        }
    }

    /**
     * Override function for implement custom data loading.
     * Used it in inherited object, like HInput, HDiv, etc.
     * 
     * @param tagName
     * @param tagValue
     * @return
     * @throws Exception
     */
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        return false;
    }

    /**
     * Get Json of HData element
     * 
     * @return
     * @throws Exception
     */
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        // ADD ATTRIBUTES
        getJsonValue(jsNode);

        // ADD CHILD HDATA
        if (nodes != null)
            nodes.getJson(jsNode);

        for(Entry<String, ObjectNode> js : customJson.entrySet())
            jsNode.put(js.getKey(), js.getValue());
        
        return jsNode;
    }

    /**
     * Get Json of HData attributes
     * 
     * @param jsParent
     * @throws Exception
     */
    public void getJsonValue(ObjectNode jsParent) throws Exception {
        for (Entry<String, HDataValue> ehta : registerValues.entrySet()) {
            HDataValue hta = ehta.getValue();

            boolean isAdded = hta.addToJson(jsParent);
        }
    }

    @Override
    public String toString() {
        try {
            return getJsonString();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

}
