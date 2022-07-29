package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;

public class SurveyQuestion implements Serializable {

    private int m_questionID;

    public final int getID() {
        return m_questionID;
    }

    private String m_str;

    public final String getString() {
        return m_str;
    }

    private int m_type;

    public final int getType() {
        return m_type;
    }

    private int m_questionType;
    //public int getQuestionType() { return m_questionType; }

    public boolean isEligibleQuestion() {
        return m_questionType == ContestConstants.ELIGIBLE_QUESTION;
    }

    private ArrayList m_answerChoices;

    public final ArrayList getAnswerChoices() {
        return m_answerChoices;
    }

    public SurveyQuestion(int id, String str, int type, int questionType, ArrayList ansChoice) {
        m_questionID = id;
        m_str = str;
        m_type = type;
        m_questionType = questionType;
        m_answerChoices = ansChoice;
    }

    public String toString() {
        return m_str;
    }
}
