package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a choice of a survey question if the question is a single or multiple choice question. A choice has an unique
 * ID, a text description and a 'correct' flag. The 'correct' flag can mean different things in different question
 * category. For example, in 'eligibility' questions, 'correct' means 'eligible'. Note that a choice is not necessarily
 * bound to a specific question.
 * 
 * @author Qi Liu
 * @version $Id: SurveyChoiceData.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class SurveyChoiceData implements Serializable, CustomSerializable {
    /** Represents the ID of the choice. */
    protected int m_id;

    /** Represents the text of the choice. */
    protected String m_text;

    /** Represents a flag indicating if the choice is a 'correct' choice. */
    protected boolean m_correct;

    /**
     * Creates a new instance of <code>SurveyChoiceData</code>. It is required by custom serialization.
     */
    public SurveyChoiceData() {
    }

    /**
     * Creates a new instance of <code>SurveyChoiceData</code>.
     * 
     * @param id the ID of the choice.
     * @param text the text of the choice.
     * @param correct <code>true</code> if the choice is 'correct'; <code>false</code> otherwise.
     */
    public SurveyChoiceData(int id, String text, boolean correct) {
        m_id = id;
        m_text = text;
        m_correct = correct;
    }

    /**
     * Gets the text of the choice.
     * 
     * @return the text of the choice.
     */
    public String getText() {
        return m_text;
    }

    /**
     * Gets the ID of the choice.
     * 
     * @return the ID of the choice.
     */
    public int getID() {
        return m_id;
    }

    /**
     * Gets the 'correct' flag.
     * 
     * @return <code>true</code> if the choice is 'correct'; <code>false</code> otherwise.
     */
    public boolean isCorrect() {
        return m_correct;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_id);
        writer.writeString(m_text);
        writer.writeBoolean(m_correct);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        m_id = reader.readInt();
        m_text = reader.readString();
        m_correct = reader.readBoolean();
    }
}
