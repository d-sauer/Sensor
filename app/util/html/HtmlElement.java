package util.html;

import java.lang.reflect.Field;
import java.util.Collection;

public class HtmlElement {

    //
    // VARS..
    //

    private String id;
    private String name;
    private String type;
    private String value;
    private String data;

    //
    // CONSTRUCTORS
    //

    public HtmlElement() {

    }

    public HtmlElement(String id) {
        this.id = id;
    }

    //
    // GETERS / SETTERS
    //
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //
    // LOGIC..
    //

    public String getJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            Object o = null;
            ;
            try {
                o = (Object) field.get(this);
            } catch (Exception e) {
            }

            if (o != null) {
                if (i > 0 && i < fields.length)
                    sb.append(", ");
                sb.append("\"" + field.getName() + "\":\"" + o + "\"");
            }
        }

        sb.append("} ");
        return sb.toString();
    }

    public static String getListJSON(Collection<HtmlElement> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        int c = 0;
        for (HtmlElement he : list) {
            if (c > 0 && c < list.size())
                sb.append(", ");

            sb.append(he.getJSON());
            c++;
        }

        sb.append(" ]");

        return sb.toString();
    }

    @Override
    public String toString() {
        return getJSON();
    }

}
