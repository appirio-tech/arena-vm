package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewApplicationMoveRequest
        extends Message {

    private int applicationId;

    public ViewApplicationMoveRequest() {
    }

    public ViewApplicationMoveRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(applicationId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        applicationId = reader.readInt();
    }
}

