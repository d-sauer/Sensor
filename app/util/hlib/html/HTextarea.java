package util.hlib.html;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;

public class HTextarea extends HTag {

    public static final HtmlTags HTML_TAG = HtmlTags.textarea;

    public HTextarea() throws Exception {
        super(HTML_TAG);
    }

    public HTextarea(String id) throws Exception {
        this();
        setSelector("#" + id);
    }

    public static HTextarea load(HDataContainer hdc, String id) throws Exception {
        HTextarea hi = HTagUtil.getElementById(hdc, id, HTextarea.class);
        return hi;
    }

    //
    // Input type attributes
    //
    private HDataValue<String>       value;

    public HDataValue<String> getValue() throws Exception {
        return this.value;
    }

    public String getAttrValue() {
        return this.value == null ? null : this.value.getValue();
    }

    public void setAttrValue(String value) throws Exception {
        if (this.value == null)
            this.value = new HDataValue<String>(this, "value", value);
        else
            this.value.setValue(value);
    }


    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }

    @Override
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        if (tagName.equals("value")) {
            String value = tagValue.getTextValue();
            setAttrValue(value);
            return true;
        }
        else {
            return super.loadFromJson(tagName, tagValue);
        }
    }

}
