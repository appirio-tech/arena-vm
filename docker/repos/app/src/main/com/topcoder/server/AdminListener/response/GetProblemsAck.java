/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetProblemsAck extends ContestManagementAck {

    private Collection problems;
    private Collection assignedProblems;
    
    public GetProblemsAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(problems.toArray());
        writer.writeObjectArray(assignedProblems.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        problems = Arrays.asList(reader.readObjectArray());
        assignedProblems = Arrays.asList(reader.readObjectArray());
    }

    public GetProblemsAck(Throwable exception) {
        super(exception);
    }

    public GetProblemsAck(Collection problems, Collection assignedProblems) {
        super();
        this.problems = problems;
        this.assignedProblems = assignedProblems;
    }

    public Collection getProblems() {
        return problems;
    }

    public Collection getAssignedProblems() {
        return assignedProblems;
    }
}
