/*
 * AssignComponentResponse.java Created on June 28, 2002, 2:46 AM
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.AssignComponentInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify one team problem component assignment update to team members.<br>
 * Use: When the team leader assigns a team problem component by <code>AssignComponentRequest</code>, this response
 * will be sent to all team members.<br>
 * Note: It is not supported now.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: AssignComponentResponse.java 72300 2008-08-13 08:33:29Z qliu $
 * @see AssignComponentRequest
 * @deprecated Use {@link AssignComponentsResponse} instead.
 */
public class AssignComponentResponse extends BaseResponse {
    /** Represents the problem component assignment updated. */
    protected AssignComponentInfo info = null;

    /**
     * Creates a new instance of <code>AssignComponentResponse</code>. It is required by custom serialization.
     */
    public AssignComponentResponse() {
    }

    /**
     * Creates a new instance of <code>AssignComponentResponse</code>.
     * 
     * @param info the updated problem component assignment.
     */
    public AssignComponentResponse(AssignComponentInfo info) {
        super();
        this.info = info;
    }

    /**
     * Gets the updated problem component assignment.
     * 
     * @return the updated problem component assignment.
     */
    public AssignComponentInfo getAssignComponentInfo() {
        return info;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(info);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        info = (AssignComponentInfo) reader.readObject();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.AssignComponentResponse) [");
        ret.append("info = ");
        if (info == null) {
            ret.append("null");
        } else {
            ret.append(info.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
