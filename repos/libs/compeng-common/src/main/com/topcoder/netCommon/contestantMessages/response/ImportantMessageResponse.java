package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send one unread important message to the client immediately after successful logging in.<br>
 * Use: This response may be a part of the responses of <code>LoginRequest</code>. When the current user successfully
 * logs in, all the unread important messages are sent one by one using this response. The client should make sure that
 * the current user see the important message. Once this response is sent by server, the corresponding important message
 * is considered as read.
 * 
 * @author Ryan Fairfax
 * @version $Id: ImportantMessageResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class ImportantMessageResponse extends BaseResponse {
    /** Represents the message text. */
    private String message;

    /** Represents the ID of the message. */
    private int messageId;

    /**
     * Creates a new instance of <code>ImportantMessageResponse</code>. It is required by custom serialization.
     */
    public ImportantMessageResponse() {
        message = "";
        messageId = 0;
    }

    /**
     * Creates a new instance of <code>ImportantMessageResponse</code>.
     * 
     * @param messageId the ID of the message.
     * @param message the message text.
     */
    public ImportantMessageResponse(int messageId, String message) {
        this.messageId = messageId;
        this.message = message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(messageId);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        messageId = reader.readInt();
        message = reader.readString();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse) [");
        ret.append("messageId=");
        ret.append(messageId);
        ret.append(", ");
        ret.append("message=");
        ret.append(message);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the message text of the important message.
     * 
     * @return the message text.
     */
    public String getText() {
        return message;
    }

    /**
     * Gets the ID of the important message.
     * 
     * @return the message ID.
     */
    public int getId() {
        return messageId;
    }
}
