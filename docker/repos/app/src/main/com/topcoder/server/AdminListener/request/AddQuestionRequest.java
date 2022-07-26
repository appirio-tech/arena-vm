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

public class AddQuestionRequest extends ContestManagementRequest {

    private int roundID;
    private QuestionData question;
    
    public AddQuestionRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(question);
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        question = (QuestionData)reader.readObject();
        roundID = reader.readInt();
    }

    public AddQuestionRequest(int roundID, QuestionData question) {
        this.roundID = roundID;
        this.question = question;
    }

    public int getRoundID() {
        return roundID;
    }

    public QuestionData getQuestion() {
        return question;
    }
}
