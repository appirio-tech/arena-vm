/**
 * CoderRoomData.java
 *
 * Description:		Structure representing a coder within a room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

//import com.topcoder.netCommon.*;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public class CoderRoomData extends CoderData {

    /** The seed within a room */
    private int seed;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public CoderRoomData() {
    }

    /**
     * Constructor
     *
     * @param handle the handle of the coder
     * @param rank   the rank of the coder
     * @param seed   the seed of the coder
     */
    public CoderRoomData(int coderID, String handle, int rank, int seed) {
        super(coderID, handle, rank);
        this.seed = seed;
    }

    /**
     * Gets the room seed
     *
     * @returns the room seed
     */
    public int getSeed() {
        return seed;
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
        writer.writeInt(seed);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        seed = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return "(CoderRoomData)[" + getCoderID() + ", " + getHandle() + ", " + getRank() + ", " + seed + "]";
    }
}

/* @(#)CoderRoomData.java */
