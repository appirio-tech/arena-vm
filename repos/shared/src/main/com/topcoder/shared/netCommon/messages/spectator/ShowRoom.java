/**
 * ShowRoom.java
 *
 * Description:		Notifies the spectator application to show a specific room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

// Note: MUST define serializable for the announcer app...

public class ShowRoom extends Message implements java.io.Serializable {

    /** room information */
    private RoomData room;

    /** The coder ID's to show */
    private int coderIDs[];

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ShowRoom() {
    }

    /** Default Constructor */
    public ShowRoom(RoomData room, int coderIDs[]) {
        super();
        this.room = room;
        this.coderIDs = coderIDs;
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

    /**
     * Gets the coder ID's to display
     *
     * @returns the coder ID's
     */
    public int[] getCoderIDs() {
        return coderIDs;
    }

    /** Default customWriteObject */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        room.customWriteObject(writer);
        writer.writeObject(coderIDs);
    }

    /** Default customReadObject */
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);

        room = new RoomData();
        room.customReadObject(reader);
        coderIDs = (int[]) reader.readObject();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("(ShowRoom)[").append(room).append(", ");
        if (coderIDs == null) {
            sb.append("***THIS IS A PROBLEM***:  no coder ID's specified to show (coderIDs is null)");
        } else {
            sb.append("(");
            for (int i = 0; i < coderIDs.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(coderIDs[i]);
            }
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }
}


/* @(#)ShowRoom */
