/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class DeleteAnswerRequest extends ContestManagementRequest {

    private int answerID;
    
    public DeleteAnswerRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(answerID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        answerID = reader.readInt();
    }

    public DeleteAnswerRequest(int answerID) {
        this.answerID = answerID;
    }

    public int getAnswerID() {
        return answerID;
    }
}
