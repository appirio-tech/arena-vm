package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to keep the connection alive. There is no business logic payload.<br>
 * Use: This response is specific to <code>KeepAliveRequest</code>. It is used to avoid timing out. The client
 * processor can safely ignore the response.
 * 
 * @author Lars Backstrom
 * @author Diego Belfer (mural)
 * @version $Id: KeepAliveResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class KeepAliveResponse extends UnsynchronizeResponse {
    /**
     * Creates a new instance of <code>KeepAliveResponse</code>. It is required by custom serialization.
     */
    public KeepAliveResponse() {
    }

    /**
     * Creates a new instance of <code>KeepAliveResponse</code>.
     * 
     * @param requestId the ID of the corresponding request.
     */
    public KeepAliveResponse(int requestId) {
        super(requestId);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.KeepAliveResponse) [id = ");
        ret.append(getID());
        ret.append("]");
        return ret.toString();
    }
}
