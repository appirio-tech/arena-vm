package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.SurveyAnswerData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to register a user to a round. The survey answers (if any) are also sent back to the server.<br>
 * Use: When the current user agrees to the terms of use and answers all survey questions, this request should be sent
 * to finalize the registration process.<br>
 * Note: The round must be in registration phase. This request must be sent after <code>RegisterInfoRequest</code>.
 * 
 * @author Walter Mundt
 * @version $Id: RegisterRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see RegisterInfoRequest
 */
public class RegisterRequest extends BaseRequest {
    /** Represents the survey answers. */
    protected ArrayList<SurveyAnswerData> surveyData;

    /** Represents the ID of the round to be registered. */
    protected int roundID;

    /**
     * Creates a new instance of <code>RegisterRequest</code>. It is required by custom serialization.
     */
    public RegisterRequest() {
    }

    /**
     * Creates a new instance of <code>RegisterRequest</code>. The survey answers must be a list of
     * <code>SurveyAnswerData</code> instances. There is no copy.
     * 
     * @param surveyData the answers of survey questions.
     * @param roundID the ID of the round to be registered.
     * @see com.topcoder.netCommon.contest.SurveyAnswerData
     */
    public RegisterRequest(ArrayList surveyData, int roundID) {
        this.surveyData = surveyData;
        this.roundID = roundID;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        surveyData = reader.readArrayList();
        roundID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(surveyData);
        writer.writeInt(roundID);
    }

    public int getRequestType() {
        return ContestConstants.REGISTER;
    }

    /**
     * Gets the list of survey answers. The answers are <code>SurveyAnswerData</code> instances. There is no copy.
     * 
     * @return the list of survey answers.
     * @see com.topcoder.netCommon.contest.SurveyAnswerData
     */
    public ArrayList getSurveyData() {
        return surveyData;
    }

    /**
     * Gets the ID of the round to be registered.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.RegisterRequest) [");
        ret.append("surveyData = ");
        if (surveyData == null) {
            ret.append("null");
        } else {
            ret.append(surveyData.toString());
        }
        ret.append(", ");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
