package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class ComponentBroadcastCommand extends RoundIDCommand {

    String message;
    int problemId;

    public ComponentBroadcastCommand() {
    }

    public ComponentBroadcastCommand(int roundId, String message, int problemId) {
        super(roundId);
        this.message = message;
        this.problemId = problemId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(message);
        writer.writeInt(problemId);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        message = reader.readString();
        problemId = reader.readInt();
    }

    public String getMessage() {
        return message;
    }

    public int getProblemId() {
        return problemId;
    }
}
