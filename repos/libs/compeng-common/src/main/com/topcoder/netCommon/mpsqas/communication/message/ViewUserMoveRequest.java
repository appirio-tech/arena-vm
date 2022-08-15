package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewUserMoveRequest
        extends Message {

    private int userId;

    public ViewUserMoveRequest() {
    }

    public ViewUserMoveRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(userId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        userId = reader.readInt();
    }
}

