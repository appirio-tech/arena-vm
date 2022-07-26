package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class SetUserStatusCommand extends ContestMonitorRequest implements CustomSerializable {

    private String handle;
    private boolean isActiveStatus;

    public SetUserStatusCommand() {
    }

    public SetUserStatusCommand(String handle, boolean activeStatus) {
        this.handle = handle;
        isActiveStatus = activeStatus;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
        writer.writeBoolean(isActiveStatus);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        handle = reader.readString();
        isActiveStatus = reader.readBoolean();
    }

    public String getHandle() {
        return handle;
    }

    public boolean isActiveStatus() {
        return isActiveStatus;
    }

}
