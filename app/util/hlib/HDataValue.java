package util.hlib;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import util.hlib.element.HDataValueException;

public class HDataValue<E> {

    private String   attributeName;
    private Class<E> attributeValueClass;
    private E        attributeValue;
    // private boolean isMultidata = false;
    private boolean  isCustom = false;

    public HDataValue(HData hdata, String name, Class<E> valueClass) throws Exception {
        this.attributeName = name;
        this.attributeValueClass = valueClass;
        hdata.addValue(this);
    }

    public HDataValue(HData hdata, String name, E value) throws Exception {
        this.attributeName = name;
        this.attributeValue = value;
        this.attributeValueClass = (Class<E>) value.getClass();

        hdata.addValue(this);
    }

    public HDataValue(HData hdata, String name, E value, boolean isCustom) throws Exception {
        this.attributeName = name;
        this.attributeValue = value;
        this.isCustom = isCustom;
        if (value!=null)
            this.attributeValueClass = (Class<E>) value.getClass();

        hdata.addValue(this);
    }

    /**
     * Get value data name 
     * @return
     */
    public String getName() {
        return this.attributeName;
    }

    /**
     * Set value representation name
     * @param name
     */
    public void setName(String name) {
        this.attributeName = name;
    }

    /**
     * Get data value object
     * @return
     */
    public E getValue() {
        return this.attributeValue;
    }

    /**
     * Set data value object
     * @param value
     */
    public void setValue(E value) {
        this.attributeValue = value;
    }

    public boolean isSet() {
        if (attributeValue != null) {
            if (attributeValue instanceof HDataValueType) {
                return ((HDataValueType) getValue()).isSet();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean addToJson(ObjectNode jsNode) {
        if (isSet()) {
            if (attributeValue instanceof HDataValueType) {
                jsNode.put(getName(), ((HDataValueType) getValue()).getValue());
            } 
            else if (attributeValue instanceof Boolean ){
                jsNode.put(getName(), (Boolean)getValue());
            }
            else if (attributeValue instanceof Integer ){
                jsNode.put(getName(), (Integer)getValue());
            }
            else if (attributeValue instanceof Long){
                jsNode.put(getName(), (Long)getValue());
            }
            else if (attributeValue instanceof Double){
                jsNode.put(getName(), (Double)getValue());
            }
            else if (attributeValue instanceof Float){
                jsNode.put(getName(), (Float)getValue());
            }
            else if (attributeValue instanceof String){
                jsNode.put(getName(), (String)getValue());
            }
            else {
                jsNode.put(getName(), getValue().toString());
            }
            return true;
        } else {
            return false;
        }
    }

    public void loadAttributeFromJson(HData htmlTag, JsonNode json) throws Exception {
        JsonNode name = json.get(this.attributeName);

        if (name != null) {
            if (name.isNull()) {
                attributeValue = null;
            } else {
                if (this.attributeValueClass.isAssignableFrom(String.class)) {
                    attributeValue = attributeValueClass.newInstance();
                    attributeValue = (E) name.asText();
                }
                else {
                    throw new HDataValueException(this.attributeName);
                }
            }
        }
    }

    @Override
    public String toString() {
        return attributeName + ":" + attributeValue.toString();
    }
}
