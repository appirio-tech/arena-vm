package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RefreshAllRoomsCommand extends RoundIDCommand implements CustomSerializable {

    public RefreshAllRoomsCommand() {
    }

    public RefreshAllRoomsCommand(int roundID) {
        super(roundID);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

}
