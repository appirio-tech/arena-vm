/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class GetRoundsRequest extends ContestManagementRequest {

    private int contestId;
    
    public GetRoundsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestId);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        contestId = reader.readInt();
    }

    public GetRoundsRequest(int contestId) {
        this.contestId = contestId;
    }

    public int getContestId() {
        return contestId;
    }
}
