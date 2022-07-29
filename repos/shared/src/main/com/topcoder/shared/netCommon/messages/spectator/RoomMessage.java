/**
 * RoomMessage.java
 *
 * Description:		Defines common behaviors of a room message
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

//import com.topcoder.netCommon.*;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

public abstract class RoomMessage extends Message {

    /** room information */
    private RoomData room;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public RoomMessage() {
    }

    /** Default Constructor */
    public RoomMessage(RoomData room) {
        super();
        this.room = room;
    }

    /**
     * Gets the room information
     *
     * @returns the room information
     * @see com.topcoder.netCommon.spectatorMessages.RoomData
     */
    public RoomData getRoom() {
        return room;
    }

    /** Default customWriteObject */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        room.customWriteObject(writer);
    }

    /** Default customReadObject */
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);

        room = new RoomData();
        room.customReadObject(reader);
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(RoomMessage)[").append(room.toString()).append("]").toString();
    }
}


/* @(#)RoomMessage.java */
