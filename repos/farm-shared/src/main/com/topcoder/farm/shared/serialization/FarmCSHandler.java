/*
 * FarmCSHandler
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.shared.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.customserializer.CustomSerializerProvider;

/**
 * Base class for farm serializer
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FarmCSHandler extends CSHandler {
    private Log log = LogFactory.getLog(FarmCSHandler.class);
    private final Map writeMap;
    private final Map readMap;
    private boolean allowNonCustomSerializableClasses = Boolean.getBoolean("com.topcoder.farm.shared.serialization.FarmCSHandler.allowNonCustomSerializableClasses");
    
    private byte serializableId;
    
    /*
     * Just a little implementation change to avoid problems with inheritance.
     * And to avoid so many ifs and instanceof. 
     */
    public FarmCSHandler(CustomSerializerProvider serializerProvider, Map writeMap, Map readMap, byte serializableId) {
        super(serializerProvider);
        this.writeMap = writeMap;
        this.readMap = readMap;
        this.serializableId = serializableId;
    }

    protected boolean writeObjectOverride(Object object) throws IOException {
        Class clazz = object.getClass();
        Byte classId = (Byte) writeMap.get(clazz);
        if (classId != null) {
            writeByte(classId.byteValue());
            customWriteObject(object);
            return true;
        }  
//        else {
//            //DEBUG 
//            if (clazz.getName().startsWith("com.topcoder")) {
//                System.out.println("ADDBYTEID: "+clazz.getName());
//            }
//        }
        return false;
    }
    
    protected void writeUnhandledObject(Object object) throws IOException {
        if (!writeSerializable(object)) {
            super.writeUnhandledObject(object);
        }
    }

    protected Object readObjectOverride(byte type) throws IOException {
        Class clazz = (Class) readMap.get(new Byte(type));
        if (clazz != null) {
            return readCustomSerializable(clazz);
        } else {
            if (type == serializableId) {
                return readObjectSerializable();
            } else {
                return super.readObjectOverride(type);
            }
        }
    }

    protected boolean writeSerializable(Object object) throws IOException {
        if (object instanceof Serializable) {
            if (!allowNonCustomSerializableClasses) {
                log.fatal("SERIALIZABLE CLASS: "+object.getClass().getName()+"\nCustomSerializable Should be implemented!!!");
            }
            writeByte(serializableId);
            ByteArrayOutputStream bs = new ByteArrayOutputStream(50);
            ObjectOutputStream stream = new ObjectOutputStream(bs);
            stream.writeObject(object);
            stream.flush();
            stream.close();
            writeByteArray(bs.toByteArray());
            return true;
        }
        return false;
    }
    
    protected Object readObjectSerializable() throws IOException {
        byte[] bs = readByteArray();
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bs));
        try {
            return stream.readObject();
        } catch (ClassNotFoundException e) {
            throw (StreamCorruptedException) new StreamCorruptedException().initCause(e);
        }
    }
}
