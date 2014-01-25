package util.hlib.html;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;

public class HSelect extends HTag {

    public static final HtmlTags HTML_TAG = HtmlTags.select;

    public HSelect() throws Exception {
        super(HTML_TAG);
    }

    public HSelect(String id) throws Exception {
        this();
        setSelector("#" + id);
    }

    public static HSelect load(HDataContainer hdc, String id) throws Exception {
        HSelect hi = HTagUtil.getElementById(hdc, id, HSelect.class);
        return hi;
    }


    //
    // Input type attributes
    //
    private HDataValue<String>       value;
    private HDataValue<String>       text;
    private HDataValue<Integer>      index;


    public HDataValue<String> getValue() throws Exception {
        return this.value;
    }

    public String getSelectedValue() {
        return this.value == null ? null : this.value.getValue();
    }

    public void setSelectedValue(String value) throws Exception {
        if (this.value == null)
            this.value = new HDataValue<String>(this, "value", value);
        else
            this.value.setValue(value);
    }

    private void setSelectedText(String text) throws Exception {
        if (this.text == null)
            this.text = new HDataValue<String>(this, "text", text);
        else
            this.text.setValue(text);
    }

    private void setSelectedIndex(Integer index) throws Exception {
        if (this.index == null)
            this.index = new HDataValue<Integer>(this, "index", index);
        else
            this.index.setValue(index);
    }

    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }

    @Override
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        if (tagName.equals("value")) {
            String value = tagValue.asText();
            setSelectedValue(value);
            return true;
        }
        else if (tagName.equals("text")) {
            String value = tagValue.asText();
            setSelectedText(value);
            return true;
        }
        else if (tagName.equals("index")) {
            String value = tagValue.asText();
            Integer iValue = Integer.parseInt(value);
            setSelectedIndex(iValue);
            return true;
        }
        else {
            return super.loadFromJson(tagName, tagValue);
        }
    }

}
