/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetQuestionsAck extends ContestManagementAck {

    private Collection questions;
    
    public GetQuestionsAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(questions.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        questions = Arrays.asList(reader.readObjectArray());
    }

    public GetQuestionsAck(Throwable exception) {
        super(exception);
    }

    public GetQuestionsAck(Collection problems) {
        super();
        this.questions = problems;
    }

    public Collection getQuestions() {
        return questions;
    }
}
