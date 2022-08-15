package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationRoomMoveResponse
        extends MoveResponse {

    private int applicationType;

    public ApplicationRoomMoveResponse() {
    }

    public ApplicationRoomMoveResponse(int applicationType) {
        this.applicationType = applicationType;
    }

    public int getApplicationType() {
        return applicationType;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(applicationType);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        applicationType = reader.readInt();
    }
}
