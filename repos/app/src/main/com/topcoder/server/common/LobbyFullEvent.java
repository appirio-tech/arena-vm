package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class LobbyFullEvent extends TCEvent {

    private int m_userID;
    private String m_lobbyName;
    private boolean m_full;

    public LobbyFullEvent() {
    }

    public LobbyFullEvent(int userID, String lobbyName, boolean full) {
        super(LOBBY_FULL_TYPE, ALL_TARGET, userID);
        m_userID = userID;
        m_lobbyName = lobbyName;
        m_full = full;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_userID);
        writer.writeString(m_lobbyName);
        writer.writeBoolean(m_full);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_userID = reader.readInt();
        m_lobbyName = reader.readString();
        m_full = reader.readBoolean();
    }

    public int getUserID() {
        return m_userID;
    }

    public String getLobbyName() {
        return m_lobbyName;
    }

    public boolean getFull() {
        return m_full;
    }
}
