package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class PhaseEvent extends TCEvent {

    protected int m_phase;
    protected int m_round;
    protected int m_contest;
    protected String m_lobbyStatus;
    protected boolean m_sendPopups;
    
    public PhaseEvent() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_phase);
        writer.writeInt(m_round);
        writer.writeInt(m_contest);
        writer.writeBoolean(m_sendPopups);
        writer.writeString(m_lobbyStatus);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_phase = reader.readInt();
        m_round = reader.readInt();
        m_contest = reader.readInt();
        m_sendPopups = reader.readBoolean();
        m_lobbyStatus = reader.readString();
        
    }


    public PhaseEvent(int contestID, int roundID, int phase, String lobbyStatus) {
        super(PHASE_TYPE, ALL_TARGET, contestID);
        m_round = roundID;
        m_contest = contestID;
        m_phase = phase;
        m_lobbyStatus = lobbyStatus;
        m_sendPopups = true;
    }
    
    public PhaseEvent(int contestID, int roundID, int phase, String lobbyStatus, boolean popups) {
        super(PHASE_TYPE, ALL_TARGET, contestID);
        m_round = roundID;
        m_contest = contestID;
        m_phase = phase;
        m_lobbyStatus = lobbyStatus;
        m_sendPopups = popups;
    }
    
    public boolean getPopups()
    {
        return m_sendPopups;
    }

    public int getRound() {
        return m_round;
    }

    public int getPhase() {
        return m_phase;
    }

    public int getContest() {
        return m_contest;
    }

    public String getLobbyStatus() {
        return m_lobbyStatus;
    }
}
