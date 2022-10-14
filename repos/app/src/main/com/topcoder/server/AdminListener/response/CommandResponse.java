package com.topcoder.server.AdminListener.response;


import java.io.*;

import com.topcoder.shared.netCommon.*;


public abstract class CommandResponse implements CustomSerializable, Serializable {

    private String message;


    public CommandResponse() {
    }


    public CommandResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        message = reader.readString();
    }

}

