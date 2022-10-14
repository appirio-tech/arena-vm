/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class DeleteQuestionRequest extends ContestManagementRequest {

    private int questionID;
    
    public DeleteQuestionRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(questionID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        questionID = reader.readInt();
    }

    public DeleteQuestionRequest(int questionID) {
        this.questionID = questionID;
    }

    public int getQuestionID() {
        return questionID;
    }
}
