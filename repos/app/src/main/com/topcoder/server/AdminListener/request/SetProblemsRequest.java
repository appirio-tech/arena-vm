/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class SetProblemsRequest extends ContestManagementRequest {

    private int id;
    private Collection problems;
    
    public SetProblemsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObjectArray(problems.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        problems = Arrays.asList(reader.readObjectArray());
    }

    public SetProblemsRequest(int id, Collection problems) {
        this.id = id;
        this.problems = problems;
    }

    public int getRoundID() {
        return id;
    }

    public Collection getProblems() {
        return problems;
    }
}
