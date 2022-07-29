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

public class AddContestRequest extends ContestManagementRequest {

    private ContestData contest;

    public AddContestRequest() {
        
    }
    
    public AddContestRequest(ContestData contest) {
        this.contest = contest;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(contest);
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        contest = (ContestData)reader.readObject();
    }

    public ContestData getContest() {
        return contest;
    }
}
