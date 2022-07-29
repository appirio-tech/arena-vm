package com.topcoder.netCommon.testerMessages;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.Message;

/**
 * Defines a ping request for the tester applet. The ping request can carry a byte array as payload.
 * The payload is meant to test the bandwidth and correctness of the connection. 
 * 
 * @author Qi Liu
 * @version $Id: PingRequest.java 71772 2008-07-18 07:46:22Z qliu $
 */
public class PingRequest extends Message {
    /** Represents the payload to be transmitted. */
    private byte[] payload;

    /**
     * Creates a new instance of <code>PingRequest</code>. It is required by custom serialization.
     */
    public PingRequest() {
    }

    /**
     * Creates a new instance of <code>PingRequest</code>. The payload is given.
     * 
     * @param payload the payload of this ping.
     */
    public PingRequest(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Gets the payload data of this request.
     * 
     * @return the payload data of this request.
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
        return "(Ping)[Length=" + payload.length + "]";
    }
}
