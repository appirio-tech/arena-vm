/**
 * CoderData.java
 *
 * Description:     Structure representing a coder
 * @author          Tim "Pops" Roberts
 * @version         1.01
 */

package com.topcoder.shared.netCommon.messages.spectator;

//import com.topcoder.netCommon.*;

import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.netCommon.messages.Message;

import java.io.*;

public class CoderData extends Message implements Serializable, Cloneable, CustomSerializable  {

    /** Coder ID */
    private int coderID;
    /** Handle of the coder */
    private String handle;
    /** Rank of the coder */
    private int rank;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public CoderData() {
    }

    /**
     * Constructor
     *
     * @param coderID the coder's ID
     * @param handle the handle of the coder
     * @param rank   the rank of the coder
     */
    public CoderData(int coderID, String handle, int rank) {
        this.coderID = coderID;
        this.handle = handle;
        this.rank = rank;
    }

    /**
     * Gets the coder ID
     *
     * @returns the coder ID
     */
    public int getCoderID() {
        return coderID;
    }

    /**
     * Gets the handle
     *
     * @returns the user handle
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the rank
     *
     * @returns the rank of the user
     */
    public int getRank() {
        return rank;
    }


    public void setCoderID(int coderID) {
		this.coderID = coderID;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public void setRank(int rank) {
		this.rank = rank;
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
        writer.writeInt(coderID);
        writer.writeString(handle);
        writer.writeInt(rank);
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
        coderID = reader.readInt();
        handle = reader.readString();
        rank = reader.readInt();
    }

   public boolean equals(Object obj) {
   	if (obj == null || !(obj instanceof CoderData)) return false;
   	return ((CoderData) obj).getCoderID() == coderID;
   }
    
    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return "(CoderData)[" + coderID + ", " + handle + ", " + rank + "]";
    }
}


/* @(#)CoderData.java */
