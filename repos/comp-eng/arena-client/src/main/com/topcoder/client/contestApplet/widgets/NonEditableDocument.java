package com.topcoder.client.contestApplet.widgets;

/**
 * NonEditableDocument.java
 *
 * Description:		Document Model that allows only programatic setting of text
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import javax.swing.text.*;

public class NonEditableDocument extends PlainDocument {

    public NonEditableDocument() {
        this("");
    }

    public NonEditableDocument(String text) {
        super();
        insertString(0, text, null);
    }

    public void setText(String text) {
        try {
            super.remove(0, getLength());
            super.insertString(0, text, null);
        } catch (BadLocationException e) {
        }

    }

    public void insertString(int off, String str, AttributeSet a) {
    }

    public void remove(int off, int len) {
    }
}


/* @(#)NonEditableDocument.java */
