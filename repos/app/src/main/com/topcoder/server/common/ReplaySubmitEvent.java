package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class ReplaySubmitEvent extends TCEvent implements CustomSerializable {

    // just used for replay
    int m_userID;

    public int getUserID() {
        return m_userID;
    }

    int m_roomID;

    public int getRoomID() {
        return m_roomID;
    }

    int m_componentID;

    public int getComponentID() {
        return m_componentID;
    }

    int m_subVal;

    public int getSubVal() {
        return m_subVal;
    }

    String m_src;

    public String getSrc() {
        return m_src;
    }

    public ReplaySubmitEvent() {
    }

    public ReplaySubmitEvent(int userId, int roomId, int problemId, int value, String src) {
        super(REPLAY_SUBMIT_TYPE, ROOM_TARGET, userId);
        m_userID = userId;
        m_roomID = roomId;
        m_componentID = problemId;
        m_subVal = value;
        m_src = src;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_userID);
        writer.writeInt(m_roomID);
        writer.writeInt(m_componentID);
        writer.writeInt(m_subVal);
        writer.writeString(m_src);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_userID = reader.readInt();
        m_roomID = reader.readInt();
        m_componentID = reader.readInt();
        m_subVal = reader.readInt();
        m_src = reader.readString();
    }

}
