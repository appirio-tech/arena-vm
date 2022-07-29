package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to remove the assignment of a component from the team member. The current user must be a team
 * leader.<br>
 * Use: This request is sent by client when the current user (must be a team leader) unassigns a problem component to a
 * team member.<br>
 * Note: This request is only available to team rounds. It is not supported now.
 * 
 * @author Hao Kung
 * @version $Id: UnassignComponentRequest.java 72300 2008-08-13 08:33:29Z qliu $
 * @deprecated Use {@link AssignComponentsRequest} instead.
 */
public class UnassignComponentRequest extends BaseRequest {
    /** Represents the ID of the problem component to be unassigned. */
    protected int m_componentID;

    /**
     * Creates a new instance of <code>UnassignComponentRequest</code>. It is required by custom serialization.
     */
    public UnassignComponentRequest() {
    }

    /**
     * Creates a new instance of <code>UnassignComponentRequest</code>.
     * 
     * @param cid the ID of the problem component to be unassigned.
     */
    public UnassignComponentRequest(int cid) {
        m_componentID = cid;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_componentID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        m_componentID = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.UNASSIGN_COMPONENT;
    }

    /**
     * Gets the ID of the problem component to be unassigned.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return m_componentID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.UnassignComponentRequest) [");
        ret.append("m_componentID = ");
        ret.append(m_componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
