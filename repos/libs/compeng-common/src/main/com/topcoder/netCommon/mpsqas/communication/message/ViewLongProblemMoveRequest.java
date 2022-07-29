package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author mktong
 */
public class ViewLongProblemMoveRequest
        extends Message {

    private int problemId;

    public ViewLongProblemMoveRequest(int problemId) {
        this.problemId = problemId;
    }

    public ViewLongProblemMoveRequest() {
//        this.problemId = problemId;
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
}
