/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class GetAnswersRequest extends ContestManagementRequest {

    private int questionID;
    
    public GetAnswersRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(questionID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        questionID = reader.readInt();
    }

    public GetAnswersRequest(int id) {
        this.questionID = id;
    }

    public int getQuestionID() {
        return questionID;
    }
}
