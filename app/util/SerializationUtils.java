package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SerializationUtils {

    public static String writeObjectBase64(Serializable object) throws Exception {
        BASE64Encoder b64 = new BASE64Encoder();
        return b64.encode(writeObject(object));
    }

    public static byte[] writeObject(Serializable object) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(object);
        oos.close();

        return baos.toByteArray();
    }

    public static Object readObject(String base64) throws Exception {
        BASE64Decoder b64 = new BASE64Decoder();
        byte[] b = b64.decodeBuffer(base64);
        return readObject(b);
    }

    public static Object readObject(byte[] objectData) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
