package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to assign several components to one or more team members. The current user must be a team leader.<br>
 * Use: This request is sent by client when the current user (must be a team leader) assigns several problem components
 * to a group of team members.<br>
 * Note: This request is only available to team rounds. The current assignment will be replaced by this new assignment.
 * 
 * @author Hao Kung
 * @version $Id: AssignComponentsRequest.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class AssignComponentsRequest extends BaseRequest {
    /** Represents the problem component assignment to be processed. */
    protected ComponentAssignmentData data;

    /**
     * Creates a new instance of <code>AssignComponentsRequest</code>. It is required by custom serialization.
     */
    public AssignComponentsRequest() {
    }

    /**
     * Creates a new instance of <code>AssignComponentsRequest</code>.
     * 
     * @param data the problem component assignment to be processed.
     */
    public AssignComponentsRequest(ComponentAssignmentData data) {
        this.data = data;
    }

    public int getRequestType() {
        return ContestConstants.ASSIGN_COMPONENTS;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        data = (ComponentAssignmentData) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(data);
    }

    /**
     * Gets the problem component assignment.
     * 
     * @return the problem component assignment.
     */
    public ComponentAssignmentData getData() {
        return data;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.AssignComponentsRequest) [");
        ret.append("data = ");
        ret.append(data);
        ret.append("]");
        return ret.toString();
    }
}
