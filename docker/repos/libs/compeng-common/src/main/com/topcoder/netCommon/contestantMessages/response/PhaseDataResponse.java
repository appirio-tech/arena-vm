package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the client about a phase update of a round.<br>
 * Use: This response may be sent at any time from the server, since the timer on the server may be different from the
 * timer on the client. When receiving this event, the current phase of the round should be immediately changed to the
 * phase in this response, even if the phases are the same.<br>
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: PhaseDataResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class PhaseDataResponse extends BaseResponse {
    /** Represents the current phase to be updated. */
    PhaseData data;

    /**
     * Creates a new instance of <code>PhaseDataResponse</code>. It is required by custom serialization.
     */
    public PhaseDataResponse() {
    }

    /**
     * Creates a new instance of <code>PhaseDataResponse</code>.
     * 
     * @param data the current phase to be updated.
     */
    public PhaseDataResponse(PhaseData data) {
        this.data = data;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(data);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        data = (PhaseData) reader.readObject();
    }

    /**
     * Gets the current phase to be updated.
     * 
     * @return the current phase.
     */
    public PhaseData getPhaseData() {
        return data;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse) [");
        ret.append("data = ");
        if (data == null) {
            ret.append("null");
        } else {
            ret.append(data.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
