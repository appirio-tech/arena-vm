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

public class SetSurveyRequest extends ContestManagementRequest {

    private SurveyData survey;
    
    public SetSurveyRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(survey);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        survey = (SurveyData)reader.readObject();
    }

    public SetSurveyRequest(SurveyData survey) {
        this.survey = survey;
    }

    public SurveyData getSurvey() {
        return survey;
    }
}
