package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.problem.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class SaveProblemStatementRequest
        extends Message {

    protected ProblemComponent component;
    protected Problem problem;
    protected int type;

    public SaveProblemStatementRequest(ProblemComponent component) {
        this.component = component;
        this.type = MessageConstants.COMPONENT_STATEMENT;
    }

    public SaveProblemStatementRequest(Problem problem) {
        this.problem = problem;
        this.type = MessageConstants.PROBLEM_STATEMENT;
    }

    public SaveProblemStatementRequest() {
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

