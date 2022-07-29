package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the status of a lobby chat room or a moderated chat room.<br>
 * Use: This response may be sent automatically by the server without any requests. The client should use the status in
 * this response to replace any previous status of the chat room with the same name. For example, when a lobby chat room
 * is full, the status will change from available to unavailable and this response will be sent.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateMenuResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see CreateMenuResponse
 */
public class UpdateMenuResponse extends BaseResponse {
    /** Represents the type of the chat room in the response. */
    private int type;

    /** Represents the name of the chat room. */
    private String element;

    /** Represents the status of the chat room to be updated. */
    private String status;

    /**
     * Creates a new instance of <code>UpdateMenuResponse</code>. It is required by custom serialization.
     */
    public UpdateMenuResponse() {
    }

    /**
     * Creates a new instance of <code>UpdateMenuResponse</code>. The status is a string of 'A' (available) or 'F'
     * (not available).
     * 
     * @param type the type of the chat room.
     * @param element the name of the chat room.
     * @param status the status of the chat room.
     * @see ContestConstants#LOBBY_MENU
     * @see ContestConstants#ACTIVE_CHAT_MENU
     */
    public UpdateMenuResponse(int type, String element, String status) {
        this.type = type;
        this.element = element;
        this.status = status;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeString(element);
        writer.writeString(status);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        element = reader.readString();
        status = reader.readString();
    }

    /**
     * Gets the type of the chat room in the response. It will only be lobby or moderated.
     * 
     * @return the type of the chat room.
     * @see ContestConstants#LOBBY_MENU
     * @see ContestConstants#ACTIVE_CHAT_MENU
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the name of the chat room.
     * 
     * @return the name of the chat room.
     */
    public String getElement() {
        return element;
    }

    /**
     * Gets the updated status of the chat room. It is a string of 'A' (available) or 'F' (not available).
     * 
     * @return the status of the chat room.
     */
    public String getStatus() {
        return status;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateMenuResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("element = ");
        if (element == null) {
            ret.append("null");
        } else {
            ret.append(element.toString());
        }
        ret.append(", ");
        ret.append("status = ");
        if (status == null) {
            ret.append("null");
        } else {
            ret.append(status.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}