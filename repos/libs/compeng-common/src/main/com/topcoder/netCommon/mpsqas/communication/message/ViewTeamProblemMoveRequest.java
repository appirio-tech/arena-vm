package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class ViewTeamProblemMoveRequest extends Message {

    public ViewTeamProblemMoveRequest() {
    }

    public ViewTeamProblemMoveRequest(int problemId) {
        this.problemId = problemId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(problemId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problemId = reader.readInt();
    }

    private int problemId;
}
