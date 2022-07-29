/*
 * Message
 * 
 * Created 07/18/2007
 */
package com.topcoder.shared.i18n;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * The Message class represents a self contained message.
 * 
 * A message contains the required information that allows 
 * rendering the message in different languages/formats.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Message implements Serializable, CustomSerializable {
    /**
     * The bundle name where they key must be searched
     */
    private String bundleName;
    /**
     * The key of the message
     */
    private String key;
    /**
     * Value arguments that can be merged into the resulting message 
     */
    private Object[] values;
    
    public Message() {
    }
    
    public Message(String key) {
        this("default", key);
    }
    
    public Message(String bundleName, String key) {
        this.bundleName = bundleName;
        this.key = key;
    }

    public Message(String bundleName, String key, Object[] values) {
        this.bundleName = bundleName;
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public Object[] getValues() {
        return values;
    }

    public String getBundleName() {
        return bundleName;
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.bundleName = reader.readString();
        this.key = reader.readString();
        this.values = reader.readObjectArray();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(bundleName);
        writer.writeString(key);
        writer.writeObjectArray(values);
    }
    
    public String toString() {
        return "bundle=" + bundleName + ", key=" + key + ", values=[" + (values == null ? "null" : String.valueOf(values.length)) + "]";
    }
}
