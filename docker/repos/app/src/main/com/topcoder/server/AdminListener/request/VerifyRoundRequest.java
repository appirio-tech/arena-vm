/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class VerifyRoundRequest extends ContestManagementRequest {

    private int roundID;

    public VerifyRoundRequest() {
    
    }
    
    public VerifyRoundRequest(int id) {
        this.roundID = id;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        roundID = reader.readInt();
    }

    public int getRoundID() {
        return roundID;
    }
}
