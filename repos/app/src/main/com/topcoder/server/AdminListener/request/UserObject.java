package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class UserObject extends RoundIDCommand implements CustomSerializable {

    private String handle;

    public UserObject() {
    }

    public UserObject(int roundID, String handle) {
        super(roundID);
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
