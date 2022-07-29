package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a survey question, which implements the question interface.
 * 
 * @author Qi Liu
 * @version $Id: SurveyQuestionData.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class SurveyQuestionData implements Question, Serializable, CustomSerializable {
    /**
     * Represents the ID of the question.
     */
    protected int questionID;
    /** Represents the type of the question. */
    protected int questionType;
    /** Represents a flag indicating if the question is an eligibility question. */
    protected boolean eligibleQuestion;
    /** Represents the question text. */
    protected String questionText;
    /** Represents the choices of the question if this question is a single or multiple choice question. */
    protected ArrayList<SurveyChoiceData> choices;

    /**
     * Creates a new instance of <code>SurveyQuestionData</code>. It is required by custom serialization.
     */
    public SurveyQuestionData() {
    }

    /**
     * Creates a new instance of <code>SurveyQuestionData</code>.
     * 
     * @param questionID the ID of the question.
     * @param questionText the text of the question.
     * @param questionType the type of the question.
     * @param eligibleQuestion <code>true</code> if the question is an eligibility question; <code>false</code> otherwise.
     * @param choices the choices of the question if it is a single or multiple choice question.
     */
    public SurveyQuestionData(int questionID, String questionText, int questionType, boolean eligibleQuestion, ArrayList choices) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.questionType = questionType;
        this.choices = choices;
        this.eligibleQuestion = eligibleQuestion;
    }

    /**
     * Gets the ID of the question.
     * 
     * @return the ID of the question.
     */
    public int getQuestionID() {
        return questionID;
    }

    public int getQuestionCategory() {
        if (isEligibleQuestion())
            return ELIGIBILITY;
        else
            return SURVEY;
    }

    public int getQuestionType() {
        return questionType;
    }

    /**
     * Gets a flag indicating if this question is an eligibility question.
     * 
     * @return <code>true</code> if the question category is eligibility; <code>false</code> otherwise.
     */
    public boolean isEligibleQuestion() {
        return eligibleQuestion;
    }

    public String getQuestionText() {
        return questionText;
    }

    public ArrayList getAnswerText() {
        ArrayList text = new ArrayList();
        int choiceCount = 0;
        if (choices != null) choiceCount = choices.size();
        for (int i = 0; i < choiceCount; i++) {
            SurveyChoiceData data = (SurveyChoiceData) choices.get(i);
            text.add(data.getText());
        }
        return text;
    }

    public Answer getAnswer(ArrayList answers) {
        // Initialize a survey answer with the unique identifiers...
        SurveyAnswerData answer = new SurveyAnswerData(questionID, questionType, isEligibleQuestion());

        answer.setAnswers(answers);

        // Set the answers
        boolean correct = true;
        if (getQuestionType() == SINGLECHOICE || getQuestionType() == MULTIPLECHOICE) {
            ArrayList userChoices = new ArrayList();
            for (int i = 0; i < answers.size(); i++) {
                String s = (String) answers.get(i);
                for (int j = 0; j < choices.size(); j++) {
                    SurveyChoiceData data = (SurveyChoiceData) choices.get(j);
                    if (data.getText().equals(s)) {
                        userChoices.add(data);
                        if (isEligibleQuestion() && !data.isCorrect()) {
                            correct = false;
                        }
                    }
                }
            }
            answer.setChoices(userChoices);
        } else {   // For the text answers just include all the choices.
            answer.setChoices(choices);
        }
        answer.setCorrect(correct);

        // Return the answer
        return answer;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(questionID);
        writer.writeInt(questionType);
        writer.writeBoolean(eligibleQuestion);
        writer.writeString(questionText);
        writer.writeArrayList(choices);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        questionID = reader.readInt();
        questionType = reader.readInt();
        eligibleQuestion = reader.readBoolean();
        questionText = reader.readString();
        choices = reader.readArrayList();
    }

}

