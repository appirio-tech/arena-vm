package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public abstract class RoundIDCommand extends ContestMonitorRequest implements CustomSerializable {

    private int roundID;

    RoundIDCommand() {
    }

    RoundIDCommand(int roundID) {
        this.roundID = roundID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roundID = reader.readInt();
    }

    public final int getRoundID() {
        return roundID;
    }

}
