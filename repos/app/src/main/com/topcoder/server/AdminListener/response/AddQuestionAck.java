/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class AddQuestionAck extends ContestManagementAck {

    private int questionID;

    public AddQuestionAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(questionID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        questionID = reader.readInt();
    }
    
    public AddQuestionAck(Throwable exception) {
        super(exception);
    }

    public AddQuestionAck(int answerID) {
        super();
        this.questionID = answerID;
    }

    public int getQuestionID() {
        return questionID;
    }
}
