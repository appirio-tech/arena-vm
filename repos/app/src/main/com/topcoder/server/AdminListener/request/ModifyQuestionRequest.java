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

public class ModifyQuestionRequest extends ContestManagementRequest {

    private QuestionData question;
    
    public ModifyQuestionRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(question);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        question = (QuestionData)reader.readObject();
    }

    public ModifyQuestionRequest(QuestionData question) {
        this.question = question;
    }

    public QuestionData getQuestion() {
        return question;
    }
}
