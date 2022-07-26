package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class RoundBroadcastCommand extends RoundIDCommand {

    String message;

    public RoundBroadcastCommand() {
    }

    public RoundBroadcastCommand(int roundId, String message) {
        super(roundId);
        this.message = message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        message = reader.readString();
    }

    public String getMessage() {
        return message;
    }
}
