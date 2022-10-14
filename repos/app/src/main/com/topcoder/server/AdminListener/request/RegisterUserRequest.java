package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class RegisterUserRequest extends RoundIDCommand {

    private String handle;
    private boolean atLeast18;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(int roundId, String handle, boolean atLeast18) {
        super(roundId);
        this.handle = handle;
        this.atLeast18 = atLeast18;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(handle);
        writer.writeBoolean(atLeast18);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        handle = reader.readString();
        atLeast18 = reader.readBoolean();
    }

    public String getHandle() {
        return handle;
    }

    public boolean getAtLeast18() {
        return atLeast18;
    }
}

