package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RoomObject extends RoundIDCommand implements CustomSerializable {

    private int roomID;

    public RoomObject() {
    }

    public RoomObject(int roundID, int eventID) {
        super(roundID);
        this.roomID = eventID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomID = reader.readInt();
    }

    public int getRoomID() {
        return roomID;
    }

}
