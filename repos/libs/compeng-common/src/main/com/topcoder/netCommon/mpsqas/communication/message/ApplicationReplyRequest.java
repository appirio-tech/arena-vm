package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationReplyRequest
        extends Message {

    private boolean accepted;
    private String message;

    public ApplicationReplyRequest() {
    }

    public ApplicationReplyRequest(boolean accepted, String message) {
        this.accepted = accepted;
        this.message = message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeBoolean(accepted);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        accepted = reader.readBoolean();
        message = reader.readString();
    }
}

