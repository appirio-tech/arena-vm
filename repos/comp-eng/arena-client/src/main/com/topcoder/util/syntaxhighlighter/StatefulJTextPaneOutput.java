/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;


/**
 * <p>
 * The StatefulJTextPaneOutput class implements the HighlightedOutput interface in order to provide an output plugin
 * that outputs highlighted text to a JTextPane.  This differs from the JTextPaneOutput class in that this is meant to
 * continually update the JTextPane.  As such, it maintains state regarding the JTextPane to determine if it needs to
 * be highlighted and where it should be highlighted from (in "fast matching" mode).  This class completely highlights
 * the JTextPane as necessary and makes it available via getTextPane().
 * </p>
 * <p>
 * There are two ways to highlight using this class: accuracy mode and fast matching mode.  As a result of the decision
 * to use regular expressions, it is hard to accurately highlight the document without highlighting the entire document
 * for each change.  However, highlighting the entire document can be slow and unwieldy.  Fast matching mode is
 * provided to allow matching of the document starting from a point near the insertion, removal or replacement of text.
 * In order to use this, a user should utilize <code>getHighlightString()</code> to obtain the substring needed to
 * highlight.  Internal state is maintained that tells an instance of this class what portion of the document is being
 * highlighted.  It is extremely important that highlighting take place subsequent to a call to this method in order
 * to maintain reliability of the internal state.  If highlighting does not or cannot take place,
 * <code>resetState()</code> should be called prior to any other method call.  This will ensure that the state is
 * reset from the beginning and highlighting will occur starting from the beginning of the document.  Failure to do
 * one or the other will result in possible corruption of state and mishighlighting.
 * </p>
 * <p>
 * Note: Accuracy mode is utilized simply by giving the highlighter the entire content of the Document and not using
 * <code>getHighlightString()</code>.  Use of <code>getHighlightString()</code> necessarily implies "fast matching
 * mode."  Fast matching mode may not be reliable for complex regular expressions that may need to be completely
 * reevaluated.  Care should be exercised when using this mode.
 * </p>
 * <p>
 * Since this class modifies the underlying document, it is assumed that no changes to the document are made
 * while it's being highlighted (i.e. a write lock is held on the Document).
 * </p>
 * <p>
 * Secondary Condition: The "fast match" mode assumes that a ContentSegment with a null TextStyle is one that was
 * not highlighted by the highlighter.  If using "fast match" mode, it would most likely to be desirable to ensure
 * this is true, though it's not necessary.
 * </p>
 * <p>
 * Thread Safety:
 * This class is not thread safe.
 * </p>
 *
 * Changes by WB:
 *   Added more getHighlightString() methods to facilitate update window.
 *
 * @author still, duner
 * @author WishingBone
 * @version 2.0
 */
public class StatefulJTextPaneOutput implements HighlightedOutput {
    /**
     * <p>
     * Represents the JTextPane that the highlighted output will be directed to. This is provided by the user
     * in the constructor. This is guaranteed to be non-null. Because of the nature of this component, this value
     * is shared between the calling application and this class. The user of the component should take care to
     * understand the consequences of this.
     * </p>
     */
    private final JTextPane textPane;

    /**
     * <p>
     * Represents the String content of the pane that is being highlighted by the highlighter.  The highlighter will
     * call needsUpdate().  If needsUpdate() returns true (if the string to be highlighted is different from
     * paneContent), then paneContent will be updated with that String.  This is initially an empty string (which
     * will never match the highlighter provided text or, if it ever does, will not matter since it's empty).
     * </p>
     */
    private String paneContent = "";

    /**
     * <p>
     * Represents the language that was last highlighted by the highlighter.  The usage is similar to paneContent,
     * however, an automatic update will be precipitated by a language changed regardless of paneContent.  This is
     * initially empty to indicate no language.
     * </p>
     */
    private String paneLanguage = "";

    /**
     * <p>
     * This is part of the "fast matching" strategy used to speed up highlighting on large documents that are being
     * edited near the end of the document.  This stores a List of ContentSegments from the previous highlighting
     * attempt.  A subsequent highlighting attempt (in "fast matching" mode) will check this array to determine where
     * it can safely restart the highlighting from.  This safe point is basically the last highlighted segment (one
     * with a non-null style) that occurs prior to the change in the document.  This is initially empty to indicate
     * no prior segments.
     * </p>
     */
    private List previousSegments = new ArrayList();

    /**
     * <p>
     * This is part of the "fast matching" strategy (see above for details).  This stores the last "safe" segment.
     * Since the process is disconnected, it is necessary to store this for later use when the highlighted invokes
     * the setText method.  It is important to note that because of this field and the others that relate to fast
     * matching, this class is particularly sensitive to threading issues (i.e. this class is not thread safe).
     * An initial value of -1 signals that there is no current segment and that "fast matching" mode is disabled.
     * </p>
     */
    private int curSegment = -1;

    /**
     * <p>
     * This is part of the "fast matching" strategy (see above for details).  If the sequence of conditions
     * necessary to maintain "fast matching" mode are not met (i.e. getHighlightString() is not followed by
     * a call to setText() by the SyntaxHighlighter or other external component), the internal state can become
     * corrupted.  This keeps track of whether the current segment referenced has been highlighted.  If it has not,
     * and another call to getHighlightString() is made, then resetState() is called.
     * </p>
     * <p>
     * Note: This does not solve any internal state issues if the original string retrieved from getHighlightString()
     * is highlighted. In fact, this could compound any potential problems.  The user should be advised to tread
     * carefully and ensure the conditions required by "fast matching" mode are met.
     * </p>
     */
    private boolean isCurHighlighted = false;

    /**
     * Represents whether the real highlighting should be done. <code>true</code> the real highlighting should be
     * done, otherwise we need only update the starting point, the stored segments in
     * <code>StatefulJTextPaneOutput</code> class under fast mode.  
     * 
     * @since new Added
     */
    private boolean update = true;
    
    /**
     * Sets the signal to see whether the real highlighting should be done.
     * 
     * @param update whether the real highlighting should be done.
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }
    /**
     * <p>
     * Initializes the textPane attribute with the textPane parameter.
     * </p>
     *
     *
     * @param textPane
     *            A JTextPane instance to send the output to. Must not be null.
     * @throws NullPointerException
     *             if textComponent is null.
     */
    public StatefulJTextPaneOutput(JTextPane textPane) {
        SHHelper.checkNull(textPane, "textPane");
        this.textPane = textPane;
    }

    /**
     * <p>
     * Indicates whether the internal state is currently in "fast match" mode.  This will return
     * false prior to the first call to setText() since the entire string will have to be matched
     * since there are no prior segments to rely on.
     * </p>
     *
     * @return true if the internal state is consistent with "fast match" mode, false otherwise.
     */
    private boolean isInFastMode() {
        if (curSegment > -1 && !previousSegments.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * This method uses the given contentSegments as a positioning guide and appropriately styles each segment
     * to the textPane.
     * </p>
     *
     * @param contentSegments
     *            An array of ContentSegments instances use to form the document. This should be ordered
     *            appropriately such that the document can be created simply be iterating the array.
     * @throws NullPointerException
     *             if contentSegment is null.
     * @throws HighlightingException
     *             if an error occurs during the highlighting process.
     * @throws IllegalArgumentException
     *             if any elements of contentSegment are null or no elements contained.
     */
    public void setText(ContentSegment[] contentSegments)
        throws HighlightingException {
        SHHelper.checkNull(contentSegments, "contentSegments");
        if (contentSegments.length == 0) {
            throw new IllegalArgumentException("contentSegments should not contain no elements.");
        }
        // always here contentSegments is sorted by HighlightedSequence.getOrderedSegments
        // however sort it here make this method always do the right thing.
        try {
            Arrays.sort(contentSegments);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Some elements of contentSegments are of wrong type.");
        }

        StyledDocument doc = textPane.getStyledDocument();
        int documentLength = doc.getLength();

        // calculate offset if we're using "fast" highlighting mode
        int offset = 0;
        if (isInFastMode()) {
            offset = ((ContentSegment) previousSegments.get(curSegment)).getStart();
        }

        for (int i = 0; i < contentSegments.length; ++i) {
            if (contentSegments[i] == null) {
                throw new IllegalArgumentException(
                    "Some elements of contentSegments are null or illegal.");
            }

            // check the segment bounds.
            if (contentSegments[i].getStart() + offset + contentSegments[i].getContent().length() > documentLength) {
                throw new HighlightingException("A referenced segment is outside the bounds of the document. "
                                                + "The document should not be changed while highlighting occurs.");
            }
        }

        // if the update is false, the real highlighting should not be done. Otherwise it will the undoManager
        // in StandardEditorPanel go out of sync
        for (int i = 0; i < contentSegments.length && update; ++i) {
            ContentSegment seg = contentSegments[i];

            MutableAttributeSet attrSet = new SimpleAttributeSet();
            TextStyle style = seg.getStyle();
            // set attrSet the right style
            if (style != null) {
                if (style.getBGColor() != null) {
                    StyleConstants.setBackground(attrSet, style.getBGColor());
                }

                if (style.getFont() != null) {
                    Font font = style.getFont();
                    StyleConstants.setFontFamily(attrSet, font.getFamily());
                    StyleConstants.setFontSize(attrSet, font.getSize());
                    StyleConstants.setBold(attrSet, font.isBold());
                    StyleConstants.setItalic(attrSet, font.isItalic());
                }

                if (style.getColor() != null) {
                    StyleConstants.setForeground(attrSet, style.getColor());
                }
            }

            // style the necessary segment
            doc.setCharacterAttributes(seg.getStart() + offset, seg.getContent().length(), attrSet, true);
        }

        isCurHighlighted = true;

        // create segments.
        createFastSegments(contentSegments, offset);
    }

    /**
     * <p>
     * Private helper method which aids in forming a full List of segments for "fast matching" mode.  If fast
     * matching mode is not being used, then it simply stores all elements of contentSegments into
     * previousSegments.
     * </p>
     *
     * @param contentSegments the segments used for the highlighting process that should be stored.  Should not
     * be null.
     * @param offset the offset at which the segmenting starts.  Should be greater than or equal to zero.
     *
     */
    private void createFastSegments(ContentSegment[] contentSegments, int offset) {
        // if not in "fast" mode or if there are no currently stored segments, store the entire contentSegments array.
        // otherwise, replace the contents of previousSegments starting at curSegment.
        if (!isInFastMode()) {
            previousSegments.clear();
            previousSegments.addAll(Arrays.asList(contentSegments));
        } else {
            previousSegments.subList(curSegment, previousSegments.size()).clear();

            for (int i = 0; i < contentSegments.length; i++) {
                ContentSegment seg = contentSegments[i];
                previousSegments.add(new ContentSegment(seg.getStart() + offset, seg.getEnd() + offset,
                    seg.getStyle(), seg.getContent()));
            }
        }
    }

    /**
     * <p>
     * Resets the state of "fast" matching mode.  This is essentially used to disable "fast" mode and use
     * "accuracy" mode instead. More specifically, after a call to getHighlightString(), this class will
     * be in "fast" mode.  In order to end this mode, a call to resetState() is required.
     * </p>
     * <p>
     * Note: Calling this method does not solve any internal state issues if the original string retrieved from
     * getHighlightString() is eventually highlighted.  In fact, this could compound any potential problems.
     * The user is advised to tread carefully and ensure the conditions required by "fast matching" mode are met
     * (specifically, that any call to getHighlightString() results in immediate highlighting or in a call to
     * resetState() and that the original highlight string is not highlighted).
     * </p>
     */
    public void resetState() {
        curSegment = -1;
        previousSegments.clear();
        isCurHighlighted = false;
    }

    /**
     * <p>
     * Returns a string that represents the "best guess" for the string that should be highlighted given a starting
     * position of a change in the document.  This is part of the "fast mode" that highlights only a portion of the
     * input document rather than the entire document for the sake of speed.  This method should be called directly
     * in tandem with an attempt to highlight since internal state is maintained once this method is called.  If
     * "fast mode" is not desired, a call to resetState() will "undo" a call to this method.
     * </p>
     * <p>
     * The "safe" point is chosen as the point before the previously highlighted segment because the assumption
     * is it's the only segment that would likely be affected by a change to a point past it.  For instance, in
     * the case of a partial block match (one without an end delimiter), we need to reevaluate whether or not
     * the block was closed or whether it still remains open.  A previously highlighted token will only change
     * if something was added to it (meaning tokens before it will not be affected).  The only potential pitfall
     * is with regular expression (pattern) matching.  A regular expression may not have matched earlier but may
     * match now, however, this may not be fully reevaluated.  There is really no way around this except to
     * create rules that do not expose this behavior (i.e. for block comments use a block match rule rather
     * than a PatternMatchRule).  The multi-pass nature of the Highlighter makes it unsuitable for highlighting
     * text on the fly, so this attempts to mitigate speed issues by sacrificing some potential accuracy for
     * speed.
     * </p>
     * <p>
     * Note: The nature of this method and its role in maintaining internal state is necessary because of the
     * structure of the highlighter.  Since the selection of the string to highlight and the actual highlighting
     * of the string is disconnected, care must be taken to honor the conditions set forth above.  This method
     * attempts to determine whether the previously retrieved string in "fast matching" mode was actually
     * highlighted.  If it was not, it attempts to gracefully recover by calling resetState() itself.  However,
     * any string retrieved from this method should no longer be used since it may no longer be correct.
     * </p>
     *
     * @param position the position the insertion, removal or replace took place at.  This should be >= 0.  There is
     * no upper bound on the value.  If position is greater than the document size, the end of the document will
     * be assumed.
     *
     * @return a String representing a partial string contained by the JTextPane which should be highlighted.
     *
     * @throws IllegalArgumentException if position is less than zero.
     * @throws BadLocationException if the calculated position is inconsistent with the document's state at the
     * time of return.
     */
    public String getHighlightString(int position) throws BadLocationException {
        // Simply delegate
        return getHighlightString(position, 0, 0, 0, 0);
    }

    /**
     * <p>
     * Returns a string that represents the "best guess" for the string that should be highlighted given a starting
     * position of a change in the document.
     *</p>
     *
     * @param position the position the insertion, removal or replace took place at.  This should be >= 0.  There is
     * no upper bound on the value.  If position is greater than the document size, the end of the document will
     * be assumed.
     * @param remove the length of the content that's being actively removed.
     * @param insert the length of the content that's being actively inserted.
     * @param lines the number of lines in the update window. Could be 0 when it means update window is not used
     * (change proceeds to the end of the file).
     *
     * @return a String representing a partial string contained by the JTextPane which should be highlighted.
     *
     * @throws IllegalArgumentException if position, length or lines is less than zero.
     * @throws BadLocationException if the calculated position is inconsistent with the document's state at the
     * time of return.
     */
    public String getHighlightString(int position, int remove, int insert, int lines) throws BadLocationException {
        // Simply delegate
        return getHighlightString(position, remove, insert, lines, 0);
    }

    /**
     * <p>
     * Returns a string that represents the "best guess" for the string that should be highlighted given a starting
     * position of a change in the document.
     *</p>
     *
     * @param position the position the insertion, removal or replace took place at.  This should be >= 0.  There is
     * no upper bound on the value.  If position is greater than the document size, the end of the document will
     * be assumed.
     * @param remove the length of the content that's being actively removed.
     * @param insert the length of the content that's being actively inserted.
     * @param lines the number of lines in the update window. Could be 0 when it means update window is not used
     * (change proceeds to the end of the file).
     * @param columns the number of columns per line. Could be 0 when it means update window does not column or
     * does not wrap.
     *
     * @return a String representing a partial string contained by the JTextPane which should be highlighted.
     *
     * @throws IllegalArgumentException if position, length, lines or columns is less than zero.
     * @throws BadLocationException if the calculated position is inconsistent with the document's state at the
     * time of return.
     */
    public String getHighlightString(int position, int remove, int insert, int lines, int columns)
            throws BadLocationException {
        if (position < 0) {
            throw new IllegalArgumentException("position should be greater than or equal to zero.");
        }
        if (remove < 0) {
            throw new IllegalArgumentException("remove should be greater than or equal to zero.");
        }
        if (insert < 0) {
            throw new IllegalArgumentException("insert should be greater than or equal to zero.");
        }
        if (lines < 0) {
            throw new IllegalArgumentException("lines should be greater than or equal to zero.");
        }
        if (columns < 0) {
            throw new IllegalArgumentException("columns should be greater than or equal to zero.");
        }

        if (!isCurHighlighted) {
            resetState();
        }

        curSegment = 0;

        // switching from "accuracy" to "fast" mode or the position is the beginning of the document.
        if (previousSegments.isEmpty()) {
            return textPane.getDocument().getText(0, textPane.getDocument().getLength());
        }

        // determine a "safe" starting point.  This is:
        // 1. A segment that occurs prior to the position that was changed.
        // 2. A segment that has already been highlighted (in case the change affects that segment).
        // Note: There is no such thing as a truly "safe" starting point given the semantics of the highlighter.
        //       The "fast mode" is not 100% accurate.
        // The docs for this method contain more detail on why the safe point is chosen.
        for (int i = 1; i < previousSegments.size(); ++i) {
            ContentSegment seg = (ContentSegment) previousSegments.get(i);
            if (seg.getStart() >= position) {
                break;
            }
            if (seg.getStyle() != null) {
                curSegment = i - 1;
            }
        }
        int start = ((ContentSegment) previousSegments.get(curSegment)).getStart();
        String text = textPane.getDocument().getText(start, textPane.getDocument().getLength() - start);

        // If update window is not enabled, all the text needs to be updated.
        if (lines == 0) {
            return text;
        }

        // Locate the target position after which the update does not proceed.
        int targetPosition = position + insert;
        int currentColumn = 0;
        int lastIndexedPosition =
                ((ContentSegment) previousSegments.get(previousSegments.size() - 1)).getEnd() + 1 + insert - remove;

        // If no change is made, then we are good so far.
        if (insert == 0 && remove == 0 && start + text.length() <= lastIndexedPosition) {
            return null;
        }

        int line = lines;
        while (targetPosition - start < text.length()) {
            char ch = text.charAt(targetPosition - start);
            if (ch == '\n') {
                --line;
                currentColumn = 0;
            } else if (columns > 0 && Character.isJavaIdentifierPart(ch)) {
                ++currentColumn;
                if (currentColumn > columns) {
                    --line;
                    currentColumn -= columns;
                }
            }
            if (line <= 0) {
                // If no change is made, then we are good so far.
                if (insert == 0 && remove == 0 && targetPosition <= lastIndexedPosition) {
                    return null;
                }
                // Otherwise go for another extra screen for buffering.
                if (line <= -lines) {
                    break;
                }
            }
            ++targetPosition;
        }

        return text.substring(0, Math.min(targetPosition - start, text.length()));
    }

    /**
     * <p>
     * Signals whether or not the highlighter should highlight the string given to it given the current state of this
     * HighlightedOutput subclass.  This occurs under the following conditions:
     *
     * 1. The highlighting is in "fast mode."  OR
     * 2. The previously highlighted text differs from this one.  OR
     * 3. The previously highlighted language differs from this one.
     * </p>
     *
     * @param text The text that SyntaxHighlighter is trying to highlight.
     * @param language The language whose rules are being used to highlight text.
     *
     * @return true if the JTextPane should be updated given the text to highlight in the given language.
     *
     * @throws NullPointerException if either text or language are null.
     * @throws IllegalArgumentException if either text or language is a null, trimmed string.
     */
    public boolean needsUpdate(String text, String language) {
        SHHelper.checkString(text, "text");
        SHHelper.checkString(language, "language");

        // automatically update if we're using "fast match" mode.  If we're using this mode, then matching against
        // text is unreliable since text represents a subset of the actual content.
        if (curSegment > -1) {
            return true;
        }

        // update only if the pane content or language has changed.  A language change means the syntax
        // may be different, so we must update/redo the highlighting.
        if (paneContent.equals(text) && paneLanguage.equals(language)) {
            return false;
        }

        // update the text content and the language for next time.
        paneContent = text;
        paneLanguage = language;

        return true;
    }

    /**
     * <p>
     * This returns the textPane which is used for display the result.
     * </p>
     *
     *
     * @return the textPane attribute.
     */
    public JTextPane getJTextPane() {
        return textPane;
    }
}
