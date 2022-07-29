package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Represents a exchange key request message.
 * @author visualage
 */
public class ExchangeKeyRequest
        extends Message {

    protected byte[] key;

    /**
     * Construct an empty request before deserializing by calling <tt>customReadObject</tt>.
     * @see #customReadObject
     */
    public ExchangeKeyRequest() {
    }

    /**
     * Construct a exchange key request message, ready for sending.
     *
     * @param key   the partial AES key to be exchanged, key length should be 16 bytes.
     */
    public ExchangeKeyRequest(byte[] key) {
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
