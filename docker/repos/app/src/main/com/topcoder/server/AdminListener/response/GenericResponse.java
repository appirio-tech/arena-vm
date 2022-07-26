package com.topcoder.server.AdminListener.response;


import com.topcoder.shared.netCommon.*;

import java.io.*;

public class GenericResponse implements CustomSerializable, Serializable {

    private int recipientId;
    private Object responseObject;

    public GenericResponse() {
    }

    public GenericResponse(int recipientId, Object responseObject) {
        this.recipientId = recipientId;
        this.responseObject = responseObject;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(recipientId);
        writer.writeObject(responseObject);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        recipientId = reader.readInt();
        responseObject = reader.readObject();
    }

}

