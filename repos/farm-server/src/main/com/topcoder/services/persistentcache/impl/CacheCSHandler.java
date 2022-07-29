/*
 * CacheCSHandler
 * 
 * Created 05/18/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

import org.apache.log4j.Logger;

import com.topcoder.shared.netCommon.CSHandler;

/**
 * Serializer for Cache Objects.<p>
 * 
 * This class adds Serializable serialization capability to CSHandler class and uses
 * the ClassLoader obtained from Thread.getContextClassLoader() to find classes required
 * for deserialization. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: CacheCSHandler.java 68012 2008-01-16 18:55:14Z thefaxman $
 */
public class CacheCSHandler extends CSHandler {
    private Logger log = Logger.getLogger(CacheCSHandler.class);
    private static final byte SERIALIZABLE_ID = 99;

    public CacheCSHandler() {
    }


    public void writeObjectForCache(Object obj) throws IOException {
        /*
         * We need to catch types that are not properly handled by CSHandler. Big arrays,
         * and x[][] where the x is different of String or double.
         * In addition if we Serialize any object of the type x[] where x is different from 
         * primitive type, String, or Object. CSHandler will serialize it as a Object[].
         * 
         * This is an adhoc implementation and will work for one dimensional arrays and n x m Matrixes.
         */
        boolean supported = isProperlySupportedByCSHandler(obj);
        if (!supported) {
            writeSerializable(obj);
        } else {
            writeObject(obj);
        }
    }

    private boolean isProperlySupportedByCSHandler(Object obj) {
        boolean supported = true;
        if (obj != null && obj.getClass().isArray()) {
            //Arrays longer than Short.MAX_VALUE are not supported by CSHandler
            if (Array.getLength(obj) > Short.MAX_VALUE) {
                supported = false;
            } else {
                Class type = obj.getClass().getComponentType();
                if (type.isArray()) {
                    //Only double[][] Object[][] and String[][] are supported by CSHandler, 
                    //if the number of columns is less or equal to Short.MAX_VALUE.
                    Object row = Array.get(obj, 0);
                    if (row != null && Array.getLength(row) > Short.MAX_VALUE) {
                        supported = false;
                    } else if (type.getComponentType() != double.class && type.getComponentType() != Object.class) {
                        supported = false;
                    }
                } else if (type != String.class && !type.isPrimitive() && type != Object.class) {
                    //Only String[] Object[] and primitive arrays are handled.
                    supported = false;
                }
            }
        }
        return supported;
    }
        
    protected boolean writeObjectOverride(Object object) throws IOException {
        return false;
    }
    
    protected void writeUnhandledObject(Object object) throws IOException {
        if (!writeSerializable(object)) {
            super.writeUnhandledObject(object);
        }
    }

    protected Object readObjectOverride(byte type) throws IOException {
        if (type == SERIALIZABLE_ID) {
            return readObjectSerializable();
        } else {
            return super.readObjectOverride(type);
        }
    }

    protected boolean writeSerializable(Object object) throws IOException {
        if (object instanceof Serializable) {
            writeByte(SERIALIZABLE_ID);
            writeJustSerializable(object);
            return true;
        }
        return false;
    }

    private void writeJustSerializable(Object object) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(50);
        ObjectOutputStream stream = new ObjectOutputStream(bs);
        stream.writeObject(object);
        stream.flush();
        stream.close();
        writeByteArray(bs.toByteArray());
    }
    
    protected Object readObjectSerializable() throws IOException {
        byte[] bs = readByteArray();
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bs)) {
            protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader != null && classLoader != ClassLoader.getSystemClassLoader()) {
                    try {
                        return Class.forName(desc.getName(), false, classLoader);
                    } catch (Exception e) {
                        log.error("Could not find serializable class", e);
                    }
                }
                return super.resolveClass(desc);
            }
        };
        try {
            return stream.readObject();
        } catch (ClassNotFoundException e) {
            throw (StreamCorruptedException) new StreamCorruptedException().initCause(e);
        }
    }
    
    protected Class findClassGuarded(String name) {
        try {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            log.error("Could not find class:"+name, e);
            return null;
        }
    }
}
