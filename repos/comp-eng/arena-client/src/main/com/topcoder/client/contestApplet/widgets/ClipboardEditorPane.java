package com.topcoder.client.contestApplet.widgets;

import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;

public class ClipboardEditorPane extends JEditorPane {
    private boolean clipboardEnabled = true;

    public void setClipboardEnabled(boolean on) {
        clipboardEnabled = on;
    }

    public boolean isClipboardEnabled() {
        return clipboardEnabled;
    }

    public ClipboardEditorPane() {
    }

    public ClipboardEditorPane(String url) throws IOException {
        super(url);
    }

    public ClipboardEditorPane(String type, String text) {
        super(type, text);
    }

    public ClipboardEditorPane(URL initialPage) throws IOException {
        super(initialPage);
    }

    public void copy() {
        if (clipboardEnabled) {
            super.copy();
        }
    }

    public void cut() {
        if (clipboardEnabled) {
            super.cut();
        }
    }

    public void paste() {
        if (clipboardEnabled) {
            super.paste();
        }
    }

    public void replaceSelection(String n) {
        if (clipboardEnabled) {
            super.replaceSelection(n);
        }
    }

    public String getSelectedText() {
        return clipboardEnabled ? super.getSelectedText() : "";
    }

    public void setSelectionStart(int s) {
        if (clipboardEnabled) {
            super.setSelectionStart(s);
        }
    }

    public void setSelectionEnd(int e) {
        if (clipboardEnabled) {
            super.setSelectionEnd(e);
        }
    }

    public void select(int s, int e) {
        if (clipboardEnabled) {
            super.select(s, e);
        }
    }

    public void selectAll() {
        if (clipboardEnabled) {
            super.selectAll();
        }
    }
}
