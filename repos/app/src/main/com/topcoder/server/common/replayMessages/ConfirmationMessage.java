package com.topcoder.server.common.replayMessages;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public final class ConfirmationMessage implements CustomSerializable {

    private int messageID;
    private long sentTime;
    private long receivedTime;

    ConfirmationMessage() {
    }

    public ConfirmationMessage(int messageID, long sentTime) {
        this.messageID = messageID;
        this.sentTime = sentTime;
        receivedTime = System.currentTimeMillis();
    }

    public long getReceivedTime() {
        return receivedTime;
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
        writer.writeLong(receivedTime);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        messageID = reader.readInt();
        sentTime = reader.readLong();
        receivedTime = reader.readLong();
    }

}
