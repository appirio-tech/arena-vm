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

public class AddAnswerRequest extends ContestManagementRequest {

    private int questionID;
    private AnswerData answer;
    
    public AddAnswerRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(questionID);
        writer.writeInt(answer.getId());
        writer.writeString(answer.getText());
        writer.writeInt(answer.getSortOrder());
        writer.writeBoolean(answer.isCorrect());
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        questionID = reader.readInt();
        answer = new AnswerData(reader.readInt(), reader.readString(), reader.readInt(), reader.readBoolean());
    }

    public AddAnswerRequest(int questionID, AnswerData answer) {
        this.questionID = questionID;
        this.answer = answer;
    }

    public int getQuestionID() {
        return questionID;
    }

    public AnswerData getAnswer() {
        return answer;
    }
}
