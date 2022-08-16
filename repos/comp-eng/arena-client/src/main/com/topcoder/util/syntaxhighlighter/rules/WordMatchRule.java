/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter.rules;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.topcoder.util.syntaxhighlighter.ContentSegment;
import com.topcoder.util.syntaxhighlighter.HighlightedSequence;
import com.topcoder.util.syntaxhighlighter.TextStyle;


/**
 * <p>
 * WordMatchRule defines a simple means of applying a "word search" to the input text. This rule uses
 * HighlightedSequence's getNextToken() method to retrieve tokens and perform a binary search against the list
 * of words. This is fairly efficient for even relatively large inputs (relative to other source files) as on
 * an old machine, this method took roughly 50 ms to match against a well sized list of keywords.
 * </p>
 * <p>Thread Safety: This class only uses immutable state and is thread safe.
 * </p>
 *
 * Changes by WB:
 *   Used HashSet to replace to original binary search word matching.
 *
 * @author duner, still
 * @author WishingBone
 * @version 2.0
 */
public class WordMatchRule implements Rule {
    /**
     * <p>The name for word node.</p>
     */
    private static final String WORD_NODE_NAME = "word";
    /**
     * <p>The name for wordList node.</p>
     */
    private static final String WORDLIST_NODE_NAME = "wordlist";

    /**
     * <p>
     * This represents a set of words to highlight as governed by this Rule. This set cannot and will not ever
     * contain null or empty elements or non-string elements. This is enforced in the constructors.
     * </p>
     *
     */
    private final Set wordList = new HashSet();

    /**
     * <p>
     * Initializes this WordMatchRule instance using the given Node.The provided Node will be an element
     * corresponding to a WordMatchRule (see schema and sample input for details). For instance, the portion this
     * Node represents is represented in the following example XML:
     *
     * <pre>
     *
     *   &lt;wordMatchRule&gt;
     *                  &lt;wordlist&gt;this,public,class,int,double,char &lt;/wordlist&gt;
     *                  &lt;word&gt;protected &lt;/word&gt; '
     *                  &lt;word&gt;private &lt;/word&gt;
     *          &lt;/wordMatchRule&gt;
     *
     * </pre>
     *
     * @param node
     *            An element Node corresponding to a WordMatchRule rule type.It must be non-null.
     * @throws IllegalArgumentException
     *             when config for WordMatchRule is not right, like word is not a valid java identifier.
     * @throws NullPointerException if the node is null.
     */
    public WordMatchRule(Node node) {
        RuleHelper.checkNull(node, "node");
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("node is not a element.");
        }

        // get wordlist and add words split from wordlist string to newWordList
        String[] list = RuleHelper.getNodeText(
                RuleHelper.getSingleChildElementByName((Element) node, WORDLIST_NODE_NAME)).split(",");
        for (int i = 0; i < list.length; ++i) {
            if (!checkAndAddWord(list[i])) {
                throw new IllegalArgumentException(list[i] + " is not a valid word.");
            }
        }

        // get single word
        NodeList wordNodes = ((Element) node).getElementsByTagName(WORD_NODE_NAME);
        for (int i = 0; i < wordNodes.getLength(); ++i) {
            Node wordNode = wordNodes.item(i);
            // is not node's child
            if (wordNode.getParentNode() != node) {
                throw new IllegalArgumentException("some word nodes are not the wordMatchRule's children.");
            }
            // add single word
            if (!checkAndAddWord(RuleHelper.getNodeText(wordNode))) {
                throw new IllegalArgumentException(RuleHelper.getNodeText(wordNode) + " is not a valid word.");
            }
        }
    }

    /**
     * <p>
     * This public constructor initializes wordList attribute from the wordList paramteter.
     *
     * @param list
     *            A String[] containing the list of words to match using this rule.
     * @throws IllegalArgumentException
     *             if the length of the wordList array is less than 1, if any string contained in the wordList
     *             is null or an empty (trimmed) string.
     * @throws NullPointerException
     *             if wordList is null.
     */
    public WordMatchRule(String[] list) {
        RuleHelper.checkNull(list, "wordList");

        if (list.length == 0) {
            throw new IllegalArgumentException("Parameter 'wordList' must contain element(s).");
        }

        // check for each element in list and add it to wordList
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null) {
                throw new IllegalArgumentException("null value in list is illegal.");
            }
            if (!checkAndAddWord(list[i])) {
                throw new IllegalArgumentException(list[i] + " is not a valid word.");
            }
        }
    }

    /**
     * <p>
     * "Highlights" the given HighlightedSequence using the list of words (wordList) and the provided
     * TextStyle. TextStyle may be null to signal that no formatting should take place, but that the words
     * should be extracted from the HighlightedSequence.
     * </p>
     * <p>
     * The sequence may have some breakpoints after previous match removals, the part contains breakpoints
     * will not be matched.
     * </p>
     *
     * @param sequence
     *            The HighlightedSequence that is operated on. This may not be null.
     * @param style
     *            The TextStyle used to format any matched sequences. This may be null to signify no
     *            formatting (i.e. default).
     *
     * @throws NullPointerException
     *             if sequence is null.
     */
    public void applyRule(HighlightedSequence sequence, TextStyle style) {
        RuleHelper.checkNull(sequence, "sequence");

        // list of start and end
        List starts = new ArrayList();
        List ends = new ArrayList();

        // reset to find key-word from header.
        sequence.reset();

        // get next valid java word token
        ContentSegment token = sequence.getNextToken();

        // token is null mean the end of text
        while (token != null) {
            // check if the found java word is key-word in word list
            if (wordList.contains(token.getContent())) {
                starts.add(new Integer(token.getStart()));
                ends.add(new Integer(token.getEnd()));
            }
            token = sequence.getNextToken();
        }

        // highlight the key-word
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
        RuleHelper.checkNull(sequence, "sequence");
        
        ContentSegment token = sequence.getNextToken();

        // token is null mean the end of text
        while (token != null) {
            // check if the found java word is key-word in word list
            if (wordList.contains(token.getContent())) {
                return new Point(token.getStart(), token.getEnd());
            }
            token = sequence.getNextToken();
        }
        return null;
    }

    /**
     * <p>
     * This private helper method first check if word is legal(after trimed), if it is legal, and it to
     * word list. The caller should ensure newWordList and word is non-null;
     * </p>
     * @param word the word to be checked
     * @return whether the word is valid or not
     */
    private boolean checkAndAddWord(String word) {
        word = word.trim();

        // check and add the word
        if (isValidWord(word)) {
            wordList.add(word);
            return true;
        }
        return false;
    }

    /**
     * <p>This privarte method check if word is a valid java word.</p>
     * @param word the to be checked
     * @return Whether the word is valid or not
     */
    private boolean isValidWord(String word) {
        if (word.length() == 0) {
            return false;
        }
        // check first character
        if (!Character.isJavaIdentifierStart(word.charAt(0))) {
            return false;
        }
        // check the left character(s)
        for (int i = 1; i < word.length(); ++i) {
            if (!Character.isJavaIdentifierPart(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
