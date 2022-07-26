package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class MoveEvent extends TCEvent {

    private int m_userID;
    private int m_roomID;

    public MoveEvent() {
    }

    public MoveEvent(int userID, int roomID) {
        super(MOVE_TYPE, ROOM_TARGET, userID);
        m_userID = userID;
        m_roomID = roomID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_userID);
        writer.writeInt(m_roomID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_userID = reader.readInt();
        m_roomID = reader.readInt();
    }

    public int getUserID() {
        return m_userID;
    }

    public int getRoomID() {
        return m_roomID;
    }
}
