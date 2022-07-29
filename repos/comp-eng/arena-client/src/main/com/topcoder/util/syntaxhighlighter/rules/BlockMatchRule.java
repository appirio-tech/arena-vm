/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter.rules;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.topcoder.util.syntaxhighlighter.HighlightedSequence;
import com.topcoder.util.syntaxhighlighter.TextStyle;


/**
 * <p>
 * BlockMatchRule defines a simple means of identifying a block of text between two delimiting String. This
 * rule does not take into account anything other than a simple match, it will simply look for these two strings
 * and "highlight" everything between the two strings (including the delimiters themselves).
 * If a start delimiter is found, but not an end delimiter, then the entire string, starting at the index of
 * the start delimiter, will be highlighted.
 * </p>
 * <p>NOTE: Don't use BlockMatchRule(Node) to create new BlockMatchRule, it is designed for internal call only.</p>
 * <p>
 * Thread Safety: This class only uses immutable state and is thread safe.
 * </p>
 *
 * @author  duner, still
 * @version 2.0
 */
public class BlockMatchRule implements Rule {
    /**
     * <p>The name for startDelimiter node.</p>
     */
    private static final String START_DELIMITER_NODE_NAME = "startDelimiter";

    /**
     * <p>The name for endDelimiter node.</p>
     */
    private static final String END_DELIMITER_NODE_NAME = "endDelimiter";

    /**
     * <p>
     * This represents the start delimeter used in matching a block of text (see applyRule). This is set in
     * the constructors to a non-null, non-empty String. It is immutable once set.
     * </p>
     *
     */
    private final String startDelimiter;

    /**
     * <p>
     * This represents the end delimeter used in matching a block of text (see applyRule). This is set in the
     * constructors to a non-null, non-empty String. It is immutable once set.
     * </p>
     *
     */
    private final String endDelimiter;

    /**
     * <p>
     * Initializes this BlockMatchRule instance using the given Node. The provided Node will be an element
     * corresponding to a BlockMatchRule. For instance, the portion this
     * Node represents is represented in the following example XML:
     * <pre>
     *   &lt;blockMatchRule&gt;
     *                   &lt;startDelimiter&gt;...&lt;/startDelimiter&gt;
     *                   &lt;endDelimiter&gt;...&lt;endDelimiter&gt;
     *   &lt;blockMatchRule&gt;
     * </pre>
     *
     * <p>
     * NOTE: Don't use this constructor to create new BlockMatchRule, it is designed for internal call only.
     * </p>
     *
     * @param node
     *            An element Node corresponding to a WordMatchRule rule type. It must be non-null.
     * @throws IllegalArgumentException
     *            when config for blockMatchRule is not right, like miss startDelimiter or endDelimiter.
     * @throws NullPointerException if the node is null.
     */
    public BlockMatchRule(Node node) {
        RuleHelper.checkNull(node, "node");

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("node is not a element.");
        }
        // get startDelimiter and endDelimiter config
        // only spaces string is legal here
        startDelimiter = RuleHelper.getNodeTextWithoutTrimEmptyCheck(RuleHelper.getSingleChildElementByName(
            (Element) node, START_DELIMITER_NODE_NAME));
        endDelimiter = RuleHelper.getNodeTextWithoutTrimEmptyCheck(RuleHelper.getSingleChildElementByName(
            (Element) node, END_DELIMITER_NODE_NAME));
    }

    /**
     * <p>
     * This public constructor initializes the startDelimiter and endDelimiter attributes according to the
     * corresponding parameters.
     * </p>
     *
     *
     * @param startDelimiter
     *            A String containing the start of the block of text.
     * @param endDelimiter
     *            A String containing the end of the block of text.
     * @throws IllegalArgumentException
     *             if the length of either startDelimiter or endDelimiter is empty.
     * @throws NullPointerException
     *             if either startDelimiter or endDelimiter is null.
     */
    public BlockMatchRule(String startDelimiter, String endDelimiter) {
        RuleHelper.checkNullEmptyParam(startDelimiter, "startDelimiter");
        RuleHelper.checkNullEmptyParam(endDelimiter, "endDelimiter");

        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
    }

    /**
     * <p>
     * This applies the block matching rule using the HighlightedSequence: hight text of sequence between startDelimiter
     * and endDelimiter inclusively.
     * </p>
     * <p>
     * The sequence may have some breakpoints after previous match removals, startDelimiter
     * and endDelimiter will not match the part contains breakpoints.
     * </p>
     * <p>
     * If a start delimiter is found, but not an end delimiter, then the entire string, starting at the index of
     * the start delimiter, will be highlighted.
     * </p>
     * @param sequence
     *            The HighlightedSequence that is operated on. This may not be null.
     * @param style
     *            The TextStyle used to format any matched sequences. This may be null to signify no
     *            formatting (i.e. default).
     * @throws NullPointerException
     *             if sequence is null.
     *
     */
    public void applyRule(HighlightedSequence sequence, TextStyle style) {
        RuleHelper.checkNull(sequence, "sequence");

        // list save for a sequence of starts and ends
        List starts = new ArrayList();
        List ends = new ArrayList();

        // get the start match
        int startIndex = sequence.indexOf(startDelimiter);

        while (startIndex >= 0) {
            // get the end match
            int endIndex = sequence.indexOf(endDelimiter, startIndex + startDelimiter.length());

            // if no end match , match to the end
            if (endIndex < 0) {
                endIndex = sequence.length();
            } else {
                endIndex += endDelimiter.length();
            }

            // save found start and end to list
            starts.add(new Integer(startIndex));
            ends.add(new Integer(endIndex));
            startIndex = sequence.indexOf(startDelimiter, endIndex);
        }

        // hight the segment defined by start(inclusive) and end(exclusive)
        sequence.highlight(starts, ends, style);
    }
    
    /**
     * <p>
     * Get the current token from sequence.
     * </p>
     * 
     * @return the positon of if matched found, null otherwise. 
     */
    public Point getToken(HighlightedSequence sequence) {
        int startIndex = sequence.indexOf(startDelimiter);
        
        if (startIndex >= 0) {
            int endIndex = sequence.indexOf(endDelimiter, startIndex + startDelimiter.length());
            if (endIndex < 0) {
                endIndex = sequence.length();
            } else {
                endIndex += endDelimiter.length();
            }
            return new Point(startIndex, endIndex);
        }
        
        return null;
    }
}
