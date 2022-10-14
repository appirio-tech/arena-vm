package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Lets the applet know the problem id structure of the problem it is
 * viewing.
 *
 * @author mitalub
 */
public class NewProblemIdStructureResponse
        extends Message {

    private ProblemIdStructure problemIdStructure;

    public NewProblemIdStructureResponse() {
    }

    public NewProblemIdStructureResponse(ProblemIdStructure problemIdStructure) {
        this.problemIdStructure = problemIdStructure;
    }

    public ProblemIdStructure getProblemIdStructure() {
        return problemIdStructure;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(problemIdStructure);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problemIdStructure = (ProblemIdStructure) reader.readObject();
    }
}
