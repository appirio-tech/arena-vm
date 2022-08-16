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

public class AddRoundRequest extends ContestManagementRequest {

    private int contestId;
    private RoundData round;
    
    public AddRoundRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestId);
        writer.writeObject(round);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        contestId = reader.readInt();
        round = (RoundData)reader.readObject();
    }

    public AddRoundRequest(int contestId, RoundData round) {
        this.contestId = contestId;
        this.round = round;
    }

    public int getContestId() {
        return contestId;
    }

    public RoundData getRound() {
        return round;
    }
}
