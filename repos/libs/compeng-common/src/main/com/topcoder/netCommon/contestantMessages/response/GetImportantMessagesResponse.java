/*
 * ImportantMessageResponse.java Created on March 17, 2005, 10:28 AM This class contains a message that the user should
 * read before successfully logging in
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.Message;

/**
 * Defines a response to send all important messages.<br>
 * Use: This response is specific to <code>GetImportantMessagesRequest</code>. The client should show the list of
 * important messages to the current user.<br>
 * Note: Important messages are different from admin broadcasts. For example, a change to the testing environment is an
 * important message, while a clarification of a problem component is an admin broadcast.
 * 
 * @author Ryan Fairfax
 * @version $Id: GetImportantMessagesResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class GetImportantMessagesResponse extends BaseResponse {
    /** Represents all important messages. */
    private ArrayList al = new ArrayList();

    /**
     * Creates a new instance of <code>GetImportantMessagesResponse</code>. It is required by custom serialization.
     */
    public GetImportantMessagesResponse() {
    }

    /**
     * Adds an important message to this response. The time is represented by the number of milliseconds since January
     * 1, 1970, 00:00:00 GMT.
     * 
     * @param message the message text.
     * @param time the time of the important message.
     * @see java.util.Date#getTime()
     */
    public void addItem(String message, long time) {
        al.add(new ImportantMessage(message, time));
    }

    /**
     * Gets the list of all important messages. The list contains instances of
     * <code>GetImportantMessagesResponse.ImportantMessage</code>. There is no copy.
     * 
     * @return the list of all important messages.
     */
    public List getItems() {
        return al;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(al);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        al = reader.readArrayList();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse) [");
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Defines an important message, including a message text and a time of the message.
     * 
     * @author Ryan Fairfax
     */
    public static final class ImportantMessage extends Message {
        /** Represents the time of the message. */
        long time = 0;

        /** Represents the message text. */
        String message = "";

        /**
         * Creates a new instance of <code>ImportantMessage</code>. It is required by custom serialization.
         */
        public ImportantMessage() {
        }

        /**
         * Creates a new instance of <code>ImportantMessage</code>. The time is represented by the number of
         * milliseconds since January 1, 1970, 00:00:00 GMT.
         * 
         * @param message the message text.
         * @param time the time of the message.
         * @see java.util.Date#getTime()
         */
        public ImportantMessage(String message, long time) {
            this.time = time;
            this.message = message;
        }

        /**
         * Gets the message text of the important message.
         * 
         * @return the message text.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the time of the important message. The time is represented by the number of milliseconds since January
         * 1, 1970, 00:00:00 GMT.
         * 
         * @return the time.
         * @see java.util.Date#getTime()
         */
        public long getTime() {
            return time;
        }

        public void customWriteObject(CSWriter writer) throws IOException {
            super.customWriteObject(writer);
            writer.writeLong(time);
            writer.writeString(message);
        }

        public void customReadObject(CSReader reader) throws IOException {
            super.customReadObject(reader);
            time = reader.readLong();
            message = reader.readString();
        }
    }
}
