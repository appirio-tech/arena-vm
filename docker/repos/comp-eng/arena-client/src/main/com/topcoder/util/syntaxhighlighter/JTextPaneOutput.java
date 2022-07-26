/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.awt.Font;
import java.util.Arrays;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 * <p>
 * The JTextPaneOutput class implements the HighlightedOutput interface in order to provide an output plugin
 * that outputs highlighted text to a JTextPane. This class does not actually display the JTextPane in order
 * to provide more flexibility to the caller.  This class completely highlights the JTextPane as necessary
 * and makes it available via getTextPane().</p>
 * <p>
 * Thread Safety:
 * This class is not thread safe.
 * </p>
 * @author duner, still
 * @version 2.0
 */
public class JTextPaneOutput implements HighlightedOutput {
    /**
     * <p>
     * Represents the JTextPane that the highlighted output will be directed to. This is either constructed by
     * this class' constructor or provided by the user in the other constructor. This is guaranteed to be
     * non-null. Because of the nature of this component, this value may be shared between the calling
     * application. The user of the application should understand the consequences of this.
     * </p>
     *
     */
    private final JTextPane textPane;

    /**
     * <p>
     * Simply initializes the textComponent attribute to a valid JTextPane instance. There are no restrictions
     * on how it should be configured.
     * </p>
     *
     */
    public JTextPaneOutput() {
        textPane = new JTextPane();
    }

    /**
     * <p>
     * Initializes the textComponent attribute with the textComponent parameter.
     * </p>
     *
     *
     * @param textPane
     *            A JTextPane instance to send the output to. Must not be null.
     * @throws NullPointerException
     *             if textComponent is null.
     */
    public JTextPaneOutput(JTextPane textPane) {
        SHHelper.checkNull(textPane, "textPane");
        this.textPane = textPane;
    }

    /**
     * <p>
     * This method makes the given contentSegments and appropriately style each segment to the textPane.
     *
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

        Document doc = textPane.getDocument();

        try {
            // clear the current content of textpane
            doc.remove(0, doc.getLength());

            for (int i = 0; i < contentSegments.length; ++i) {
                if (contentSegments[i] == null) {
                    throw new IllegalArgumentException(
                        "Some elements of contentSegments are null or illegal.");
                }

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
                // insert the styled segment content
                doc.insertString(seg.getStart(), seg.getContent(), attrSet);
            }
        } catch (BadLocationException e) {
            throw new HighlightingException("contentSegments have bad location segments.");
        }
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
