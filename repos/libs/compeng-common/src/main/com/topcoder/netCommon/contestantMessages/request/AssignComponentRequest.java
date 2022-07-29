package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

import java.io.IOException;

/**
 * Defines a request to assign a component to a team member. The current user must be a team leader.<br>
 * Use: This request is sent by client when the current user (must be a team leader) assigns a problem component to a
 * team member.<br>
 * Note: This request is only available to team rounds. It is not supported now.
 * 
 * @author Hao Kung
 * @version $Id: AssignComponentRequest.java 72300 2008-08-13 08:33:29Z qliu $
 * @deprecated Use {@link AssignComponentsRequest} instead.
 */
public class AssignComponentRequest extends BaseRequest {
    /** Problem ID */
    protected int m_problemID;

    /** ProblemComponent ID */
    protected int m_componentID;

    /** Handle of user assigned to component */
    protected String m_userHandle;

    /**
     * Creates a new instance of <code>AssignComponentRequest</code>. It is required by custom serialization.
     */
    public AssignComponentRequest() {
    }

    /**
     * Constructor without problem id. The problem ID is set to 0.
     * 
     * @param cid component id
     * @param handle handle of assigned user
     */
    public AssignComponentRequest(int cid, String handle) {
        this(0, cid, handle);
    }

    /**
     * Creates a new instance of <code>AssignComponentRequest</code>.
     * 
     * @param pid problem id
     * @param cid component id
     * @param handle handle of assigned user
     */
    public AssignComponentRequest(int pid, int cid, String handle) {
        m_problemID = pid;
        m_componentID = cid;
        m_userHandle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_problemID);
        writer.writeInt(m_componentID);
        writer.writeString(m_userHandle);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        m_problemID = reader.readInt();
        m_componentID = reader.readInt();
        m_userHandle = reader.readString();
    }

    public int getRequestType() {
        return ContestConstants.ASSIGN_COMPONENT;
    }

    /**
     * Gets the problem ID of the assignment.
     * 
     * @return the problem ID
     */
    public int getProblemID() {
        return m_problemID;
    }

    /**
     * Gets the component ID of the assignment.
     * 
     * @return the component ID
     */
    public int getComponentID() {
        return m_componentID;
    }

    /**
     * Gets the handle of the assigned user.
     * 
     * @return the handle of the user assigned to the component
     */
    public String getUserHandle() {
        return m_userHandle;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.AssignComponentRequest) [");
        ret.append("m_problemID = ");
        ret.append(m_problemID);
        ret.append(", ");
        ret.append("m_componentID = ");
        ret.append(m_componentID);
        ret.append(", ");
        ret.append("m_userHandle = ");
        if (m_userHandle == null) {
            ret.append("null");
        } else {
            ret.append(m_userHandle.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
