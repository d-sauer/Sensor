package util.hlib.html;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;

public class HInput extends HTag {

    public static final HtmlTags HTML_TAG = HtmlTags.input;

    public HInput() throws Exception {
        super(HTML_TAG);
    }

    public HInput(String id) throws Exception {
        this();
        setSelector("#" + id);
    }

    public HInput(String id, InputTagType type) throws Exception {
        this(id);
        setType(type);
    }

    
    
    public static HInput load(HDataContainer hdc, String id) throws Exception {
        HInput hi = HTagUtil.getElementById(hdc, id, HInput.class);
        return hi;
    }

    /**
     * Input tag types
     */
    public static enum InputTagType {
        text, hidden, password, checkbox, radio;
    }

    //
    // Input type attributes
    //
    private HDataValue<InputTagType> type;
    private HDataValue<String>       value;
    private HDataValue<Boolean>      checked;

    public HDataValue<InputTagType> getType() throws Exception {
        return this.type;
    }

    public void setType(InputTagType type) throws Exception {
        if (this.type == null)
            this.type = new HDataValue<InputTagType>(this, "type", type);
        else
            this.type.setValue(type);
    }

    public HDataValue<String> getValue() throws Exception {
        return this.value;
    }

    public HDataValue<Boolean> getChecked() throws Exception {
        return this.checked;
    }

    public boolean isChecked() throws Exception {
        if (this.checked != null) {
            Boolean b = this.checked.getValue();
            if (b != null && b == true)
                return true;
            else
                return false;
        } else 
            return false;
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

    public void setAttrChecked(boolean checked) throws Exception {
        if (this.checked == null)
            this.checked = new HDataValue<Boolean>(this, "checked", checked);
        else
            this.checked.setValue(checked);
    }

    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }

    @Override
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        if (tagName.equals("type")) {
            String strType = tagValue.getTextValue().toLowerCase();
            for (InputTagType itt : InputTagType.values()) {
                if (itt.toString().equals(strType)) {
                    setType(itt);
                    return true;
                }
            }
        }
        else if (tagName.equals("value")) {
            String value = tagValue.getTextValue();
            setAttrValue(value);
            return true;
        }
        else if (tagName.equals("checked")) {
            String value = tagValue.asText();
            Boolean checked = Boolean.valueOf(value);
            setAttrChecked(checked);
            
            return true;
        }
        else {
            return super.loadFromJson(tagName, tagValue);
        }

        return false;
    }

}
