package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class GlobalBroadcastCommand extends ContestMonitorRequest implements CustomSerializable {

    String message;

    public GlobalBroadcastCommand() {
    }

    public GlobalBroadcastCommand(String message) {
        this.message = message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        message = reader.readString();
    }

    public String getMessage() {
        return message;
    }
}
