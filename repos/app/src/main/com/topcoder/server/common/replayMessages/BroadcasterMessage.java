package com.topcoder.server.common.replayMessages;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public final class BroadcasterMessage implements CustomSerializable {

    private int messageID;
    private long sentTime;
    private Object message;

    BroadcasterMessage() {
    }

    public BroadcasterMessage(int messageID, Object message) {
        this.messageID = messageID;
        this.message = message;
        sentTime = System.currentTimeMillis();
    }

    public Object getMessage() {
        return message;
    }

    public int getMessageID() {
        return messageID;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(messageID);
        writer.writeLong(sentTime);
        writer.writeObject(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        messageID = reader.readInt();
        sentTime = reader.readLong();
        message = reader.readObject();
    }

}
