/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class GetProblemsRequest extends ContestManagementRequest {

    private int roundID;
    
    public GetProblemsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
    }

    public GetProblemsRequest(int id) {
        this.roundID = id;
    }

    public int getRoundID() {
        return roundID;
    }
}
