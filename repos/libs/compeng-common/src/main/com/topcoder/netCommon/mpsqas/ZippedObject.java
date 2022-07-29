package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.zip.*;

/**
 * ZippedObject
 *
 * A class to zip an object that is to be sent from the server
 * to client or vice verse.  If the Object cannot be zipped
 * an unzipped version is stored.
 *
 * @author mitalub
 */
public class ZippedObject implements Serializable, Cloneable, CustomSerializable {

    /**
     * The constructor stores a zipped version of the Object, if possible.
     * If there is an exception the Object is stored without being zipped.
     *
     * @param obj   The Object to zip.
     */
    public ZippedObject(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
            GZIPOutputStream zos = new GZIPOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(zos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            zippedObject = baos.toByteArray();
            unzippedObject = null;
        } catch (Exception e) {
            zippedObject = null;
            unzippedObject = obj;
        }
    }

    /**
     * getObject returns the Object stored in this ZippedObject.  It checks to see
     * if the Object has been stored as zipped or not, and return the Object.  If
     * the Object has been zipped, but can't be unzipped, null is returned.
     */
    public Object getObject() {

        if (unzippedObject != null) return unzippedObject;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(zippedObject);
            GZIPInputStream zis = new GZIPInputStream(bais);
            ObjectInputStream ois = new ObjectInputStream(zis);
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(unzippedObject);
        writer.writeByteArray(zippedObject);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        unzippedObject = reader.readObject();
        zippedObject = reader.readByteArray();
    }

    private byte[] zippedObject = null;  //a byte array to hold the zippedObject
    private Object unzippedObject = null;  //if zipping not possible, the Object is stored here
}
