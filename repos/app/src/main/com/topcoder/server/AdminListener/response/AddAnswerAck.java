/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class AddAnswerAck extends ContestManagementAck {

    private int answerID;
    
    public AddAnswerAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(answerID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        answerID = reader.readInt();
    }

    public AddAnswerAck(Throwable exception) {
        super(exception);
    }

    public AddAnswerAck(int answerID) {
        this.answerID = answerID;
    }

    public int getAnswerID() {
        return answerID;
    }
}
