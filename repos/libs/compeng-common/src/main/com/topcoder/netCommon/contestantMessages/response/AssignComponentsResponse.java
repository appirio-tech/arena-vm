/**
 * AssignComponentsResponse Description: Lists the components a coder has been assigned to
 * 
 * @author Tim Bulat
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify all team problem component assignment updates to team members.<br>
 * Use: When the team leader changes the problem component assignment by <code>AssignComponentsRequest</code>, all
 * team members in the room will receive this response.<br>
 * Note: The assignment in this response should replace all previous assignments. The response only contains assigned
 * problem components to the current user of the receiver.
 * 
 * @author Tim Bulat
 * @version $Id: AssignComponentsResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class AssignComponentsResponse extends BaseResponse {
    /** Represents the ID of the division in the round of the assignment. */
    private int divisionID;

    /** Represents the ID of the round of the assignment. */
    private long roundID;

    /** Represents the assigned problem components to the current user. */
    private ComponentLabel assignedComponents[];

    /**
     * Creates a new instance of <code>AssignComponentsResponse</code>. It is required by custom serialization.
     */
    public AssignComponentsResponse() {
    }

    /**
     * Creates a new instance of <code>AssignComponentsResponse</code>.
     * 
     * @param assignedComponents the assigned problem components to the current user of the receiver.
     * @param roundID the ID of the round of the assignment.
     * @param divisionID the ID of the division in the round.
     */
    public AssignComponentsResponse(ComponentLabel[] assignedComponents, long roundID, int divisionID) {
        this.roundID = roundID;
        this.assignedComponents = assignedComponents;
        this.divisionID = divisionID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(assignedComponents);
        writer.writeLong(roundID);
        writer.writeInt(divisionID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        assignedComponents = (ComponentLabel[]) reader.readObjectArray(ComponentLabel.class);
        roundID = reader.readLong();
        divisionID = reader.readInt();
    }

    /**
     * Gets the assigned problem components to the current user of the receiver.
     * 
     * @return the assigned problem components to the current user.
     */
    public ComponentLabel[] getAssignedComponents() {
        return assignedComponents;
    }

    /**
     * Gets the ID of the round of the assignment.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets the ID of the division in the round of the assignment.
     * 
     * @return the division ID of the round.
     */
    public int getDivisionID() {
        return divisionID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.AssignComponentsResponse) [");
        ret.append("components = ");
        if (assignedComponents == null) {
            ret.append("null");
        } else {
            ret.append(Arrays.asList(assignedComponents).toString());
        }
        ret.append(", ");
        ret.append("roundID = " + roundID);
        ret.append(", ");
        ret.append("divisionID = " + divisionID);
        ret.append("]");
        return ret.toString();
    }
}
