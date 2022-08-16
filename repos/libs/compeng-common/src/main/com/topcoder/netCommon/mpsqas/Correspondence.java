package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class Correspondence
        implements Serializable, Cloneable, CustomSerializable {

    private String date = "";
    private String sender = "";
    private String message = "";
    private int correspondenceId = -1;
    private int replyToId = -1;
    private ArrayList receiverUserIds = new ArrayList();

    public Correspondence() {
        this("", "", "", -1, -1);
    }

    public Correspondence(String message, int replyToId) {
        this("", "", message, -1, replyToId);
    }

    public Correspondence(String message, int replyToId,
            ArrayList receiverUserIds) {
        this("", "", message, -1, replyToId);
        this.receiverUserIds = receiverUserIds;
    }

    public Correspondence(String date, String sender, String message) {
        this(date, sender, message, -1, -1);
    }

    public Correspondence(String date, String sender, String message,
            int correspondenceId) {
        this(date, sender, message, correspondenceId, -1);
    }

    public Correspondence(String date, String sender, String message,
            int correspondenceId, int replyToId) {
        this.replyToId = replyToId;
        this.correspondenceId = correspondenceId;
        this.message = message;
        this.date = date;
        this.sender = sender;
    }

    public void setReceiverUserIds(ArrayList receiverUserIds) {
        this.receiverUserIds = receiverUserIds;
    }

    public ArrayList getReceiverUserIds() {
        return receiverUserIds;
    }

    public int getCorrespondenceId() {
        return correspondenceId;
    }

    public void setReplyToId(int replyToId) {
        this.replyToId = replyToId;
    }

    public int getReplyToId() {
        return replyToId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(date);
        writer.writeString(sender);
        writer.writeString(message);
        writer.writeInt(correspondenceId);
        writer.writeInt(replyToId);
        writer.writeArrayList(receiverUserIds);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        date = reader.readString();
        sender = reader.readString();
        message = reader.readString();
        correspondenceId = reader.readInt();
        replyToId = reader.readInt();
        receiverUserIds = reader.readArrayList();
    }
}
