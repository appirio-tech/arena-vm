package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class MainProblemMoveResponse
        extends MoveResponse {

    private HashMap problems;

    public MainProblemMoveResponse() {
        problems = new HashMap();
    }

    public HashMap getProblems() {
        return problems;
    }

    public ArrayList getProblems(int key) {
        return (ArrayList) problems.get(new Integer(key));
    }

    public void addProblem(int key, ProblemInformation problem) {
        Integer ikey = new Integer(key);
        ArrayList list = (ArrayList) problems.get(ikey);

        if (list == null) {
            list = new ArrayList();
            problems.put(ikey, list);
        }
        list.add(problem);
    }

    public void addProblems(int key, ArrayList problems) {
        if (problems == null)
            return;

        Integer ikey = new Integer(key);
        ArrayList list = (ArrayList) this.problems.get(ikey);

        if (list == null) {
            list = new ArrayList();
            this.problems.put(ikey, list);
        }
        list.addAll(problems);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeHashMap(problems);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problems = reader.readHashMap();
    }

    public String toString() {
        return "MainProblemMoveResponse[<" + problems.size() + " problems>]";
    }
}
