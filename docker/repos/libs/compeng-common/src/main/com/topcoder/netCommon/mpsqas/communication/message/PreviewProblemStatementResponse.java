package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.problem.*;

import java.io.*;

/**
 *
 * @author mitalub
 */
public class PreviewProblemStatementResponse
        extends Message {

    protected ProblemComponent component;
    protected Problem problem;
    protected int type;

    public PreviewProblemStatementResponse() {
    }

    public PreviewProblemStatementResponse(ProblemComponent component) {
        type = MessageConstants.COMPONENT_STATEMENT;
        this.component = component;
    }

    public PreviewProblemStatementResponse(Problem problem) {
        type = MessageConstants.PROBLEM_STATEMENT;
        this.problem = problem;
    }

    public ProblemComponent getComponent() {
        return component;
    }

    public Problem getProblem() {
        return problem;
    }

    public int getType() {
        return type;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(problem);
        writer.writeObject(component);
        writer.writeInt(type);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problem = (Problem) reader.readObject();
        component = (ProblemComponent) reader.readObject();
        type = reader.readInt();
    }

}
