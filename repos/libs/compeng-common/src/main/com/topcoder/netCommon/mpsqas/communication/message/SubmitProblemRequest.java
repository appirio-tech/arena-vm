package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class SubmitProblemRequest
        extends Message {

    protected ProblemInformation problem;

    public SubmitProblemRequest() {
    }

    public SubmitProblemRequest(ProblemInformation problem) {
        this.problem = problem;
    }

    public ProblemInformation getProblem() {
        return problem;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(problem);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problem = (ProblemInformation) reader.readObject();
    }
}

