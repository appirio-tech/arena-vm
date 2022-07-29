package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contestantMessages.response.UnassignComponentResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the removal of a team problem component assignment used in <code>UnassignComponentResponse</code>. It
 * removes the assignment of a single team problem component.<br>
 * Note: It is not used any more.
 * 
 * @author Matthew P. Suhocki
 * @version $Id: UnassignComponentInfo.java 72424 2008-08-20 08:06:01Z qliu $
 * @see UnassignComponentResponse
 * @deprecated Use {@link ComponentAssignmentData} instead.
 */
public class UnassignComponentInfo implements CustomSerializable, Serializable {
    /** Represents the ID of the problem. */
    private int problemID;

    /** Represents the ID of the problem component to be unassigned. */
    private int componentID;

    /**
     * Creates a new instance of <code>UnassignComponentInfo</code>. It is required by custom serialization.
     */
    public UnassignComponentInfo() {
    }

    /**
     * Creates a new instance of <code>UnassignComponentInfo</code>.
     * 
     * @param pid the ID of the problem.
     * @param cid the ID of the problem component to be unassigned.
     */
    public UnassignComponentInfo(int pid, int cid) {
        setProblemID(pid);
        setComponentID(cid);
    }

    /**
     * Creates a new instance of <code>UnassignComponentInfo</code>. The problem ID is set as 0.
     * 
     * @param cid the ID of the problem component to be unassigned.
     */
    public UnassignComponentInfo(int cid) {
        this(0, cid);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(problemID);
        csWriter.writeInt(componentID);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        problemID = csReader.readInt();
        componentID = csReader.readInt();
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
     * Gets the ID of the problem component to be unassigned.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
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
     * Sets the ID of the problem component to be unassigned.
     * 
     * @param cid the problem component ID.
     */
    public void setComponentID(int cid) {
        componentID = cid;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.UnassignComponentInfo) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}