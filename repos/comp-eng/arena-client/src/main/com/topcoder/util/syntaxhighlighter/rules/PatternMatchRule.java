/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter.rules;

import com.topcoder.util.syntaxhighlighter.HighlightedSequence;
import com.topcoder.util.syntaxhighlighter.RuleMatchException;
import com.topcoder.util.syntaxhighlighter.TextStyle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * <p>
 * PatternMatchRule defines a simple means of applying a regular expression pattern to the input text. This rule
 * simply utilizes the Java Pattern and Matcher classes in order to find matches within the input string.
 * </p>
 * <p>
 * NOTE: Don't use PatternMatchRule(Node) to create new PatternMatchRule, it is designed for internal call only.
 * </p>
 * <p>
 * Thread Safety: This class only uses immutable state and is thread safe.
 * </p>
 *
 * Changes by WB:
 * - Stored compiled version of the regular expression.
 * - Used DOTALL option in compilation.
 *
 * @author duner, still
 * @author WishingBone
 * @version 2.0
 */
public class PatternMatchRule implements Rule {
    /**
     * <p>The name for pattern node.</p>
     */
    private static final String PATTERN_NODE_NAME = "pattern";

    /**
     * <p>
     * This represents the regular expression pattern used to highlight as governed by this Rule. This is set in
     * either constructor to a non-null Pattern instance. It is immutable once set.
     * </p>
     *
     */
    private final Pattern pattern;

    /**
     * <p>
     * Initializes this PatternMatchRule instance using the given Node. This is marked package private to allow
     * instantiation internally (so we can ensure a validated document). The provided Node will be an element
     * corresponding to a PatterMatchRule (see schema and sample input for details). For instance, the portion this
     * Node represents is represented in the following example XML:
     *
     * <pre>
     *
     *   &lt;patternMatchRule&gt;
     *                   &lt;pattern&gt;(?s)\/\*(.)*?\*\/ &lt;/pattern&gt;
     *   &lt;/patternMatchRule&gt;
     *
     * </pre>
     *
     * <p>
     * NOTE: Don't use this constructor to create new PatternMatchRule, it is designed for internal call only.
     * </p>
     *
     * @param node
     *            An element Node corresponding to a WordMatchRule rule type.It must be non-null.
     * @throws IllegalArgumentException
     *             when config is not right
     */
    public PatternMatchRule(Node node) {
        RuleHelper.checkNull(node, "node");
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("node is not a element.");
        }
        String regex = RuleHelper.getNodeTextWithoutTrimEmptyCheck(RuleHelper.getSingleChildElementByName(
                (Element) node, PATTERN_NODE_NAME));
        try {
            this.pattern = Pattern.compile(regex, Pattern.DOTALL);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Problems occurs in pattern's compile.");
        }

    }

    /**
     * <p>
     * This public constructor initializes the pattern attribute to the pattern parameter.
     *
     *
     * @param pattern
     *            A String containing the pattern to match using this rule.
     * @throws IllegalArgumentException
     *             if the length of pattern is less than 1.
     * @throws NullPointerException
     *             if pattern is null.
     */
    public PatternMatchRule(String pattern) {
        RuleHelper.checkNull(pattern, "pattern");

        if (pattern.length() == 0) {
            throw new IllegalArgumentException("Paramteter 'pattern' should not be empty.");
        }

        try {
            this.pattern = Pattern.compile(pattern, Pattern.DOTALL);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Problems occurs in pattern's compile.");
        }
    }

    /**
     * <p>
     * This applies the pattern matching rule using the Pattern and Matcher classes. The pattern is the pattern
     * attribute and the String to match against is the HighlightedSequence.
     *
     * @param sequence
     *            The HighlightedSequence that is operated on. This may not be null.
     * @param style
     *            The TextStyle used to format any matched sequences. This may be null to signify no formatting
     *            (i.e. default).
     * @throws NullPointerException
     *             if sequence is null.
     * @throws RuleMatchException
     *             if the rule is unable to be applied (specificially, if there is a problem with the pattern).
     */
    public void applyRule(HighlightedSequence sequence, TextStyle style) throws RuleMatchException {
        RuleHelper.checkNull(sequence, "sequence");

        Matcher matcher = pattern.matcher(sequence);

        // matched start and end list
        List starts = new ArrayList();
        List ends = new ArrayList();

        while (matcher.find()) {
            // find a match
            starts.add(new Integer(matcher.start()));
            ends.add(new Integer(matcher.end()));
        }

        // highlight matched segments
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
        Matcher matcher = pattern.matcher(sequence);
        return matcher.find() ? new Point(matcher.start(), matcher.end()) : null;
    }
}