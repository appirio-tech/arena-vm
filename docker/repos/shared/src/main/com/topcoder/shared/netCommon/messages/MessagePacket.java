/**
 * MessagePacket.java
 *
 * Description:		A packet of messages
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

public class MessagePacket implements Serializable, Cloneable, CustomSerializable {

    /** constant for an unknown/unspecified ID */
    public static final int UNKNOWNID = -1;

    /** unique identifier of the message packet */
    public int ID;

    /** the messages */
    public ArrayList messages;

    /**
     * Default constructor. This is the same as <code>new MessagePacket(UNKNOWNID, Collections.EMPTY_LIST)</code>
     */
    public MessagePacket() {
        this(UNKNOWNID, new ArrayList());
    }

    /**
     * Default constructor.
     *
     * @param msg    message to create the message packet for
     */
    public MessagePacket(Message msg) {
        this();
        add(msg);
    }

    /**
     * Creates a message packet with the specified ID. This is the same as <code>new MessagePacket(ID, Collections.EMPTY_LIST)</code>
     *
     * @param ID the unique identifier of the message packet
     */
    public MessagePacket(int ID) {
        this(ID, new ArrayList());
    }

    /**
     * Creates a message packet with the a list of messages.  This is the same as <code>new MessagePacket(MessagePacket.UNKNOWNID, messages)</code>
     *
     * @param messages list of message objects
     *
     * @see com.topcoder.shared.netCommon.messages.Message
     */
    public MessagePacket(List messages) {
        this(UNKNOWNID, messages);
    }

    /**
     * Creates a message packet with the specified ID and messages.
     *
     * @param ID       the unique identifier of the message packet
     * @param messages list of message objects
     *
     * @see com.topcoder.shared.netCommon.messages.Message
     * @see java.util.List
     */
    public MessagePacket(int ID, List messages) {
        this.ID = ID;
        this.messages = new ArrayList(messages);
    }

    /**
     * Adds a message to the message packet
     *
     * @param message the message to add
     *
     * @see com.topcoder.shared.netCommon.messages.Message
     * @see java.util.List
     */
    public void add(Message message) {
        messages.add(message);
    }

    /**
     * Adds all message to the message packet
     *
     * @param message the message list to add
     *
     * @see com.topcoder.shared.netCommon.messages.Message
     * @see java.util.List
     */
    public void addAll(List message) {
        messages.addAll(message);
    }


    /**
     * Removes a message from the message packet
     *
     * @param message the message to remove
     * @return the message if removed (null if not found)
     *
     * @see com.topcoder.shared.netCommon.messages.Message
     * @see java.util.List
     */
    public Message remove(Message message) {
        int pos = messages.indexOf(message);
        if (pos < 0) return null;

        return (Message) messages.remove(pos);
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(ID);
        writer.writeArrayList(messages);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        ID = reader.readInt();
        messages = reader.readArrayList();
    }

    /**
     * Gets the ID
     * @return the unique identifier of the message packet
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets the ID
     */
    public void setID(int id) {
        ID = id;
    }
    
    /**
     * Gets the messages
     * @return a non-mutable list of messages
     */
    public ArrayList getMessages() {
        return new ArrayList(messages);
    }

    /**
     * Sets the messages
     */
    public void setMessages(ArrayList messages) {
        this.messages = new ArrayList(messages);
    }

    /**
     * Creates a default shallow clone of the object.
     *
     * @return a shallow copy of this object
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Creates a default shallow clone of the object.
     *
     * @return a shallow copy of this object
     */
    public String toString() {
        return new StringBuffer().append("(MessagePacket)[").append(ID).append(", ").append(messages.toString()).append("]").toString();
    }

}


/* @(#)MessagePacket.java */
