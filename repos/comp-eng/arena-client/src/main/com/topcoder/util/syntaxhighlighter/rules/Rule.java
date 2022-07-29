/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter.rules;

import com.topcoder.util.syntaxhighlighter.HighlightedSequence;
import com.topcoder.util.syntaxhighlighter.RuleMatchException;
import com.topcoder.util.syntaxhighlighter.TextStyle;


/**
 * <p>
 * A Rule defines an interface which allows the SyntaxHighlighter to apply a series of rules easily without any
 * foreknowledge of the actual rule algorithm for that rule type. More specifically, it defines a single method,
 * applyRule, which allows a caller to specify a sequence to highlight and have the Rule create segments that are
 * to be highlighted. A Rule doesn't do anything but identify the positions that need to be highlighted. The bulk
 * of the work in calculating is done by the HighlightedSequence class.
 * </p>
 * <p>
 * Thread Safety: A Rule is not used by multiple threads internally, thus is it not necessary for a Rule instance
 * to be thread safe. However, given that Rule instances are shared.
 * </p>
 *
 * @author duner, still
 * @version 2.0
 *
 */
public interface Rule {
    /**
     * <p>
     * This method applies a Rule to a HighlightedSequence given a TextStyle that defines the formatting of the
     * highlighted portions applied by this rule. If the Rule does not apply to the sequence, it should simply exit
     * out without signalling any error.
     * </p>
     *
     *
     * @param sequence
     *            The HighlightedSequence that is operated on. This may not be null.
     * @param style
     *            The TextStyle used to format any matched sequences. This may be null to signify no formatting
     *            (i.e. default).
     * @throws NullPointerException
     *             if sequence is null.
     * @throws RuleMatchException
     *             if the rule is unable to be applied due to an error (i.e. no match is not an error).
     */
    public void applyRule(HighlightedSequence sequence, TextStyle style)
        throws RuleMatchException;
    
    /**
     * <p>
     * This method get a lange [start..end] matched  with the Rule. 
     * </p>
     * 
     * @param sequence the sequce that is operated on. This may not be null.
     * @return the positon of if matched found, null otherwise. 
     * @throws NullPointerException if sequence is null.
     */
    public Point getToken(HighlightedSequence sequence);
    
    /**
     * <p>
     * This inner class is used to represent the token position in sequence.
     * </p>
     */
    public class Point {
        /**
         * <p>
         * The start position.
         * </p>
         */
        int start;
        
        /**
         * <p>
         * The end position.
         * </p>
         */
        int end;
        
        /**
         * <p>
         * Construct with the supplied start and end.
         * </p>
         * 
         * @param start the start position
         * @param end the end position
         * 
         * @throws IllegalArgumentException if start or end is negative.
         */
        public Point(int start, int end) {
            if (start < 0) {
                throw new IllegalArgumentException("start should be non-negative.");
            }
            if (end < 0) {
                throw new IllegalArgumentException("end should be non-negative.");
            }
            this.start = start;
            this.end = end;
        }
        
        /**
         * <p>
         * Gets the start position.
         * </p>
         * 
         * @return the start position
         */
        public int getStart() {
            return this.start;
        }
        
        /**
         * <p>
         * Gets the end position.
         * </p>
         * 
         * @return the end Position
         */
        public int getEnd() {
            return this.end;
        }
    }
}
