package util.hlib.html;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.element.Selector;

public class HHtml extends HData {

    private HDataValue<Selector> selector;
    private HDataValue<String>   innerHTML;

    public HHtml() {
        this.setHDataName("html");
    }

    public HHtml(String selector) throws Exception {
        this();
        setSelector(selector);
    }

    public HHtml(HDataContainer hdc) throws Exception {
        this();
    }

    public HHtml(HDataContainer hdc, String selector) throws Exception {
        this();
        setSelector(selector);
        hdc.add(this);
    }

    /**
     * Set InnerHTML
     * 
     * @param innerHTML
     * @throws Exception
     */
    public void setInnerHTML(String innerHTML) throws Exception {
        if (this.innerHTML == null)
            this.innerHTML = new HDataValue<String>(this, "innerHTML", innerHTML);
        else
            this.innerHTML.setValue(innerHTML);
    }

    /**
     * Get InnerHTML
     * 
     * @return
     */
    public String getInnerHTML() {
        return this.innerHTML.getValue();
    }

    public Selector getSelector() {
        if (this.selector == null)
            return null;
        else
            return this.selector.getValue();
    }

    public Selector setSelector() throws Exception {
        return initSelector();
    }

    public Selector setSelector(String selectorString) throws Exception {
        return initSelector().add(selectorString);
    }

    private Selector initSelector() throws Exception {
        Selector s = null;
        if (this.selector == null) {
            s = new Selector();
            this.selector = new HDataValue<Selector>(this, "selector", s);
        }
        else {
            this.selector.getValue();
        }

        return s;
    }

    @Override
    public boolean loadFromJson(String tagName, JsonNode tagValue) throws Exception {
        if (tagName.equals("selector")) {
            String value = tagValue.getTextValue();
            setSelector(value);
            return true;
        } 
        else {
            return super.loadFromJson(tagName, tagValue);
        }
    }

    @Override
    public ObjectNode getJson(ObjectNode jsNode) throws Exception {
        return super.getJson(jsNode);
    }
}
