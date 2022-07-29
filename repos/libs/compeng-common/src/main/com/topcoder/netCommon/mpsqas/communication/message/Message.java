package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Base class for all messages
 * @author Logan Hanks
 */
abstract public class Message
        implements Serializable, Cloneable, CustomSerializable {

    // abstract methods from CustomSerializable repeated here for reference

    abstract public void customWriteObject(CSWriter writer)
            throws IOException;

    abstract public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException;
}
