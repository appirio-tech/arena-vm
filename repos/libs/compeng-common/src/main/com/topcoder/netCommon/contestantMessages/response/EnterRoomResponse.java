package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.RoomInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the client information of the room when entering.<br>
 * Note: This response is no longer used.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: EnterRoomResponse.java 72300 2008-08-13 08:33:29Z qliu $
 * @deprecated It is not used.
 */
public class EnterRoomResponse extends BaseResponse {
    /** Represents the information of the room to be entered. */
    RoomInfo data;

    /**
     * Creates a new instance of <code>EnterRoomResponse</code>. It is required by custom serialization.
     */
    public EnterRoomResponse() {
    }

    /**
     * Creates a new instance of <code>EnterRoomResponse</code>.
     * 
     * @param data the information of the room to be entered.
     */
    public EnterRoomResponse(RoomInfo data) {
        this.data = data;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(data);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        data = (RoomInfo) reader.readObject();
    }

    /**
     * Gets the information of the room to be entered.
     * 
     * @return the information of the room to be entered.
     */
    public RoomInfo getRoomInfo() {
        return data;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.EnterRoomResponse) [");
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
