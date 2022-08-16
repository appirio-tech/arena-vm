package com.topcoder.netCommon.mpsqas.communication;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.security.Key;

/**
 * Manages the sending and receiving of messages.
 * @author Logan Hanks
 */
public class MPSQASMessageHandler
        extends CSHandler {

    static final protected byte MPSQAS_MESSAGE = 97;

    public MPSQASMessageHandler(Key key) {
        super(key);
    }

    protected boolean writeObjectOverride(Object object) throws IOException {
        if (object instanceof Message) {
            writeMessage((Message) object);
            return true;
        }
        return false;
    }

    private void writeMessage(Message message)
            throws IOException {
        writeByte(MPSQAS_MESSAGE);
        writeString(message.getClass().getName());
        message.customWriteObject(this);
    }

    protected Object readObjectOverride(byte type)
            throws IOException {
        if (type != MPSQAS_MESSAGE)
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: unexpected type " + type);

        String className = readString();

        if (className == null)
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: unexpected null class name");

        try {
            Class c = Class.forName(className);
            CustomSerializable message = (CustomSerializable) c.newInstance();

            message.customReadObject(this);
            return message;
        } catch (InstantiationException e) {
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: instantiation exception");
        } catch (IllegalAccessException e) {
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: illegal access exception");
        } catch (ClassNotFoundException e) {
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: class not found exception");
        }
    }
}
