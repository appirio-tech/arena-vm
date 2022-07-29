package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class DisconnectRequest extends ContestMonitorRequest implements CustomSerializable {

    private int connId;

    public DisconnectRequest() {
    }

    public DisconnectRequest(int connId) {
        this.connId = connId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(connId);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        connId = reader.readInt();
    }

    public int getConnId() {
        return connId;
    }

}
