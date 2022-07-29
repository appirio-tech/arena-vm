/**
 * Answer.java Description: Interface to an the answer of the question
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */

package com.topcoder.netCommon.contest;

import java.util.ArrayList;

/**
 * Defines an interface which represents the answer(s) to a question.
 * 
 * @author Tim "Pops" Roberts
 * @version $Id: Answer.java 72046 2008-07-31 06:47:43Z qliu $
 */
public interface Answer {
    /**
     * Gets the answers of a question. The answers are texts.
     * 
     * @return the list of answers.
     */
    public ArrayList getAnswers();
}

/* @(#)Answer.java */
