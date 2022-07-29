package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class AddTimeCommand extends RoundIDCommand implements CustomSerializable {

    private int minutes;
    private int seconds;
    private int phase;
    private boolean addToStart;

    public AddTimeCommand() {
    }

    public AddTimeCommand(int roundID, int minutes, int seconds, int phase, boolean addToStart) {
        super(roundID);
        this.minutes = minutes;
        this.seconds = seconds;
        this.phase = phase;
        this.addToStart = addToStart;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(minutes);
        writer.writeInt(seconds);
        writer.writeInt(phase);
        writer.writeBoolean(addToStart);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        minutes = reader.readInt();
        seconds = reader.readInt();
        phase = reader.readInt();
        addToStart = reader.readBoolean();
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getPhase() {
        return phase;
    }

    public boolean isAddToStart() {
        return addToStart;
    }

}
