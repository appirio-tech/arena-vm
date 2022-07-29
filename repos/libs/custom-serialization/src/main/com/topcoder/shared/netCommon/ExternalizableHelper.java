/*
 * ExternalizableHelper Created 3/23/2007
 */
package com.topcoder.shared.netCommon;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;

/**
 * Provides helper methods to externalize a custom serializable object. The externalizer can be defined by the system
 * property 'com.topcoder.shared.netCommon.externalizable.handler'. The class name should be the value of the property.
 * The handler class should be an sub-class of <code>CSHandler</code>, which provides a default constructor. <br>
 * This class is thread-safe, since it uses <code>ThreadLocal</code> to get the <code>CSHandler</code> instance used
 * to serialize the object.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExternalizableHelper {
    /** The default class name of the handler. */
    public static final String DEFAULT_HANDLER_VALUE = "com.topcoder.server.serialization.ExternalizableCSHandler";

    /** The property key where the class name of the handler is stored. */
    public static final String DEFAULT_HANDLER_KEY = "com.topcoder.shared.netCommon.externalizable.handlerClass";

    /** The constructor of the handler. */
    private static Constructor handlerConstructor;

    /** The thread local variable to store the serialization handler used in the externalizer. */
    private static ThreadLocal threadLocal = new ThreadLocal() {
        protected Object initialValue() {
            try {
                if (handlerConstructor == null) {
                    String className = System.getProperty(DEFAULT_HANDLER_KEY, DEFAULT_HANDLER_VALUE);
                    handlerConstructor = Class.forName(className).getConstructor(new Class[] {});
                }
                return handlerConstructor.newInstance(new Object[] {});
            } catch (Exception e) {
                throw (IllegalStateException) new IllegalStateException(
                    "Exception trying to instantiate externalizable handler").initCause(e);
            }
        }
    };

    /**
     * Serializes the given object into the given output. The serialization is done by a thread local serialization
     * handler.
     * 
     * @param out the output where the serialized object is written to.
     * @param obj the object to be serialized.
     * @throws IOException if I/O error occurs.
     */
    public static void writeExternal(final ObjectOutput out, CustomSerializable obj) throws IOException {
        CSHandler handler = getInstance();
        handler.setDataOutput(new BasicTypeDataOutputImpl(new OutputStream() {
            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
            }

            public void write(byte[] b) throws IOException {
                out.write(b);
            }

            public void write(int b) throws IOException {
                out.write(b);
            }

            public void close() throws IOException {
                out.close();
            }

            public void flush() throws IOException {
                out.flush();
            }
        }));
        obj.customWriteObject(handler);
        handler.setDataOutput(null);
    }

    /**
     * Deserializes from the given input. The deserialized object is written to the given object. The serialization is
     * done by a thread local serialization handler.
     * 
     * @param in the input where the serialized object is read from.
     * @param obj the deserialized object to be assigned to.
     * @throws IOException if I/O error occurs.
     */
    public static void readExternal(final ObjectInput in, CustomSerializable obj) throws IOException {
        CSHandler handler = getInstance();
        handler.setDataInput(new BasicTypeDataInputImpl(new InputStream() {
            public boolean markSupported() {
                return false;
            }

            public int read() throws IOException {
                return in.read();
            }

            public int read(byte[] b, int off, int len) throws IOException {
                return in.read(b, off, len);
            }

            public int read(byte[] b) throws IOException {
                return in.read(b);
            }

            public long skip(long n) throws IOException {
                return in.skip(n);
            }

            public int available() throws IOException {
                return in.available();
            }

            public void close() throws IOException {
                in.close();
            }
        }));
        obj.customReadObject(handler);
        handler.setDataInput(null);
    }

    /**
     * Retrieves the thread-local serialization handler used by this externalizer.
     * 
     * @return the thread-local serialization handler.
     */
    public static CSHandler getInstance() {
        return (CSHandler) threadLocal.get();
    }
}
