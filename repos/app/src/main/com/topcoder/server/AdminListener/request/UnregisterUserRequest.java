package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class UnregisterUserRequest extends RoundIDCommand {

    private String handle;

    public UnregisterUserRequest() {
    }

    public UnregisterUserRequest(int roundId, String handle) {
        super(roundId);
        this.handle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(handle);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        handle = reader.readString();
    }

    public String getHandle() {
        return handle;
    }
}


