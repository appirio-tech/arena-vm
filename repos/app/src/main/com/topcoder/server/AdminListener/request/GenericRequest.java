package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public class GenericRequest implements CustomSerializable {

    private int senderId;
    private Object requestObject;

    public GenericRequest() {
    }

    public GenericRequest(int senderId, Object requestObject) {
        this.senderId = senderId;
        this.requestObject = requestObject;
    }

    public int getSenderId() {
        return senderId;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(senderId);
        writer.writeObject(requestObject);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        senderId = reader.readInt();
        requestObject = reader.readObject();
    }
}
