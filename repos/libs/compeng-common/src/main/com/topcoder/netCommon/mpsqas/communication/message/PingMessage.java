package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class PingMessage
        extends Message {

    private long timestamp;

    public PingMessage() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeLong(timestamp);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        timestamp = reader.readLong();
    }
}

