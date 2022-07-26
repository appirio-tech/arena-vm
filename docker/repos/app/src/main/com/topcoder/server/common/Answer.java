package com.topcoder.server.common;

import java.io.Serializable;


public final class Answer implements Serializable {


    private int answerId;
    private int questionId;
    private String answerText;
    private int sortOrder;
    private int correct;

    public Answer() {
        init();
    }


    private void init() {
        this.answerId = 0;
        this.questionId = 0;
        this.answerText = "";
        this.sortOrder = 0;
        this.correct = -1;
    }


// set

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }


// get
    public int getAnswerId() {
        return this.answerId;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public int getSortOrder() {
        return this.sortOrder;
    }

    public int getCorrect() {
        return this.correct;
    }

}
