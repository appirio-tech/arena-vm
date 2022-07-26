/**
 * DefineWeakestLinkTeam.java
 *
 * Description:         Defines a team and team members in a weakest link round
 * @author              Dave Pecora
 * @version             1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class DefineWeakestLinkTeam extends Message implements java.io.Serializable {

    /** The team ID */
    private int teamID;

    /** The team name */
    private String teamName;

    /** The coder ID's of the team members */
    private int coderIDs[];

    /** The round ID */
    private int roundID;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public DefineWeakestLinkTeam() {
    }

    /**
     * Constructs a define weakest link teams request
     *
     * @param team ID The team ID
     * @param teamName The team name
     * @param coderIDs The ID's of the coders on the team
     * @param roundID The round ID
     */
    public DefineWeakestLinkTeam(int teamID, String teamName, int coderIDs[], int roundID) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.coderIDs = coderIDs;
        this.roundID = roundID;
    }

    /**
     * Returns the team ID
     * @return the team ID
     */
    public int getTeamID() {
        return teamID;
    }

    /**
     * Returns the team name
     * @return the team name
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Returns the coder ID's of team members
     * @return the coder ID's of team members
     */
    public int[] getCoderIDs() {
        return coderIDs;
    }

    /**
     * Returns the round ID
     * @return the round ID
     */
    public int getRoundID() {
        return roundID;
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
        writer.writeString(teamName);
        writer.writeObject(coderIDs);
        writer.writeInt(roundID);
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
        teamName = reader.readString();
        coderIDs = (int[]) reader.readObject();
        roundID = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("(DefineWeakestLinkTeam)[").append(teamID).append(", ").append(teamName).append(", ");
        if (coderIDs == null) {
            sb.append("***THIS IS A PROBLEM***:  no coder ID's on team (coderIDs is null)");
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
        sb.append(", ").append(roundID).append("]");
        return sb.toString();
    }
}

