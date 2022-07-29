package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;


public final class Question implements Serializable {


    private int roundId;
    private int questionId;
    private String questionText;
    private int statusId;
    private String keyword;
    private int questionTypeId;
    private int questionStyleId;
    private String questionTypeDesc;
    private String questionStyleDesc;
    private ArrayList answers; //ArrayList of Answer attributes


    public Question() {
        init();
    }


    private void init() {
        this.roundId = 0;
        this.questionId = 0;
        this.questionText = "";
        this.statusId = 0;
        this.keyword = "";
        this.questionTypeId = 0;
        this.questionStyleId = 0;
        this.questionTypeDesc = "";
        this.questionStyleDesc = "";
        this.answers = new ArrayList();
    }


// set

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public void setQuestionStyleId(int questionStyleId) {
        this.questionStyleId = questionStyleId;
    }

    public void setQuestionTypeDesc(String questionTypeDesc) {
        this.questionTypeDesc = questionTypeDesc;
    }

    public void setQuestionStyleDesc(String questionStyleDesc) {
        this.questionStyleDesc = questionStyleDesc;
    }

    public void setAnswers(ArrayList answers) {
        this.answers = answers;
    }


// get
    public int getRoundId() {
        return this.roundId;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public int getQuestionTypeId() {
        return this.questionTypeId;
    }

    public int getQuestionStyleId() {
        return this.questionStyleId;
    }

    public String getQuestionTypeDesc() {
        return this.questionTypeDesc;
    }

    public String getQuestionStyleDesc() {
        return this.questionStyleDesc;
    }

    public ArrayList getAnswers() {
        return this.answers;
    }

}
