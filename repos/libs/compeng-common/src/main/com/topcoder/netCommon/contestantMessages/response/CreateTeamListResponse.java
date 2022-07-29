package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of all teams.<br>
 * Use: This response is used to establish the initial list of all teams. Any previous list of all teams should be
 * replaced by the data in this response.<br>
 * Note: This response is usually the first response to a subscription request. Subsequent update responses are
 * modifications to the list provided by this response.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: CreateTeamListResponse.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class CreateTeamListResponse extends BaseResponse {
    /** Represents the list of teams. */
    private TeamListInfo[] info = null;

    /**
     * Creates a new instance of <code>CreateTeamListResponse</code>. It is required by custom serialization.
     */
    public CreateTeamListResponse() {
    }

    /**
     * Creates a new instance of <code>CreateTeamListResponse</code>. There is no copy.
     * 
     * @param info the list of teams.
     */
    public CreateTeamListResponse(TeamListInfo[] info) {
        this.info = info;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(info);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        info = (TeamListInfo[]) reader.readObjectArray(TeamListInfo.class);
    }

    /**
     * Gets the list of teams. There is no copy.
     * 
     * @return the list of teams.
     */
    public TeamListInfo[] getTeamListInfo() {
        return info;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateTeamListResponse) [");
        ret.append("info = ");
        if (info == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < info.length; i++) {
                ret.append(info[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
