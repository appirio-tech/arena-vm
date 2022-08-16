package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class ProblemObject extends RoundIDCommand implements CustomSerializable {

    private int problemID;

    public ProblemObject() {
    }

    public ProblemObject(int roundID, int eventID) {
        super(roundID);
        this.problemID = eventID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(problemID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        problemID = reader.readInt();
    }

    public int getProblemID() {
        return problemID;
    }

}
