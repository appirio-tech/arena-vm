/**
 * A generic broadcast message. User: Michael Cervantes (emcee) Date: Apr 6, 2002
 */
package com.topcoder.netCommon.contestantMessages;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a broadcast message from the admins to all logged in users.
 * 
 * @author Michael Cervantes (emcee)
 * @version $Id: AdminBroadcast.java 72093 2008-08-05 07:34:40Z qliu $
 */
public class AdminBroadcast implements CustomSerializable, Serializable, Comparable {

    /**
     * Orders broadcasts in reverse order of broadcast time.
     */
    public int compareTo(Object o) {
        AdminBroadcast ab = (AdminBroadcast) o;
        long diff = (time - ab.time);
        if (diff < 0)
            return 1;
        else if (diff > 0)
            return -1;
        if (getType() != ab.getType()) {
            return getType() - ab.getType();
        }
        return message.compareTo(ab.message);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(time);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException {
        time = reader.readLong();
        message = reader.readString();
    }

    /**
     * Gets the time of the broadcast. The time is represented by the number of milliseconds since January 1, 1970,
     * 00:00:00 GMT.
     * 
     * @return the time of the broadcast.
     * @see java.util.Date#getTime()
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the time of the broadcast. The time is represented by the number of milliseconds since January 1, 1970,
     * 00:00:00 GMT.
     * 
     * @param time the time of the broadcast.
     * @see java.util.Date#getTime()
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets the type of the broadcast.
     * 
     * @return the type of the broadcast.
     */
    public int getType() {
        return ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC;
    }

    /**
     * Gets the message text of the broadcast.
     * 
     * @return the message text of the broadcast.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message text of the broadcast.
     * 
     * @param message the message text of the broadcast.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    private long time;

    public boolean equals(Object r) {
        if (r != null && r instanceof AdminBroadcast) {
            AdminBroadcast rhs = (AdminBroadcast) r;
            return rhs.getType() == getType() && rhs.getTime() == getTime() && rhs.getMessage().equals(getMessage());
        }
        return false;
    }

    public int hashCode() {
        return (int) (getTime() % (long) Integer.MAX_VALUE);
    }

    /**
     * Creates a new instance of <code>AdminBroadcast</code>. It is required by custom serialization.
     */
    public AdminBroadcast() {
    }

    /**
     * Creates a new instance of <code>AdminBroadcast</code>. The time and the message text are given.
     * 
     * @param time the time of the broadcast.
     * @param message the message text of the broadcast.
     */
    public AdminBroadcast(long time, String message) {
        setTime(time);
        setMessage(message);
    }

    public String toString() {
        return "generic_broadcast(" + time + ", " + message + ")";
    }
}
