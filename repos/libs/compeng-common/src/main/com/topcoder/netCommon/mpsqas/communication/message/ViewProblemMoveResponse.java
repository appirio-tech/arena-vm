package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewProblemMoveResponse
        extends MoveResponse {

    private ProblemInformation problem;
    private boolean statementEditable;

    public ViewProblemMoveResponse() {
    }

    public ViewProblemMoveResponse(ProblemInformation problem, boolean statementEditable) {
        this.problem = problem;
        this.statementEditable = statementEditable;
    }

    public ProblemInformation getProblem() {
        return problem;
    }

    public boolean isStatementEditable() {
        return statementEditable;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(problem);
        writer.writeBoolean(statementEditable);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problem = (ProblemInformation) reader.readObject();
        statementEditable = reader.readBoolean();
    }
}
