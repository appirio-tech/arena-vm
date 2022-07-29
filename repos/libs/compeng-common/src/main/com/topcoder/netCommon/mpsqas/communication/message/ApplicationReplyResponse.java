package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationReplyResponse
        extends Message {

    private boolean success;
    private String message;

    public ApplicationReplyResponse() {
    }

    public ApplicationReplyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeBoolean(success);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        success = reader.readBoolean();
        message = reader.readString();
    }
}

