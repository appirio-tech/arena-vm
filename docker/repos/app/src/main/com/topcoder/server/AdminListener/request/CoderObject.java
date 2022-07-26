package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;


public final class CoderObject extends RoundIDCommand implements CustomSerializable {

    private int roomID;
    private int coderID;

    public CoderObject() {
    }

    public CoderObject(int roundID, int roomID, int coderID) {
        super(roundID);
        this.roomID = roomID;
        this.coderID = coderID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
        writer.writeInt(coderID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomID = reader.readInt();
        coderID = reader.readInt();
    }

    public int getRoomID() {
        return roomID;
    }

    public int getCoderID() {
        return coderID;
    }

}
