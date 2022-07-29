package com.topcoder.server.common;

import java.io.Serializable;

public class SurveyAnswer implements Serializable {

    int m_answerID;

    public final int getAnswerID() {
        return m_answerID;
    }

    String m_answer;

    public String getAnswer() {
        return m_answer;
    }

    boolean m_correctEligible;

    public boolean isCorrectEligible() {
        return m_correctEligible;
    }

    public SurveyAnswer(int id, String answer, boolean correctEligible) {
        m_answerID = id;
        m_answer = answer;
        m_correctEligible = correctEligible;
    }
}

