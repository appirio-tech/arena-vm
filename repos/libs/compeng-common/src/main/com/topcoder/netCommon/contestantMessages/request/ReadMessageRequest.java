package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server that the important message has been read.<br>
 * Use: When the important message is shown to the current user (either automatically or manually), this request is
 * sent.
 * 
 * @author Qi Liu
 * @version $Id: ReadMessageRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class ReadMessageRequest extends BaseRequest {
    /** Represents the ID of the important message. */
    protected int messageID;

    /**
     * Creates a new instance of <code>ReadMessageRequest</code>. It is required by custom serialization.
     */
    public ReadMessageRequest() {
    }

    /**
     * Creates a new instance of <code>ReadMessageRequest</code>.
     * 
     * @param messageID the ID of the read important message.
     */
    public ReadMessageRequest(int messageID) {
        this.messageID = messageID;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        messageID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(messageID);
    }

    public int getRequestType() {
        return ContestConstants.READ_MESSAGE_REQUEST;
    }

    /**
     * Gets the ID of the read important message.
     * 
     * @return the important message ID.
     */
    public int getMessageID() {
        return messageID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ReadMessageRequest) [");
        ret.append("messageID = ");
        ret.append(messageID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
