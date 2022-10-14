package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewContestMoveResponse
        extends MoveResponse {

    private ContestInformation contest;

    public ViewContestMoveResponse() {
    }

    public ViewContestMoveResponse(ContestInformation contest) {
        this.contest = contest;
    }

    public ContestInformation getContest() {
        return contest;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(contest);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        contest = (ContestInformation) reader.readObject();
    }
}
