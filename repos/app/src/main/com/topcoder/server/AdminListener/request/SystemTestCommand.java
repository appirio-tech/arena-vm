package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class SystemTestCommand extends RoundIDCommand implements CustomSerializable {

    private int coderID;
    private int problemID;
    private boolean failOnFirstBadTest;
    private boolean reference;

    public SystemTestCommand() {
    }

    public SystemTestCommand(int roundID, int coderID, int problemID, boolean failOnFirstBadTest, boolean reference) {
        super(roundID);
        this.coderID = coderID;
        this.problemID = problemID;
        this.failOnFirstBadTest = failOnFirstBadTest;
        this.reference = reference;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(coderID);
        writer.writeInt(problemID);
        writer.writeBoolean(failOnFirstBadTest);
        writer.writeBoolean(reference);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coderID = reader.readInt();
        problemID = reader.readInt();
        failOnFirstBadTest = reader.readBoolean();
        reference = reader.readBoolean();
    }

    public int getCoderID() {
        return coderID;
    }
    
    public boolean isReference() {
        return reference;
    }

    public int getProblemID() {
        return problemID;
    }

    public boolean isFailOnFirstBadTest() {
        return failOnFirstBadTest;
    }

}
