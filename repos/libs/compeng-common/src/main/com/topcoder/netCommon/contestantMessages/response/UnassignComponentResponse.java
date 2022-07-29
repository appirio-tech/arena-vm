/*
 * UnassignComponentResponse.java Created on June 28, 2002, 2:46 AM
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.request.UnassignComponentRequest;
import com.topcoder.netCommon.contestantMessages.response.data.UnassignComponentInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify one team problem component assignment update to team members.<br>
 * Use: When the team leader removes an assignment of a team problem component by <code>UnassignComponentRequest</code>,
 * this response will be sent to all team members.<br>
 * Note: It is not supported now.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: UnassignComponentResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see UnassignComponentRequest
 * @deprecated Use {@link AssignComponentsResponse} instead.
 */
public class UnassignComponentResponse extends BaseResponse {
    /** Represents the problem component assignment updated. */
    protected UnassignComponentInfo info = null;

    /**
     * Creates a new instance of <code>UnassignComponentResponse</code>. It is required by custom serialization.
     */
    public UnassignComponentResponse() {
    }

    /**
     * Creates a new instance of <code>UnassignComponentResponse</code>.
     * 
     * @param info the information about the unassigned problem component.
     */
    public UnassignComponentResponse(UnassignComponentInfo info) {
        this.info = info;
    }

    /**
     * Gets the information about the unassigned problem component.
     * 
     * @return the unassigned problem component information.
     */
    public UnassignComponentInfo getUnassignComponentInfo() {
        return info;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(info);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        info = (UnassignComponentInfo) reader.readObject();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UnassignComponentResponse) [");
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
