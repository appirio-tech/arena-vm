/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.socket.messages;
import java.io.IOException;
import java.util.UUID;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.Message;

/**
 * The base socket message.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and
 * Round Problems):
 * <ol>
 *      <li>Remove implementing Serializable.</li>
 *      <li>Add extending Message to implement CustomSerializable.</li>
 * </ol>
 * </p>
 *
 * @see com.topcoder.shared.netCommon.messages.Message
 * @author gondzo, dexy
 * @version 1.1
 */
public class SocketMessage extends Message {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4682290970953628584L;

    /**
     * The uuid.
     */
    private UUID uuid;

    /**
     * The message.
     */
    private Object message;

    /**
     * Instantiates a new socket message.
     */
    public SocketMessage() {

    }
    /**
     * Creates a new instance of this class.
     *
     * @param   uuid    the uuid
     * @param   message    the message
     */
    public SocketMessage(UUID uuid, Object message) {
        super();
        this.uuid = uuid;
        this.message = message;
    }

    /**
     * Getter for the uuid.
     *
     * @return the uuid.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Setter for the uuid.
     *
     * @param uuid the uuid.
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Getter for the message.
     *
     * @return the message.
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Setter for the message.
     *
     * @param message the message.
     */
    public void setMessage(Object message) {
        this.message = message;
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @Override
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(uuid.getMostSignificantBits());
        writer.writeLong(uuid.getLeastSignificantBits());
        writer.writeObject(message);
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    @Override
    public void customReadObject(CSReader reader) throws IOException {
        long mostSignificantBits = reader.readLong();
        long leastSignificantBits = reader.readLong();
        uuid = new UUID(mostSignificantBits, leastSignificantBits);
        message = reader.readObject();
    }
}
