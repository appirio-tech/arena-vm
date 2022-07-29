package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RoundObject extends RoundIDCommand implements CustomSerializable {

    private int contestID;

    public RoundObject() {
    }

    public RoundObject(int contestID, int roundID) {
        super(roundID);
        this.contestID = contestID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(contestID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        contestID = reader.readInt();
    }

    public int getContestID() {
        return contestID;
    }
}
