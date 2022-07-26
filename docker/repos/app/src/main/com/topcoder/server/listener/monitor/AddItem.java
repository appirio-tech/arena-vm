package com.topcoder.server.listener.monitor;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

public final class AddItem extends ActionItem {

    private String remoteIP;

    public AddItem() {
    }

    AddItem(int id, String remoteIP) {
        super(id);
        this.remoteIP = remoteIP;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(remoteIP);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        remoteIP = reader.readString();
    }

    public String toString() {
        return getId() + " " + remoteIP;
    }

}
