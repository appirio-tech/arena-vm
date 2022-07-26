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

public class ModifyAnswerRequest extends ContestManagementRequest {

    private AnswerData answer;
    
    public ModifyAnswerRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(answer);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        answer = (AnswerData)reader.readObject();
    }

    public ModifyAnswerRequest(AnswerData answer) {
        this.answer = answer;
    }

    public AnswerData getAnswer() {
        return answer;
    }
}
