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

public class GetAnswersAck extends ContestManagementAck {

    private Collection answers;
    
    public GetAnswersAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(answers.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        answers = Arrays.asList(reader.readObjectArray());
    }

    public GetAnswersAck(Throwable exception) {
        super(exception);
    }

    public GetAnswersAck(Collection problems) {
        super();
        this.answers = problems;
    }

    public Collection getAnswers() {
        return answers;
    }
}
