package util.hlib.html;

import util.hlib.HDataContainer;
import util.hlib.element.HTag;
import util.hlib.element.HTagUtil;

public class HDiv extends HTag {

    public static final HtmlTags HTML_TAG = HtmlTags.div;

    public HDiv() throws Exception {
        super(HTML_TAG);

    }

    public HDiv(String id) throws Exception {
        this();
        setSelector("#" + id);
    }

    public HDiv(HDataContainer hdc, String id) throws Exception {
        this();
        setSelector("#" + id);
        hdc.add(this);
    }
    
    public static HDiv load(HDataContainer hdc, String id) throws Exception {
        HDiv hi = HTagUtil.getElementById(hdc, id, HDiv.class);
        return hi;
    }

}
