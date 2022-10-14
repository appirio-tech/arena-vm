/**
 * Class Location
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information for a location change
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public final class Location implements Serializable, CustomSerializable {

    /*
     * Constructors
     */
    public Location(int con, int round, int room) {
        m_contestID = con;
        m_roomID = room;
        m_roundID = round;
    }

    public Location() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_contestID);
        writer.writeInt(m_roundID);
        writer.writeInt(m_roomID);
        writer.writeString(m_contestTime);
        writer.writeString(m_timeRemaining);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        m_contestID = reader.readInt();
        m_roundID = reader.readInt();
        m_roomID = reader.readInt();
        m_contestTime = reader.readString();
        m_timeRemaining = reader.readString();
    }


    /*
     * Data members/getter/setters
     */
    private int m_contestID;

    public final int getContestID() {
        return m_contestID;
    }
    //public final void setContestID(int id) { m_contestID = id;}

    private int m_roundID;

    public final int getRoundID() {
        return m_roundID;
    }
    //public final void setRoundID(int id) { m_roundID = id;}

    private int m_roomID;

    public final int getRoomID() {
        return m_roomID;
    }
    //public final void setRoomID(int id) { m_roomID = id;}

    private String m_contestTime;
    //public final String getContestTime() { return m_contestTime;}
    //public final void setContestTime(String time) { m_contestTime = time;}

    private String m_timeRemaining;
    //public final String getTimeRemaining() { return m_timeRemaining;}
    //public final void setTimeRemaining(String time) { m_timeRemaining = time;}

    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("contest: ");
        buf.append(m_contestID);
        buf.append(" round: ");
        buf.append(m_roundID);
        buf.append(" room: ");
        buf.append(m_roomID);
        buf.append(" contest time: ");
        buf.append(m_contestTime);
        buf.append(" time remaining: ");
        buf.append(m_timeRemaining);
        return buf.toString();
    }
}
