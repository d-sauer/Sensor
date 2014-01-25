package util.hlib.element;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.html.HHtml;
import util.hlib.html.HUtil;
import util.hlib.html.HtmlTags;

public class HTag extends HHtml {

    private HDataValue<String> tagName;
    private HDataValue<String> id;
    private HDataValue<Css> CssClass;
    private HDataValue<String> style;
    private HDataValue<String> title;

    public HTag(HDataContainer hc) throws Exception {
        hc.add(this);
    }
    
    public HTag() throws Exception {
        this.setHDataName("html");
    }

    public HTag(String tagName) throws Exception {
        this();
        this.tagName = new HDataValue<String>(this, "tag", tagName);
    }

    public HTag(String tagName, String id) throws Exception {
        this();
        this.tagName = new HDataValue<String>(this, "tag", tagName);
        this.setId(id);
    }

    public HTag(HtmlTags tagName) throws Exception {
        this(tagName.toString());
    }

    /**
     * Get HTML tag
     */
    public String getTag() {
        return this.tagName.getValue();
    }

    /**
     * Set HTML tag
     * @param tagName
     * @throws Exception
     */
    public void setTag(String tagName) throws Exception {
        if (this.tagName == null)
            this.tagName = new HDataValue<String>(this, "tag", tagName);
        else
            this.tagName.setValue(tagName);
    }

    /**
     * Get ID value
     * 
     * @return
     */
    public String getId() {
        if (this.id == null)
            return null;
        else
            return this.id.getValue();
    }

    /**
     * Set ID value
     * 
     * @param id
     * @throws Exception
     */
    public void setId(String id) throws Exception {
        if (this.id == null)
            this.id = new HDataValue<String>(this, "id", id);
        else
            this.id.setValue(id);
    }

    /**
     * Get CSS class
     * @return
     */
    public Css getCssClass() {
        if (this.CssClass==null)
            return null;
        else
            return this.CssClass.getValue();
    }

    /**
     * Set CSS class
     * @param cssClass
     * @throws Exception
     */
    public Css setCssClass() throws Exception {
        return initCssClass();
    }

    public Css setCssClass(String cssClass) throws Exception {
        return initCssClass().add(cssClass);
    }
    
    private Css initCssClass() throws Exception {
        Css css = null;
        
        if (this.CssClass == null) {
            css = new Css();
            this.CssClass = new HDataValue<Css>(this, "clazz", css);
        } else
            css = this.getCssClass();
        
        return css;
    }

    /**
     * Get element CSS style
     * @return
     */
    public HDataValue getStyle() {
        return this.style;
    }

    /**
     * Set element CSS style
     * @param style
     * @throws Exception
     */
    public void setStyle(String style) throws Exception {
        if (this.style == null)
            this.style = new HDataValue<String>(this, "style", style);
        else
            this.style.setValue(style);
    }

    /**
     * Get element titile
     * @return
     */
    public HDataValue getTitle() {
        return this.title;
    }

    /**
     * Set element title
     * @param title
     * @throws Exception
     */
    public void setTitle(String title) throws Exception {
        if (this.title == null)
            this.title = new HDataValue<String>(this, "title", title);
        else
            this.title.setValue(title);
    }


    @Override
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        if (tagName.equals("tag")) {
            String value = tagValue.getTextValue();
            setTag(value);
            return true;
        }
        else if (tagName.equals("id")) {
            String value = tagValue.getTextValue();
            setId(value);
            setSelector("#" + value);
            return true;
        }
        else if (tagName.equals("clazz")) {
            String value = tagValue.getTextValue();
            setCssClass(value);
            return true;
        }
        else if (tagName.equals("style")) {
            String value = tagValue.getTextValue();
            setStyle(value);
            return true;
        }
        else if (tagName.equals("title")) {
            String value = tagValue.getTextValue();
            setTitle(value);
            return true;
        } else {
            return super.loadFromJson(tagName, tagValue);
        }
    }

    
    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }
    
    /**
     * If data value not exists or has't value then return true, otherwise return false
     * 
     * @param dataValueName
     *            - eg.: id, value, type
     * @return
     */
    public boolean isBlank(String dataValueName) {
        HDataValue hdv = this.getValue(dataValueName);
        if (hdv != null) {
            String s = hdv.getValue().toString();
            if (s.length() != 0)
                return false;

        }
        return true;
    }
}
