package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public final class SetForwardingAddressRequest extends ContestMonitorRequest implements CustomSerializable {

    private String address;
    private boolean done;

    public SetForwardingAddressRequest() {
    }

    public SetForwardingAddressRequest(String address) {
        super();
        this.address = address;
        done = false;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(address);
        writer.writeBoolean(done);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        address = reader.readString();
        done = reader.readBoolean();
    }

    public String getAddress() {
        return address;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}

