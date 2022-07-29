/**
 * ShowWeakestLinkTeam.java
 *
 * Description:         Tells the scoreboard to show the status of a given team
 * @author              Dave Pecora
 * @version             1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ShowWeakestLinkTeam extends Message implements java.io.Serializable {

    /** The team ID to show */
    private int teamID;

    /** The coder ID's to show */
    private int coderIDs[];

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ShowWeakestLinkTeam() {
    }

    /**
     * Constructs a show team request
     *
     * @param teamID  The ID of the team to show
     * @param coderIDs  The ID's of the coders to show
     */
    public ShowWeakestLinkTeam(int teamID, int coderIDs[]) {
        super();
        this.teamID = teamID;
        this.coderIDs = coderIDs;
    }

    /**
     * Returns the team ID
     * @return the team ID
     */
    public int getTeamID() {
        return teamID;
    }

    /**
     * Gets the coder ID's to display
     *
     * @returns the coder ID's
     */
    public int[] getCoderIDs() {
        return coderIDs;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(teamID);
        writer.writeObject(coderIDs);
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
        teamID = reader.readInt();
        coderIDs = (int[]) reader.readObject();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("(ShowWeakestLinkTeam)[").append(teamID).append(", ");
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

