package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewContestMoveRequest
        extends Message {

    private int contestId;

    public ViewContestMoveRequest() {
    }

    public ViewContestMoveRequest(int contestId) {
        this.contestId = contestId;
    }

    public int getContestId() {
        return contestId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(contestId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        contestId = reader.readInt();
    }
}

