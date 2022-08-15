package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class SpecAppShowRoomRequest extends ContestMonitorRequest {

    long roomID;

    public SpecAppShowRoomRequest(long roomID) {
        this.roomID = roomID;
    }

    public SpecAppShowRoomRequest() {
    }

    public long getRoomID() {
        return roomID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(roomID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roomID = reader.readLong();
    }
}



