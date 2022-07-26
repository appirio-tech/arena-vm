/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.server.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class ModifyContestRequest extends ContestManagementRequest {

    private ContestData contest;
    private int id;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObject(contest);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        contest = (ContestData)reader.readObject();
    }
    
    public ModifyContestRequest() {
        
    }

    public ModifyContestRequest(int id, ContestData contest) {
        this.id = id;
        this.contest = contest;
    }

    public ContestData getContest() {
        return contest;
    }

    public int getId() {
        return id;
    }
}
