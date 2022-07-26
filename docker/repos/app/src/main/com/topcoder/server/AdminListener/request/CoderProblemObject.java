package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class CoderProblemObject extends RoundIDCommand implements CustomSerializable {

    private int roomID;
    private int coderID;
    private int problemIndex;

    public CoderProblemObject() {
    }

    public CoderProblemObject(int roundID, int roomID, int coderID, int problemIndex) {
        super(roundID);
        this.roomID = roomID;
        this.coderID = coderID;
        this.problemIndex = problemIndex;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
        writer.writeInt(coderID);
        writer.writeInt(problemIndex);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomID = reader.readInt();
        coderID = reader.readInt();
        problemIndex = reader.readInt();
    }

    public int getRoomID() {
        return roomID;
    }

    public int getCoderID() {
        return coderID;
    }

    public int getProblemIndex() {
        return problemIndex;
    }

}
