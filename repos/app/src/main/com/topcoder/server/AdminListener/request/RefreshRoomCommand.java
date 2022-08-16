package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RefreshRoomCommand extends RoundIDCommand implements CustomSerializable {

    private int roomID;

    public RefreshRoomCommand() {
    }

    public RefreshRoomCommand(int roundID, int roomID) {
        super(roundID);
        this.roomID = roomID;
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
