package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public class BootUserCommand extends ContestMonitorRequest implements CustomSerializable {

    private String handle;

    public BootUserCommand() {
    }

    public BootUserCommand(String handle) {
        this.handle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        handle = reader.readString();
    }

    public String getHandle() {
        return handle;
    }
}



