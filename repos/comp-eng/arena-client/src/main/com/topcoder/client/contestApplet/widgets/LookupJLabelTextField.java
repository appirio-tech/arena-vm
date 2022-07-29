/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.widgets;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
//import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 * LookupJLabelTextField.java
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Fix Private Chat Problem version 1.0):
 * <ol>
 *      <li>Add {@link #addCaretHandler()} method.</li>
 *      <li>Add {@link #removeCaretHandler()} method.</li>
 * </ol>
 * </p>
 * Description:     Text field that uses a SortedComboBoxModel as a lookup for type ahead
 * @author Tim "Pops" Roberts (troberts@bigfoot.com), savon_cn
 * @version 1.1
 */

public class LookupJLabelTextField extends JTextField {

    private String unCommitted = "";
    private ComboBoxModel model;
    boolean appendEnding;
    private Color unCommittedColor = Color.gray;

    public LookupJLabelTextField(ComboBoxModel model, boolean appendEnding) {
        this();
        this.model = model;
        this.appendEnding = appendEnding;
    }

    public LookupJLabelTextField() {
        this.appendEnding = true;
        setDocument(new SpecialDocument());
        addKeyListener(new KeyHandler());
        addFocusListener(new FocusHandler());
        addCaretListener(new CaretHandler());
        addMouseListener(new MouseHandler());
        setRequestFocusEnabled(true);
    }
    /**
     * remove the caret listener handler.
     * @since 1.1
     */
    public void removeCaretHandler() {
        CaretListener[] listeners = getCaretListeners();
        if (listeners != null) {
            for(int i=0; i< listeners.length; i++) {
                removeCaretListener(listeners[i]);
            }
        }
    }
    /**
     * add the caret listener handler.
     * @since 1.1
     */
    public void addCaretHandler() {
        CaretListener[] listeners = getCaretListeners();
        if (listeners == null || listeners.length == 0) {
            addCaretListener(new CaretHandler());
        }
    }
    public void setAppendEnding(boolean appendEnding) {
        this.appendEnding = appendEnding;
    }

    public void setModel(ComboBoxModel model) {
        this.model = model;
    }

    /** 
     * This method is deprecated in jdk1.4
     * Do NOT comment this out until only
     * jdk1.4 or greater is supported
     * (then it will be replaced with a
     *  setFocusTraversal call)
     * Pops - 3/20/2003
     */
    public boolean isManagingFocus() {
        // Return true to have tabs passed to the keyhandler
        return true;
    }

    public void setUnCommittedColor(Color unCommittedColor) {
        this.unCommittedColor = unCommittedColor;
    }

    protected void paintComponent(Graphics g) {

        try {
            // Let the JTextField paint itself
            super.paintComponent(g);

            // Ignoer if in selection or no uncommitted
            if (unCommitted.equals("") || getSelectionStart() != getSelectionEnd()) return;

            // Get the metrics
            FontMetrics fm = getFontMetrics(getFont());
            Rectangle rr = modelToView(0);
            Rectangle rt = getBounds();
            if (rr == null || rt == null || fm == null) return;

            // Get the text at the current caret
            String text = getText();
            int[] t = getWordBoundry(text, getCaretPosition() - 1);
            if (t[0] == -1) return;

            // put the text prior to the current word and after
            String textBefore = text.substring(0, t[1]);
            String textAfter = text.substring(t[1]);

            // Calculate the length of the text that has been painted
            int width = fm.stringWidth(textBefore);

            // Blank out everything that was painted after the word
            g.setColor(getBackground());
            g.fillRect(rr.x + width + 1, rr.y, rt.width, rr.y + fm.getAscent());

            // Draw our uncommitted text
            g.setColor(unCommittedColor);
            g.drawString(unCommitted, rr.x + width, rr.y + fm.getAscent());
            width += fm.stringWidth(unCommitted);

            // Draw the color if first word
            if (t[0] == 0) {
                g.drawString(": ", rr.x + width, rr.y + fm.getAscent());
                width += fm.stringWidth(": ");
            }

            // Draw the rest of the text
            g.setColor(getForeground());
            g.drawString(textAfter, rr.x + width, rr.y + fm.getAscent());

            //g.drawString(unCommittedText,rr.x+width,rr.y+fm.getAscent());
            //repaint(rt);
            // Only happens when there is no text - in which case simply ignore it
        } catch (BadLocationException e) {
        }
    }

    private class CaretHandler implements CaretListener {

        public void caretUpdate(CaretEvent e) {

            // Get the text and the current caret position
            String text = getText();
            int pos = e.getDot() - 1;
            if (pos < 0) return;

            // Figure out what our uncommitted text is
            try {
                // If the character at the caret is a space - no uncommitted
                if (text.charAt(pos) == ' ') {
                    unCommitted = "";
                    return;
                }

                // Extract the word
                int[] t = getWordBoundry(text, pos);
                if (t[0] == ' ') {
                    unCommitted = "";
                    return;
                }

                String word = text.substring(t[0], t[1]).toLowerCase();
                String[] labels = convertModelToString();

                // Find the closest match (not an exact match though!)
                for (int x = 0; x < labels.length; x++) {
                    // Get the text (lowercase)
                    String labelText = labels[x].toLowerCase();
                    if (labelText.startsWith(word) && !labelText.equals(word)) {
                        // Set the uncommitted to the label text substring (keeping original case)
                        unCommitted = labels[x].substring(word.length());
                        return;
                    }
                }

                unCommitted = "";
            } finally {
                // Force a repaint since we changed the uncommitted value
                repaint();
            }
        }
    }

    private class MouseHandler extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            repaint();
        }
    }

    private class FocusHandler extends FocusAdapter {

        public void focusGained(FocusEvent e) {
            repaint();
        }

        public void focusLost(FocusEvent e) {
            unCommitted = "";
            repaint();
        }
    }

    private class KeyHandler extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            // If it's a tab (not shift tab) see if we processed it or not
            if (e.getKeyCode() == KeyEvent.VK_TAB && !e.isShiftDown()) {
                useTab();
                e.consume();
                //if(useTab()) e.consume();
            }
        }

        public boolean useTab() {

            // Get the current text
            String text = getText();

            // Find where the word begins/ends
            int[] t = getWordBoundry(text, getCaretPosition() - 1);
            if (t[0] == -1) return false;

            // If nothing to commit - return
            if (unCommitted.equals("")) {
                setCaretPosition(t[1]);
                return false;
            }

            // Get the string to lookfor
            String lookup = text.substring(t[0], t[1]);
            if (lookup.trim().equals("")) return false;

            // Get a copy of it in lowercase
            String lowerLookup = lookup.toLowerCase();

            // Loop through everything to find the user
            StringBuffer newChars = new StringBuffer();
            String[] labels = convertModelToString();
            for (int x = 0; x < labels.length; x++) {

                String matchName = labels[x].toLowerCase();

                // Is the fullname an (case insensitive) extension of our lookup
                if (matchName.startsWith(lowerLookup) && !matchName.equals(lowerLookup)) {

                    // Add the next character to the match
                    char ch = matchName.charAt(lookup.length());
                    lowerLookup += ch;
                    lookup += ch;
                    newChars.append(ch);

                    // Get the last label that starts with the match
                    // (Need last matching label because it's will have
                    //  the greatest [yet matching] difference between the target one)
                    String nextLabel = null;
                    for (int y = x + 1; y < labels.length; y++) {
                        // Is it a match, save it and go for the next one
                        if (labels[y].toLowerCase().startsWith(lowerLookup)) {
                            nextLabel = labels[y];
                        } else {
                            break;
                        }
                    }

                    // Did we find another label that matches
                    if (nextLabel == null) {
                        newChars.append(labels[x].substring(lowerLookup.length()));
                        newChars.append(getEnding(text, t[0], t[1]));
                    } else {
                        // Find all the common characters...
                        String nextName = nextLabel.toLowerCase();
                        for (int y = lookup.length(); ; y++) {
                            if (y >= matchName.length() || y >= nextName.length()) break;

                            if (matchName.charAt(y) != nextName.charAt(y)) break;
                            newChars.append(matchName.charAt(y));
                        }

                    }

                    break;

                }
            }


            // If non match - return false
            if (newChars.length() == 0) {
                setCaretPosition(t[1]);
                return false;
            } else {

                // Get the document
                Document doc = getDocument();
                try {
                    // insert the new characters as if they were typed in
                    doc.insertString(t[1], newChars.toString(), null);

                } catch (BadLocationException e) {
                    System.out.println(e);
                }
                return true;
            }
        }

        private final String getEnding(String text, int start, int end) {
            // If we should appending anything, return nothin.
            if (!appendEnding) return "";

            // If the word is the beginning word - return ": " if not already there
            if (start == 0) {
                if (end >= text.length() || text.charAt(end) != ':') {
                    return ": ";
                }

                // If not, return " " if not already there
            } else {
                if (end >= text.length() || text.charAt(end) != ' ') {
                    return " ";
                }
            }

            return "";
        }

    }

    private class SpecialDocument extends PlainDocument {

        private StringBuffer originalText = new StringBuffer(50);
        private StringBuffer temp = new StringBuffer(50);

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            // Overrides the remove to our original text buffer then calls updateuncommitted
            // to decide how the field should look
            originalText.insert(offset, str);
            update();
            setCaretPosition(offset + str.length());
        }

        public void remove(int offset, int len) throws BadLocationException {
            // Overrides the remove to our original text buffer then calls updateuncommitted
            // to decide how the field should look
            originalText.delete(offset, offset + len);
            update();
            setCaretPosition(offset);
        }

        public void update() throws BadLocationException {

            // Reset the temporary buffer
            temp.setLength(0);

            // Tokenizer our original string buffer
            StringTokenizer tok = new StringTokenizer(originalText.toString(), " :", true);

            // Run through each token
            while (tok.hasMoreTokens()) {
                // Get the token
                String token = tok.nextToken();

                // If it's one of the delimiters - simply add it to the buffer
                if (token.equals(" ") || token.equals(":")) {
                    temp.append(token);
                } else {
                    // See if we have a match in the lookup table
                    // If so, use it's case instead of the type'd in case
                    String[] labels = convertModelToString();
                    for (int x = labels.length - 1; x >= 0; x--) {
                        if (token.equalsIgnoreCase(labels[x])) {
                            token = labels[x];
                            break;
                        }
                    }
                    // Put whatever into the buffer
                    temp.append(token);
                }
            }

            // Overlay the new string
            super.remove(0, getLength());
            super.insertString(0, temp.toString(), null);

        }
    }

    private final int[] getWordBoundry(String text, int pos) {
        // If beyond the length - shorten to the length
        if (pos >= text.length()) pos = text.length() - 1;

        // If below 0 or on a space (no word) return -1
        if (pos < 0 || text.charAt(pos) == ' ') return new int[]{-1, -1};

        // Initialize array
        int[] t = {pos, pos};

        // Figure out the first character
        while (t[0] >= 0 && text.charAt(t[0]) != ' ') t[0]--;
        t[0]++;

        // Figure out the last character
        while (t[1] < text.length() && text.charAt(t[1]) != ' ') t[1]++;

        // Return the array
        return t;
    }

    private final String[] convertModelToString() {
        // Get the Labels from the sorted array
        synchronized(model) {
            String[] s = new String[model.getSize()];

            // Get the text labels for each
            for (int x = model.getSize() - 1; x >= 0; x--) {
                s[x] = ((JLabel) model.getElementAt(x)).getText();
            }

            // Return the array
            return s;
        }
    }
}
