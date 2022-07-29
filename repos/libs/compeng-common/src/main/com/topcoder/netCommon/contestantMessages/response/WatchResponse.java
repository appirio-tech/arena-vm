package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client that the subscription to the updates of a room is successful.<br>
 * Use: This response is specific to <code>WatchRequest</code>. It is an acknowledgement of the subscription. The
 * client does not have to do anything.
 * 
 * @author Lars Backstrom
 * @version $Id: WatchResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class WatchResponse extends WatchableResponse {
    /**
     * Creates a new instance of <code>WatchResponse</code>. It is required by custom serialization.
     */
    public WatchResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>WatchResponse</code>.
     * 
     * @param roomType the type of the room.
     * @param roomIndex the ID of the room.
     */
    public WatchResponse(int roomType, int roomIndex) {
        super(roomType, roomIndex);

    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.WatchResponse) [");
        ret.append("]");
        return ret.toString();
    }
}
