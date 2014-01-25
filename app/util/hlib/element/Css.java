package util.hlib.element;

import java.util.LinkedHashSet;
import java.util.Set;

import util.hlib.HDataValueType;

public class Css implements HDataValueType<Css> {

    public Set<String> cssClass = new LinkedHashSet<String>();

    public Css() {

    }

    public Css(String cssClass) {
        add(cssClass);
    }

    public Css add(String clazz) {
        String[] cs = clazz.split(" ");
        for (String c : cs) {
            cssClass.add(c);
        }

        return this;
    }
    
    public boolean remove(String clazz) {
        return cssClass.remove(clazz);
    }

    public Css clear() {
        cssClass.clear();
        return this;
    }

    public Css clearAndSet(String cssClass) {
        this.cssClass.clear();
        add(cssClass);

        return this;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public Css get() {
        return this;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String css : cssClass) {
            if (i != 0)
                sb.append(" ");
            sb.append(css);
            i++;
        }

        return sb.toString();
    }

    @Override
    public void set(Css value) {
        clear();
        this.cssClass.addAll(value.cssClass);
    }

    @Override
    public void setValue(String value) {
        clear();
        add(value);
    }

    @Override
    /**
     * Can bi set, but empty, so JavaScript will remove all css class from selected element
     */
    public boolean isSet() {
        return true;
    }

}
