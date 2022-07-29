package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the client the current team problem component assignment.<br>
 * Use: When this response is received by the client, the client should replace all previous assignments to the
 * assignment in this response. This response will be received when moving to a team contest room, or reconnecting to
 * the server when the current user was in a team contest room.<br>
 * Note: This response will be sent to team members assigned to the team contest room only. It is <b>not</b> a response
 * specific to <code>AssignComponentsRequest</code>.
 * 
 * @author Qi Liu
 * @version $Id: ComponentAssignmentDataResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class ComponentAssignmentDataResponse extends BaseResponse {
    /** Represents the problem component assignment to be processed. */
    private ComponentAssignmentData data;

    /**
     * Creates a new instance of <code>ComponentAssignmentDataResponse</code>. It is required by custom
     * serialization.
     */
    public ComponentAssignmentDataResponse() {
        this(null);
    }

    /**
     * Creates a new instance of <code>ComponentAssignmentDataResponse</code>.
     * 
     * @param data the problem component assignment.
     */
    public ComponentAssignmentDataResponse(ComponentAssignmentData data) {
        this.data = data;
    }

    /**
     * Gets the problem component assignment which replaces all previous assignments.
     * 
     * @return the problem component assignment.
     */
    public ComponentAssignmentData getData() {
        return data;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(data);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        data = (ComponentAssignmentData) reader.readObject();
    }

    public String toString() {
        return "ComponentAssignmentDataResponse[data=" + data.toString() + "]";
    }
}
