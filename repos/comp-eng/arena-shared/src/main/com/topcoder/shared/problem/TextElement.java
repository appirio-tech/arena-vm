package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * The simplest element is a <code>TextElement</code>, which corresponds to <code>CDATA</code> (plain, unstructured
 * text).
 * 
 * @see Element
 * @author Logan Hanks
 * @version $Id: TextElement.java 71771 2008-07-18 05:34:07Z qliu $
 */
public class TextElement extends BaseElement {
    /** Represents the text held by the instance. */
    private String text = "";

    /** Represents a flag indicating if the text is already HTML-escaped. */
    private boolean escapedText = false;

    /**
     * Creates a new instance of <code>TextElement</code>. It is required by custom serialization.
     */
    public TextElement() {
    }

    /**
     * Creates a new instance of <code>TextElement</code>. The text is given. The given text is not properly
     * HTML-escaped.
     * 
     * @param text the text to be held by the instance.
     */
    public TextElement(String text) {
        this.text = text;
    }

    /**
     * Creates a new instance of <code>TextElement</code>. The text is given. The flag indicating if the text is
     * already HTML-escaped is also given.
     * 
     * @param escapedText a flag indicating if the text is already HTML-escaped.
     * @param text the text to be held by the instance.
     */
    public TextElement(boolean escapedText, String text) {
        this.escapedText = escapedText;
        this.text = text;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(text);
        writer.writeBoolean(escapedText);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        text = reader.readString();
        escapedText = reader.readBoolean();
    }

    /**
     * Gets the text held by this instance.
     * 
     * @return the text.
     */
    public String getEditableText() {
        return text;
    }

    /**
     * Sets the text held by this instance.
     * 
     * @param text the text.
     */
    public void setEditableText(String text) {
        this.text = text;
    }

    public String toXML() {
        return ProblemComponent.encodeHTML(text);
    }

    public String toString() {
        if (escapedText) {
            return text;
        }
        return ProblemComponent.encodeHTML(text);
    }

    /**
     * Gets a flag indicating if the text is already HTML-escaped.
     * 
     * @return <code>true</code> if the text is HTML-escaped; <code>false</code> otherwise.
     */
    public boolean isEscapedText() {
        return escapedText;
    }

    /**
     * Sets a flag indicating if the text is already HTML-escaped.
     * 
     * @param escapedText a flag indicating if the text is already HTML-escaped.
     */
    public void setEscapedText(boolean escapedText) {
        this.escapedText = escapedText;
    }

}
