/**
 * Question.java Description: Interface to a question
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */

package com.topcoder.netCommon.contest;

import java.util.ArrayList;

/**
 * Defines an interface which represents a question to be answered by user.
 * 
 * @author Tim "Pops" Roberts
 * @version $Id: Question.java 72046 2008-07-31 06:47:43Z qliu $
 */
public interface Question {
    /**
     * Represents the single choice question type (choose one from many).
     */
    public static final int SINGLECHOICE = 1;

    /**
     * Represents the multiple choices question type (choose more than one from many).
     */
    public static final int MULTIPLECHOICE = 2;

    /**
     * Represents the long answer question type (allow user to enter mutli-line texts).
     */
    public static final int LONGANSWER = 3;

    /**
     * Represents the short answer question type (allow user to enter single-line texts).
     */
    public static final int SHORTANSWER = 4;

    /**
     * Represents the question is related to eligibility, such as the eligibility to win a prize.
     */
    public static final int ELIGIBILITY = 1;

    /**
     * Represents the question is a general survey.
     */
    public static final int SURVEY = 2;

    /**
     * Gets the category of a question.
     * 
     * @return the category of a question.
     */
    public int getQuestionCategory();

    /**
     * Gets the type of a question.
     * 
     * @return the type of a question.
     */
    public int getQuestionType();

    /**
     * Gets the text of a question.
     * 
     * @return the text of a question.
     */
    public String getQuestionText();

    /**
     * Gets the possible choices for a single choice or multiple choice question.
     * 
     * @return a list of possible choices.
     */
    public ArrayList getAnswerText();

    /**
     * Gets an answer object based on the answers of this question.
     * 
     * @param answers the list of answers.
     * @return an answer object representing the answers to this question.
     */
    public Answer getAnswer(ArrayList answers);
}

/* @(#)Question.java */
