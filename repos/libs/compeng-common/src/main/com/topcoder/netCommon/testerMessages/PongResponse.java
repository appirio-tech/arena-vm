package com.topcoder.netCommon.testerMessages;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.Message;

/**
 * Defines a pong response for the tester applet. The pong request carries the same byte array as
 * in the ping request.
 * 
 * @author Qi Liu
 * @version $Id: PongResponse.java 71772 2008-07-18 07:46:22Z qliu $
 */
public class PongResponse extends Message {
    /** Represents the payload to be transmitted. */
    private byte[] payload;

    /**
     * Creates a new instance of <code>PongResponse</code>. It is required by custom serialization.
     */
    public PongResponse() {
    }

    /**
     * Creates a new instance of <code>PongResponse</code>. The payload is given.
     * 
     * @param payload the payload of this ping.
     */
    public PongResponse(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Gets the payload data of this response.
     * 
     * @return the payload data of this response.
     */
    public byte[] getPayload() {
        return payload;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeByteArray(payload);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        payload = reader.readByteArray();
    }

    public String toString() {
        return "(Pong)[Length=" + payload.length + "]";
    }
}
