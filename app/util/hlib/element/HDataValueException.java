package util.hlib.element;

public class HDataValueException  extends Exception {

    public HDataValueException(String message) {
        super(message);
    }

    public HDataValueException(String tagName, String message) {
        super("Exception on attribute name:" + tagName + "\n" + message);
    }
    
}
