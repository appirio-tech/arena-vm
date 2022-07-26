package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines an answer to a survey question. The answer is bound to a specific question.
 * 
 * @author Qi Liu
 * @version $Id: SurveyAnswerData.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class SurveyAnswerData implements Answer, Serializable, CustomSerializable {
    /** Represents the ID of the answer's question. */
    protected int questionID;

    /** Represents the type of the answer's question. */
    protected int type;

    /** Represents a flag indicating if the answer's question is an eligibility question. */
    protected boolean eligible;

    /** Represents a flag indicating if the choice of the answer is a correct one. */
    protected boolean isCorrect;

    /** Represents the list of answers in text. */
    protected ArrayList<String> answers;

    /** Represents the list of choices in the answer if the answer's question is a single or multiple choice question. */
    protected ArrayList<SurveyChoiceData> choices;

    /**
     * Creates a new instance of <code>SurveyAnswerData</code>. It is required by custom serialization.
     */
    public SurveyAnswerData() {
    }

    /**
     * Creates a new instance of <code>SurveyAnswerData</code>. All answers and choices are initialized
     * as <code>null</code>.
     * 
     * @param questionID the ID of the question to be answered.
     * @param type the type of the question to be answered.
     * @param isEligibleAnswer <code>true</code> if the question is an eligibility question; <code>false</code> otherwise.
     */
    public SurveyAnswerData(int questionID, int type, boolean isEligibleAnswer) {
        this.questionID = questionID;
        this.type = type;
        this.eligible = isEligibleAnswer;
    }

    /**
     * Sets the answer texts of the question.
     * 
     * @param answers the list of answer texts.
     */
    public void setAnswers(ArrayList answers) {
        this.answers = answers;
    }

    public ArrayList getAnswers() {
        return answers;
    }

    /**
     * Sets the choices in the answer. Only user-selected choices should be set. There is no copy.
     * 
     * @param c the list of choices in the answer.
     */
    public void setChoices(ArrayList c) {
        choices = c;
    }

    /**
     * Gets the choices in the answer. Only user-selected choices should be set. There is no copy.
     * 
     * @return the list of choices in the answer.
     */
    public ArrayList getChoices() {
        return choices;
    }

    /**
     * Gets the ID of the answer's question.
     * 
     * @return the ID of the question.
     */
    public int getQuestionID() {
        return questionID;
    }

    /**
     * Gets the type of the answer's question.
     * 
     * @return the type of the question.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets a flag indicating if the question is an eligibility question.
     * 
     * @return <code>true</code> if the question is an eligibility question; <code>false</code> otherwise.
     */
    public boolean isEligible() {
        return eligible;
    }

    /**
     * Gets a flag indicating if the answer is a 'correct' answer.
     * 
     * @return <code>true</code> if this answer is a 'correct' answer; <code>false</code> otherwise.
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Sets a flag indicating if the answer is a 'correct' answer.
     * 
     * @param value <code>true</code> if this answer is a 'correct' answer; <code>false</code> otherwise.
     */
    public void setCorrect(boolean value) {
        isCorrect = value;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(questionID);
        writer.writeInt(type);
        writer.writeBoolean(eligible);
        writer.writeBoolean(isCorrect);
        writer.writeArrayList(answers);
        writer.writeArrayList(choices);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        questionID = reader.readInt();
        type = reader.readInt();
        eligible = reader.readBoolean();
        isCorrect = reader.readBoolean();
        answers = reader.readArrayList();
        choices = reader.readArrayList();
    }
}
