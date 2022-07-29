package com.topcoder.server.listener.monitor;

import java.io.*;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

public abstract class ActionItem implements Serializable, CustomSerializable {

    private int id;
    private long time = System.currentTimeMillis();

    ActionItem() {
    }

    ActionItem(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeLong(time);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        id = reader.readInt();
        time = reader.readLong();
    }

    public final int getId() {
        return id;
    }

}
