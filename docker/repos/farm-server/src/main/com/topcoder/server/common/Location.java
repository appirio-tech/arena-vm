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

	private int contestID;
	private int roomID;
    private String contestTime;
    private String timeRemaining;
    private int roundID;
	
    /*
     * Constructors
     */
    public Location(int con, int round, int room) {
        contestID = con;
        roomID = room;
        roundID = round;
    }

    public Location() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestID);
        writer.writeInt(roundID);
        writer.writeInt(roomID);
        writer.writeString(contestTime);
        writer.writeString(timeRemaining);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        contestID = reader.readInt();
        roundID = reader.readInt();
        roomID = reader.readInt();
        contestTime = reader.readString();
        timeRemaining = reader.readString();
    }


    /*
     * Data members/getter/setters
     */
    
	public final int getContestID() {
		return contestID;
	}

	public final void setContestID(int id) {
		contestID = id;
	}

	public final int getRoundID() {
		return roundID;
	}

	public final void setRoundID(int id) {
		roundID = id;
	}

	public final int getRoomID() {
		return roomID;
	}

	public final void setRoomID(int id) {
		roomID = id;
	}

	public final String getContestTime() {
		return contestTime;
	}

	public final void setContestTime(String time) {
		contestTime = time;
	}

	public final String getTimeRemaining() {
		return timeRemaining;
	}

	public final void setTimeRemaining(String time) {
		timeRemaining = time;
	}

    public String toString() {
        StringBuilder buf = new StringBuilder(100);
        buf.append("contest: ");
        buf.append(contestID);
        buf.append(" round: ");
        buf.append(roundID);
        buf.append(" room: ");
        buf.append(roomID);
        buf.append(" contest time: ");
        buf.append(contestTime);
        buf.append(" time remaining: ");
        buf.append(timeRemaining);
        return buf.toString();
    }
    
    
}
