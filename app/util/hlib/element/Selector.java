package util.hlib.element;

import java.util.LinkedList;
import java.util.List;

import util.hlib.HDataValueType;

/**
 * Create jquery selector string to select specific element(s) in HTML
 * 
 * @author davor
 * 
 */
public class Selector implements HDataValueType<Selector> {

    private List<String> selector = new LinkedList<String>();

    public Selector() {

    }

    public Selector(String selector) {
        this();
        add(selector);
    }

    public Selector add(String selector) {
        String[] s = selector.split(" ");
        for (String sel : s)
            this.selector.add(sel);
        return this;
    }

    public Selector setId(String id) {
        return add("#" + id);
    }

    public Selector setClass(String clazz) {
        return add("." + clazz);
    }

    public Selector clear() {
        selector.clear();
        return this;
    }

    public Selector clearAndSet(String selector) {
        this.selector.clear();
        add(selector);
        return this;
    }

    public boolean remove(String selector) {
        for (int i = 0; i < this.selector.size(); i++) {
            String s = this.selector.get(i);
            if (s.equals(selector)) {
                this.selector.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public Selector get() {
        return this;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selector.size(); i++) {
            if (i != 0)
                sb.append(" ");
            sb.append(selector.get(i));
        }

        return sb.toString();
    }

    @Override
    public void set(Selector value) {
        clear();
        this.selector.addAll(value.selector);
    }

    @Override
    public void setValue(String value) {
        clear();
        add(value);
    }

    @Override
    public boolean isSet() {
        if (this.selector.size() != 0)
            return true;
        else
            return false;
    }

}
