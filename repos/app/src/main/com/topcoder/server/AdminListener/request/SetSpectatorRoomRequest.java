package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class SetSpectatorRoomRequest extends RoundIDCommand {

    private int roomId;


    public SetSpectatorRoomRequest() {
    }


    public SetSpectatorRoomRequest(int roundId, int roomId) {
        super(roundId);
        this.roomId = roomId;
    }


    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomId);
    }


    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomId = reader.readInt();
    }


    public int getRoomId() {
        return roomId;
    }

}



