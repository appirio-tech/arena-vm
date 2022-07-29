package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class ClearPracticeRoomsCommand extends ContestMonitorRequest implements CustomSerializable {

    private int type;

    public ClearPracticeRoomsCommand() {
    }

    public ClearPracticeRoomsCommand(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(type);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        type = reader.readInt();
    }

}
