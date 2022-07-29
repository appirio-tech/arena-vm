/*
 * StartSyncResponse Created 03/23/2006
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * This message indicates the start of a message sequence of messages belonging to the response of the synchronous
 * request with Id equal to <code>requestId</code> This response message indicates that all the following messages
 * until an EndSyncResponse message belongs to the response of the request with the requestId.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: StartSyncResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class StartSyncResponse extends BaseResponse {
    /**
     * The id of the request to which this response belongs
     */
    private int requestId;

    /**
     * Creates a new instance of <code>StartSyncResponse</code>. It is required by custom serialization.
     */
    public StartSyncResponse() {
    }

    /**
     * Creates a new instance of <code>StartSyncResponse</code>.
     * 
     * @param requestId the ID of the request which this response belongs to.
     */
    public StartSyncResponse(int requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the ID of the request which this response belongs to.
     * 
     * @return the ID of the request which this response belongs to.
     */
    public int getRequestId() {
        return requestId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(requestId);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        requestId = reader.readInt();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(200);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.StartSyncResponse) [");
        ret.append("requestId = ");
        ret.append(requestId);
        ret.append("]");
        return ret.toString();
    }
}
