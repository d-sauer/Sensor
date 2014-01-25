package util.hlib.html;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;

public class HLabel extends HTag {

    public static final HtmlTags HTML_TAG = HtmlTags.label;

    public HLabel() throws Exception {
        super(HTML_TAG);
    }

    public HLabel(String id) throws Exception {
        this();
        setSelector("#" + id);
    }

    public static HLabel load(HDataContainer hdc, String id) throws Exception {
        HLabel hi = HTagUtil.getElementById(hdc, id, HLabel.class);
        return hi;
    }

    //
    // Input type attributes
    //
    private HDataValue<String>       forAttribute;

    public String getAttrFor() {
        return this.forAttribute == null ? null : this.forAttribute.getValue();
    }

    public void setAttrFor(String value) throws Exception {
        if (this.forAttribute == null)
            this.forAttribute = new HDataValue<String>(this, "value", value);
        else
            this.forAttribute.setValue(value);
    }

    public void set(String html) throws Exception {
        setInnerHTML(html);
    }
    
    public String get() {
        return getInnerHTML();
    }

    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }

//    @Override
//    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
//        if (tagName.equals("label")) {
//            String value = tagValue.getTextValue();
//            setAttrFor(value);
//            return true;
//        }
//        else {
//            return super.loadFromJson(tagName, tagValue);
//        }
//    }

}
