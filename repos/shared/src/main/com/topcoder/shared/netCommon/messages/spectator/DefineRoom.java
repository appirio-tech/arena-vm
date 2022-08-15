/**
 * DefineRoom.java
 *
 * Description:		Specifies the coders and problems assigned to a room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

public class DefineRoom extends RoomMessage {

    /** Coders assigned to the room */
    private ArrayList coders;

    /** Problems assigned to the room */
    private ArrayList problems;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public DefineRoom() {
    }

    /**
     * Constructs a define room request
     *
     * @param room     the room to define
     * @param coders   the coders assigned to the room
     * @param problems the problems assigned to the room
     *
     * @see com.topcoder.netCommon.spectatorMessages.RoomData
     * @see com.topcoder.netCommon.spectatorMessages.CoderRoomData
     * @see com.topcoder.netCommon.spectatorMessages.ProblemData
     */
    public DefineRoom(RoomData room, List coders, List problems) {
        super(room);
        this.coders = new ArrayList(coders);
        this.problems = new ArrayList(problems);
    }

    /**
     * Returns a list of CoderRoomData objects representing the coders assigned to the room and their seed position
     *
     * @returns a list of coder objects (non-mutable)
     *
     * @see com.topcoder.netCommon.spectatorMessages.CoderRoomData
     * @see java.util.List
     */
    public List getAssignedCoders() {
        return Collections.unmodifiableList(coders);
    }

    /**
     * Returns a list of Problem objects representing the coders assigned to the room
     *
     * @returns a list of problem objects (non-mutable)
     *
     * @see com.topcoder.netCommon.spectatorMessages.ProblemData
     * @see java.util.List
     */
    public List getAssignedProblems() {
        return Collections.unmodifiableList(problems);
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(coders);
        writer.writeArrayList(problems);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        coders = reader.readArrayList();
        problems = reader.readArrayList();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(DefineRoom)[").append(getRoom()).append(", ").append(coders).append(", ").append(problems).append("]").toString();
    }
}


/* @(#)DefineRoom.java */
