package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.HTMLCharacterHandler;
import com.topcoder.shared.problem.TextElement;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.TextElement</code>
 * 
 * @author Greg Paul
 * @version $Id: TextElementRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class TextElementRenderer extends BaseRenderer {
    /** Represents the text to be rendered. */
    private TextElement textElement;

    /**
     * Creates a new instance of <code>TextElementRenderer</code>. The text to be rendered is set to be
     * <code>null</code>.
     */
    public TextElementRenderer() {
        this.textElement = null;
    }

    /**
     * Creates a new instance of <code>TextElementRenderer</code>. The text to be rendered is given.
     * 
     * @param textElement the text to be rendered.
     */
    public TextElementRenderer(TextElement textElement) {
        this.textElement = textElement;
    }

    /**
     * Sets the element to be rendered. The given element must be a text.
     * 
     * @param element the text to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a text.
     */
    public void setElement(Element element) {
        if (element instanceof TextElement) {
            textElement = (TextElement) element;
        } else {
            throw new IllegalArgumentException("element must be a TextElement Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. If the text is already escaped, there is no escaping done here.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the text.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        if (textElement == null) {
            throw new IllegalStateException("The text is not set.");
        }

        return textElement.isEscapedText() ? textElement.getEditableText() : BaseRenderer.encodeHTML(textElement
            .getEditableText());
    }

    /**
     * Renders the element into plain text. If the text is already HTML escaped, it will be decoded.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the text.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (textElement == null) {
            throw new IllegalStateException("The text is not set.");
        }

        return textElement.isEscapedText() ? HTMLCharacterHandler.decode(textElement.getEditableText()) : textElement
            .getEditableText();
    }
}
