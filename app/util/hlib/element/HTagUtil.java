package util.hlib.element;

import java.util.ArrayList;
import java.util.List;

import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;

public class HTagUtil {

    public static HData getElementById(HDataContainer hdc, String id) {
        return getElementById(hdc, id, HTag.class);
    }

    public static <E extends HTag> E getElementById(HDataContainer hdc, String id, Class<E> type) {
        if (hdc == null)
            return null;

        HTag _ht = null;

        for (HData hd : hdc.get()) {
            if (hd instanceof HTag) {
                HTag ht = (HTag) hd;
                if (ht.getId() != null && ht.getId().equals(id)) {
                    if (type != null) {
                        if (ht.getClass().isAssignableFrom(type)) {
                            _ht = ht;
                            break;
                        }
                    } else {
                        _ht = ht;
                        break;
                    }
                }
            }
        }

        return (E) _ht;
    }

    public static List<? extends HTag> getElementByAttribute(HDataContainer hdc, String attributeName) {
        return getElementByAttribute(hdc, attributeName, null, HTag.class);
    }

    public static <E extends HTag> List<? extends HTag> getElementByAttribute(HDataContainer hdc, String attributeName, Class<E> type) {
        return getElementByAttribute(hdc, attributeName, null, type);
    }

    public static <E extends HTag> List<E> getElementByAttribute(HDataContainer hdc, String attributeName, String attributeValue, Class<E> type) {
        List<E> elements = new ArrayList<E>();

        List<HData> tmpHDC = hdc.get();
        for (HData hd : tmpHDC) {
            if (hd.getClass().isAssignableFrom(type)) {
                HDataValue<String> hdv = hd.getValue(attributeName);

                if (hdv != null) {
                    if (attributeValue != null) {
                        String value = hdv.getValue();
                        if (value != null && value.matches(attributeValue))
                            elements.add((E) hd);
                    } else {
                        elements.add((E) hd);
                    }
                }
            }
        }

        return elements;
    }

}
