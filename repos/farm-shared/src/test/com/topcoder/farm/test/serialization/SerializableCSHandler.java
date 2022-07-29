/*
 * SerializableCSHandler
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.test.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import com.topcoder.shared.netCommon.CSHandler;

/**
 * This CSHandler can handle any serializable object
 *   
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SerializableCSHandler extends CSHandler {
    byte SERIALIZABLE = 99;
    
    protected boolean writeObjectOverride(Object object) throws IOException {
        if (object instanceof Serializable) {
            writeByte(SERIALIZABLE);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(bs);
            stream.writeObject(object);
            stream.flush();
            stream.close();
            writeByteArray(bs.toByteArray());
            return true;
        }
        return false;
    }
    
    protected Object readObjectOverride(byte type) throws IOException {
        if (type == SERIALIZABLE) {
            byte[] bs = readByteArray();
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bs));
            try {
                return stream.readObject();
            } catch (ClassNotFoundException e) {
                throw (StreamCorruptedException) new StreamCorruptedException().initCause(e);
            }
        }
        throw new StreamCorruptedException();
    }
}
