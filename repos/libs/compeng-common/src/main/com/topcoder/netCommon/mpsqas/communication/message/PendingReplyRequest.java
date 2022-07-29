package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class PendingReplyRequest
        extends Message {

    private boolean approved;
    private String message;

    public PendingReplyRequest() {
    }

    public PendingReplyRequest(boolean approved, String message) {
        this.approved = approved;
        this.message = message;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeBoolean(approved);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        approved = reader.readBoolean();
        message = reader.readString();
    }
}

