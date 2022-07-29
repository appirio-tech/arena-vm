package com.topcoder.server.listener.monitor;

import java.io.*;
import java.util.Date;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public final class CachedItem extends ContestMonitorResponse implements Serializable, CustomSerializable {

    private String message;
    private long timestamp;

    CachedItem() {
    }

    CachedItem(String message) {
        this.message = message;
        timestamp = System.currentTimeMillis();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
        writer.writeLong(timestamp);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        message = reader.readString();
        timestamp = reader.readLong();
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        Date date = new Date(timestamp);
        return "" + date;
    }

}
