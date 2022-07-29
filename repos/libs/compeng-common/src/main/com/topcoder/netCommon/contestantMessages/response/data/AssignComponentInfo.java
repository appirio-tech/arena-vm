package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contestantMessages.response.AssignComponentResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a team problem component assignment used in <code>AssignComponentResponse</code>. It assigns a single team
 * problem component to a user in the team.<br>
 * Note: It is not used any more.
 * 
 * @author Matthew P. Suhocki
 * @version $Id: AssignComponentInfo.java 72424 2008-08-20 08:06:01Z qliu $
 * @see AssignComponentResponse
 * @deprecated Use {@link ComponentAssignmentData} instead.
 */
public class AssignComponentInfo implements CustomSerializable, Serializable {
    /** Represents the ID of the problem. */
    private int problemID;

    /** Represents the ID of the problem component to be assigned. */
    private int componentID;

    /** Represents the handle of the user assigned to the problem component. */
    private String userName;

    /** Represents the rating of the user. */
    private int userRank;

    /**
     * Creates a new instance of <code>AssignComponentInfo</code>. It is required by custom serialization.
     */
    public AssignComponentInfo() {
    }

    /**
     * Creates a new instance of <code>AssignComponentInfo</code>.
     * 
     * @param pid the ID of the problem.
     * @param cid the ID of the problem component to be assigned.
     * @param user the handle of the user assigned to the problem component.
     * @param rank the rating of the user assigned to the problem component.
     */
    public AssignComponentInfo(int pid, int cid, String user, int rank) {
        setProblemID(pid);
        setComponentID(cid);
        setUserName(user);
        setUserRank(rank);
    }

    /**
     * Creates a new instance of <code>AssignComponentInfo</code>. The problem ID is set as 0.
     * 
     * @param cid the ID of the problem component to be assigned.
     * @param user the handle of the user assigned to the problem component.
     * @param rank the rating of the user assigned to the problem component.
     */
    public AssignComponentInfo(int cid, String user, int rank) {
        this(0, cid, user, rank);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(problemID);
        csWriter.writeInt(componentID);
        csWriter.writeString(userName);
        csWriter.writeInt(userRank);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        problemID = csReader.readInt();
        componentID = csReader.readInt();
        userName = csReader.readString();
        userRank = csReader.readInt();
    }

    /**
     * Gets the ID of the problem.
     * 
     * @return the problem ID.
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * Gets the ID of the problem component to be assigned.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the handle of the user assigned to the problem component.
     * 
     * @return the handle of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the rating of the user assigned to the problem component.
     * 
     * @return the rating of the user.
     */
    public int getUserRank() {
        return userRank;
    }

    /**
     * Sets the ID of the problem.
     * 
     * @param pid the problem ID.
     */
    public void setProblemID(int pid) {
        problemID = pid;
    }

    /**
     * Sets the ID of the problem component to be assigned.
     * 
     * @param cid the problem component ID.
     */
    public void setComponentID(int cid) {
        componentID = cid;
    }

    /**
     * Sets the handle of the user assigned to the problem component.
     * 
     * @param name the handle of the user.
     */
    public void setUserName(String name) {
        userName = name;
    }

    /**
     * Sets the rating of the user assigned to the problem component.
     * 
     * @param rank the rating of the user.
     */
    public void setUserRank(int rank) {
        userRank = rank;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.AssignComponentInfo) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("userName = ");
        if (userName == null) {
            ret.append("null");
        } else {
            ret.append(userName.toString());
        }
        ret.append(", ");
        ret.append("userRank = ");
        ret.append(userRank);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}