package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class FoyerMoveResponse
        extends MoveResponse {

    ArrayList problems;

    public FoyerMoveResponse() {
        this(new ArrayList());
    }

    public FoyerMoveResponse(ArrayList problems) {
        this.problems = problems;
    }

    public ArrayList getProblems() {
        return problems;
    }

    public void addProblem(ProblemInformation problem) {
        problems.add(problem);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeArrayList(problems);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problems = reader.readArrayList();
    }
}
