package util.hlib;

public interface HDataValueType <E> {

    /**
     * Get value object
     * @return
     */
    public E get();
    
    /**
     * Get String representation of value object
     * @return
     */
    public String getValue();
    
    /**
     * Set value
     * @param value
     */
    public void set(E value);

    /**
     * Set value with string
     * @param value
     */
    public void setValue(String value);
    
    /**
     * Is value set
     * @return true - if value is not null or is not empty (e.g. empty string)
     */
    public boolean isSet();
    
}
