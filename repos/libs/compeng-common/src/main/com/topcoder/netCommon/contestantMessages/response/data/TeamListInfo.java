/*
 * TeamListInfo.java Created on June 27, 2002, 11:44 PM
 */

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contestantMessages.response.CreateTeamListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateTeamListResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a team in a team list.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: TeamListInfo.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateTeamListResponse
 * @see UpdateTeamListResponse
 */
public class TeamListInfo implements CustomSerializable, Serializable {
    /** Represents the name of the team. */
    private String teamName;

    /** Represents the rating of the team. */
    private int teamRank;

    /** Represents the handle of the team leader. */
    private String captainName;

    /** Represents the rating of the team leader. */
    private int captainRank;

    /** Represents the number of available team member applicants. */
    private int available;

    /** Represents the number of current team members. */
    private int members;

    /** Represents the status description of the team. */
    private String status;

    /**
     * Creates a new instance of <code>TeamListRowInfo</code>. It is required by custom serialization.
     */
    public TeamListInfo() {
    }

    /**
     * Creates a new instance of <code>TeamListRowInfo</code>.
     * 
     * @param teamName the name of the team.
     * @param teamRank the rating of the team.
     * @param captainName the handle of the team leader.
     * @param captainRank the rating of the team leader.
     * @param available the number of available team member applicants.
     * @param members the number of current team members.
     * @param status the status description of the team.
     */
    public TeamListInfo(String teamName, int teamRank, String captainName, int captainRank, int available, int members,
        String status) {
        setTeamName(teamName);
        setTeamRank(teamRank);
        setCaptainName(captainName);
        setCaptainRank(captainRank);
        setAvailable(available);
        setMembers(members);
        setStatus(status);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeString(teamName);
        csWriter.writeInt(teamRank);
        csWriter.writeString(captainName);
        csWriter.writeInt(captainRank);
        csWriter.writeInt(available);
        csWriter.writeInt(members);
        csWriter.writeString(status);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        teamName = csReader.readString();
        teamRank = csReader.readInt();
        captainName = csReader.readString();
        captainRank = csReader.readInt();
        available = csReader.readInt();
        members = csReader.readInt();
        status = csReader.readString();
    }

    /**
     * Gets the name of the team.
     * 
     * @return the team name.
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the rating of the team.
     * 
     * @return the rating of the team.
     */
    public int getTeamRank() {
        return teamRank;
    }

    /**
     * Gets the handle of the team leader.
     * 
     * @return the handle of the team leader.
     */
    public String getCaptainName() {
        return captainName;
    }

    /**
     * Gets the rating of the team leader.
     * 
     * @return the rating of the team leader.
     */
    public int getCaptainRank() {
        return captainRank;
    }

    /**
     * Sets the number of available team member applicants.
     * 
     * @return the number of available team member applicants.
     */
    public int getAvailable() {
        return available;
    }

    /**
     * Gets the number of current team members.
     * 
     * @return the number of current team members.
     */
    public int getMembers() {
        return members;
    }

    /**
     * Gets the status description of the team.
     * 
     * @return the status description of the team.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the name of the team.
     * 
     * @param name the team name.
     */
    public void setTeamName(String name) {
        teamName = name;
    }

    /**
     * Sets the rating of the team.
     * 
     * @param rank the rating of the team.
     */
    public void setTeamRank(int rank) {
        teamRank = rank;
    }

    /**
     * Sets the handle of the team leader.
     * 
     * @param name the handle of the team leader.
     */
    public void setCaptainName(String name) {
        captainName = name;
    }

    /**
     * Sets the rating of the team leader.
     * 
     * @param rank the rating of the team leader.
     */
    public void setCaptainRank(int rank) {
        captainRank = rank;
    }

    /**
     * Sets the number of available team member applicants.
     * 
     * @param num the number of available team member applicants.
     */
    public void setAvailable(int num) {
        available = num;
    }

    /**
     * Sets the number of current team members.
     * 
     * @param num the number of current team members.
     */
    public void setMembers(int num) {
        members = num;
    }

    /**
     * Sets the status description of the team.
     * 
     * @param status the status description of the team.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo) [");
        ret.append("teamName = ");
        if (teamName == null) {
            ret.append("null");
        } else {
            ret.append(teamName.toString());
        }
        ret.append(", ");
        ret.append("teamRank = ");
        ret.append(teamRank);
        ret.append(", ");
        ret.append("captainName = ");
        if (captainName == null) {
            ret.append("null");
        } else {
            ret.append(captainName.toString());
        }
        ret.append(", ");
        ret.append("captainRank = ");
        ret.append(captainRank);
        ret.append(", ");
        ret.append("available = ");
        ret.append(available);
        ret.append(", ");
        ret.append("members = ");
        ret.append(members);
        ret.append(", ");
        ret.append("status = ");
        if (status == null) {
            ret.append("null");
        } else {
            ret.append(status.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
