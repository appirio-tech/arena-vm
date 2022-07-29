package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class LeaderEvent extends TCEvent {

    private long m_roundID;
    private long m_roomID;
    private String m_leaderName;
    private int m_leaderSeed;
    private int m_leaderRating;
    private double m_leaderPoints;
    private boolean m_isClose;

    public LeaderEvent() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(m_roundID);
        writer.writeLong(m_roomID);
        writer.writeString(m_leaderName);
        writer.writeInt(m_leaderSeed);
        writer.writeInt(m_leaderRating);
        writer.writeDouble(m_leaderPoints);
        writer.writeBoolean(m_isClose);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_roundID = reader.readLong();
        m_roomID = reader.readLong();
        m_leaderName = reader.readString();
        m_leaderSeed = reader.readInt();
        m_leaderRating = reader.readInt();
        m_leaderPoints = reader.readDouble();
        m_isClose = reader.readBoolean();
    }

    public LeaderEvent(BaseCodingRoom room) {
        super(LEADER_TYPE, ALL_TARGET, room.getRoomID());
        m_roundID = room.getRoundID();
        m_roomID = room.getRoomID();
        RoomLeaderInfo leaderInfo = room.getLeaderInfo();
        Coder leader = leaderInfo.getCoder();
        m_leaderName = leader.getName();
        m_leaderSeed = leaderInfo.getSeed();
        m_leaderRating = leader.getRating();
        m_leaderPoints = leaderInfo.getPoints();
        m_isClose = leaderInfo.isCloseContest();
    }

    public long getRoomID() {
        return m_roomID;
    }


    public long getRoundID() {
        return m_roundID;
    }


    public String getLeaderName() {
        return m_leaderName;
    }

    public int getLeaderSeed() {
        return m_leaderSeed;
    }

    public int getLeaderRating() {
        return m_leaderRating;
    }

    public double getLeaderPoints() {
        return m_leaderPoints;
    }

    public boolean getIsClose() {
        return m_isClose;
    }
}
