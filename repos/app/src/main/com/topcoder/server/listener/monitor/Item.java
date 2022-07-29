package com.topcoder.server.listener.monitor;

import java.io.*;
import java.util.Date;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author: Sylvan Haas IV (syhaas)
 * @created: 2002-05-09
 * Base class for ChatItems and QuestionItems
 * Built in mind for the Moderated-Chat Sessions project to allow
 *  both ChatItems and QuestionItems
 *
 * Known extended classes:
 *  ChatItem, QuestionItem
 */
public abstract class Item extends ContestMonitorResponse implements CustomSerializable, Serializable {

    protected int roomID;
    protected String username;
    protected String message;
    protected long timestamp;

    public Item() {
    }

    public Item(int roomID, String username, String message) {
        this.roomID = roomID;
        this.username = username;
        this.message = message;
        timestamp = System.currentTimeMillis();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roomID);
        writer.writeString(username);
        writer.writeString(message);
        writer.writeLong(timestamp);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roomID = reader.readInt();
        username = reader.readString();
        message = reader.readString();
        timestamp = reader.readLong();
    }

    public String getMessage() {
        return message;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        Date date = new Date(timestamp);
        return "" + date;
    }

    public String toString() {
        return "roomID=" + roomID + ", username=" + username + ", timestamp=" + timestamp + ", message=" + message;
    }

    /**
     * Should return TRUE if message has "taboo" words, FALSE if it is clean
     */
    public abstract boolean isBad();

}
