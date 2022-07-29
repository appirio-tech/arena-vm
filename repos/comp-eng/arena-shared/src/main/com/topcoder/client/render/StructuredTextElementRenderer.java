/*
 * StructuredTextElementRenderer Created 06/13/2006
 */
package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.HTMLCharacterHandler;
import com.topcoder.shared.problem.StructuredTextElement;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.StructuredTextElement</code>. Compared to normal text,
 * structured text may contain HTML tags and HTML escapes.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: StructuredTextElementRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class StructuredTextElementRenderer extends BaseRenderer {
    /** Represents the structured text to be rendered. */
    private StructuredTextElement element;

    /**
     * Creates a new instance of <code>StructuredTextElementRenderer</code>. The text to be rendered is set to be
     * <code>null</code>.
     */
    public StructuredTextElementRenderer() {
        this.element = null;
    }

    /**
     * Creates a new instance of <code>StructuredTextElementRenderer</code>. The text to be rendered is given.
     * 
     * @param element the structured text to be rendered.
     */
    public StructuredTextElementRenderer(StructuredTextElement element) {
        this.element = element;
    }

    /**
     * Sets the element to be rendered. The given element must be a structured text.
     * 
     * @param element the structured text to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a structured text.
     */
    public void setElement(Element element) throws Exception {
        if (element instanceof StructuredTextElement) {
            this.element = (StructuredTextElement) element;
        } else {
            throw new IllegalArgumentException("element must be a StructuredTextElement Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. In this case, there is no escaping since the structured text
     * is expected to be HTML content.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the structured text.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        if (element == null) {
            throw new IllegalStateException("The structured text is not set.");
        }

        return element.getInnerXmlText();
    }

    /**
     * Renders the element into plain text. It only decodes HTML escapes, while leaving all HTML tags untouched.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the structured text.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        return HTMLCharacterHandler.decode(toHTML(language));
    }
}
