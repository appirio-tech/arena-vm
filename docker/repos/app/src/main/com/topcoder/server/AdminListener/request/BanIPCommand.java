package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class BanIPCommand extends ContestMonitorRequest implements CustomSerializable {

    private String ipAddress;

    public BanIPCommand() {
    }

    public BanIPCommand(String handle) {
        this.ipAddress = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(ipAddress);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        ipAddress = reader.readString();
    }

    public String getIpAddress() {
        return ipAddress;
    }

}
