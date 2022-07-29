/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class GetQuestionsRequest extends ContestManagementRequest {

    private int roundID;
    
    public GetQuestionsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
    }


    public GetQuestionsRequest(int id) {
        this.roundID = id;
    }

    public int getRoundID() {
        return roundID;
    }
}
