package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class ReplayCompileEvent extends TCEvent implements CustomSerializable {

    Submission m_sub;

    public Submission getSub() {
        return m_sub;
    }


    // just used for replay
    int m_userID;

    public int getUserID() {
        return m_userID;
    }

    int m_roomID;

    public int getRoomID() {
        return m_roomID;
    }

    int m_problemID;

    public int getProblemID() {
        return m_problemID;
    }

    String m_src;

    public String getSrc() {
        return m_src;
    }

    int m_lang;

    public int getLanguage() {
        return m_lang;
    }

    public ReplayCompileEvent() {
    }

    public ReplayCompileEvent(int userId, int roomId, int problemId, String src, Submission sub) {
        super(REPLAY_COMPILE_TYPE, ROOM_TARGET, userId);
        m_userID = userId;
        m_roomID = roomId;
        m_problemID = problemId;
        m_sub = sub;
        m_src = src;
        m_lang = sub.getLanguage();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_userID);
        writer.writeInt(m_roomID);
        writer.writeInt(m_problemID);
        writer.writeString(m_src);
        writer.writeObject(m_sub);
        writer.writeInt(m_lang);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_userID = reader.readInt();
        m_roomID = reader.readInt();
        m_problemID = reader.readInt();
        m_src = reader.readString();
        m_sub = (Submission) reader.readObject();
        m_lang = reader.readInt();
    }

}
