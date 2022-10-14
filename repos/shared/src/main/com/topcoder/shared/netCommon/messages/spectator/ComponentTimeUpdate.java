/**
 * Copyright (C) 2006 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The ComponentTimeUpdate should be sent during every request to either RoundInfo or ComponentUpdate so that the
 * scoreboard can make sure its clock is in sync with the server..
 *
 * @author brain_cn
 * @version 1.0
 */
public class ComponentTimeUpdate extends Message implements Serializable, Cloneable, CustomSerializable {
    /**
     * the component identifier for the request
     */
    private long componentID;

    /**
     * the seconds before the start of the appeals phase, or 0 if it has started
     */
    private long appealsStartTime;

    /**
     * the seconds before the end of the appeals phase, or 0 if it has ended
     */
    private long appealsEndTime;

    /**
     * No-arg constructor required by custom serialization
     */
    public ComponentTimeUpdate() {
    }

    /**
     * Create a request for an update of a ComponentTime
     *
     * @param componentID      the component id
     * @param appealsStartTime the appealsStartTime to the update
     * @param appealsStartTime the appealsStartTime to the update
     */
    public ComponentTimeUpdate(long componentID, long appealsStartTime, long appealsEndTime) {
        this.componentID = componentID;
        this.appealsStartTime = appealsStartTime;
        this.appealsEndTime = appealsEndTime;
    }

    /**
     * Create a request for an update of a ComponentTime
     *
     * @param componentID      the component id
     * @param appealsStartTime the appealsStartTime to the update
     * @param appealsStartTime the appealsStartTime to the update
     */
    public ComponentTimeUpdate(long componentID, Timestamp appealsStartTime, Timestamp appealsEndTime) {
        this.componentID = componentID;
        this.appealsStartTime = getDifference(appealsStartTime);
        this.appealsEndTime = getDifference(appealsEndTime);
    }

    /**
     * Return the seconds before the given time.
     *
     * @param time the given time
     * @return the seconds before the given time
     */
    private long getDifference(Timestamp time) {
        long dif = (time.getTime() - System.currentTimeMillis()) / 1000;
        if (dif < 0) {
            return 0;
        } else {
            return dif;
        }
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(componentID);
        writer.writeLong(appealsStartTime);
        writer.writeLong(appealsEndTime);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        componentID = reader.readLong();
        appealsStartTime = reader.readLong();
        appealsEndTime = reader.readLong();
    }

    public String toString() {
        return "(ComponentTimeUpdate)[" + componentID + ", " + appealsStartTime + ", " + appealsEndTime + "]";
    }

    /**
     * @return Returns the appealsEndTime.
     */
    public long getAppealsEndTime() {
        return appealsEndTime;
    }

    /**
     * @param appealsEndTime The appealsEndTime to set.
     */
    public void setAppealsEndTime(long appealsEndTime) {
        this.appealsEndTime = appealsEndTime;
    }

    /**
     * @return Returns the appealsStartTime.
     */
    public long getAppealsStartTime() {
        return appealsStartTime;
    }

    /**
     * @param appealsStartTime The appealsStartTime to set.
     */
    public void setAppealsStartTime(long appealsStartTime) {
        this.appealsStartTime = appealsStartTime;
    }

    /**
     * @return Returns the componentID.
     */
    public long getComponentID() {
        return componentID;
    }

    /**
     * @param componentID The componentID to set.
     */
    public void setComponentID(long componentID) {
		this.componentID = componentID;
	}
}

