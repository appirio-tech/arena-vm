package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Represents a exchange key response message.
 * @author visualage
 */
public class ExchangeKeyResponse
        extends Message {

    protected byte[] key;

    /**
     * Construct an empty response before deserializing by calling <tt>customReadObject</tt>.
     * @see #customReadObject
     */
    public ExchangeKeyResponse() {
    }

    /**
     * Construct a exchange key response message, ready for sending.
     *
     * @param key   the partial AES key to be exchanged, key length should be 16 bytes.
     */
    public ExchangeKeyResponse(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeByteArray(key);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        key = reader.readByteArray();
    }
}
