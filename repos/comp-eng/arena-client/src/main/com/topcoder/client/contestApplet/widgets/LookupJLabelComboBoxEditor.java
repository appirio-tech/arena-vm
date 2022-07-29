package com.topcoder.client.contestApplet.widgets;

/**
 * LookupJLabelComboBoxEditor.java
 *
 * Description:		ComboBox editor that will allow search ahead editing
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
//import javax.swing.event.*;
//import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

public class LookupJLabelComboBoxEditor implements ComboBoxEditor, FocusListener {

    protected SpecialTextField editor;
    protected ComboBoxModel model;
    protected JLabel temp = new JLabel();
    protected Color foreground = Common.FG_COLOR;
    protected Color background = Common.BG_COLOR;
    protected Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED, foreground, Common.WPB_COLOR);

    public void setForeground(Color color) {
        foreground = color;
    }

    public void setBackground(Color color) {
        background = color;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public LookupJLabelComboBoxEditor(ComboBoxModel model) {
        this();
        this.model = model;
    }

    public LookupJLabelComboBoxEditor() {
        /// Create everything needed
        editor = new SpecialTextField();
        editor.addFocusListener(this);
    }

    public void setModel(ComboBoxModel model) {
        this.model = model;
    }

    public Component getEditorComponent() {
        return editor;
    }

    public void setItem(Object anObject) {
        // If it's null - default to nothing
        if (anObject == null) {
            editor.setText("");
            editor.setForeground(foreground);
        } else {
            // Get the label and set the text/color to that of the label
            // FYI - do NOT modify or keep a reference to the label
            //       modifying it will modify the dropdown!
            JLabel labelObject = (JLabel) anObject;
            editor.setText(labelObject.getText());
            editor.setForeground(labelObject.getForeground());
        }

        // Reset the uncommitted text
        editor.resetUncommitted();
    }

    public Object getItem() {
        // Commit any pending text
        editor.commitText();

        // Return a new JLabel object
        temp.setText(editor.getText());
        temp.setForeground(editor.getForeground());
        return temp;
    }

    public void selectAll() {
        // Select all
        editor.selectAll();
        editor.requestFocus();
    }

    public void focusGained(FocusEvent e) {
        // Gained focus - select all
        selectAll();
    }

    public void focusLost(FocusEvent e) {
        // Lost focus - commit any pending changes
        editor.commitText();
    }

    public void addActionListener(ActionListener l) {
        // Pass any action listeners on to the editor
        editor.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        // ditto
        editor.removeActionListener(l);
    }

    private class SpecialTextField extends JTextField {

        // StringBuffer will hold the original (unmodified) text
        private StringBuffer originalText = new StringBuffer(50);

        // Uncommitted text is the lookahead text
        private String unCommittedText = "";

        public SpecialTextField() {
            // Setup the text field
            setDocument(new SpecialDocument());
            setBackground(background);
            setForeground(foreground);
            setCaretColor(foreground);
            setBorder(border);
        }


        public final void commitText() {
            // If there is uncommitted text - commit it
            if (!unCommittedText.equals("")) {
                editor.setText(editor.getText() + unCommittedText);
                resetUncommitted();
            }
        }

        public final void resetUncommitted() {
            // Reset the uncommitted text
            unCommittedText = "";
        }

        protected void paintComponent(Graphics g) {
            // Let the JTextField paint itself
            super.paintComponent(g);

            // If there is no unCommitted text, simply return
            if (unCommittedText.equals("")) return;

            // Get the metrics of what was entered
            FontMetrics fm = getFontMetrics(getFont());
            int width = fm.stringWidth(getText());

            try {
                // Find out where to place the uncommitted text
                Rectangle rr = modelToView(0);
                if (rr == null) return;

                // Draw the uncommitted text
                g.setColor(Color.gray);
                g.drawString(unCommittedText, rr.x + width, rr.y + fm.getAscent());

                // Only happens when there is no text - in which case simply ignore it
            } catch (BadLocationException e) {
            }


        }

        private class SpecialDocument extends PlainDocument {

            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                // Overrides the insertstring to our original text buffer then calls updateuncommitted
                // to decide how the field should look
                originalText.insert(offset, str);
                updateUnCommitted();
            }

            public void remove(int offset, int len) throws BadLocationException {
                // Overrides the remove to our original text buffer then calls updateuncommitted
                // to decide how the field should look
                originalText.delete(offset, offset + len);
                updateUnCommitted();
            }

            public void updateUnCommitted() throws BadLocationException {

                // Get the current text
                String currentText = originalText.toString();
                int currentLen = currentText.length();

                // If the text is blank - we're out of here
                if (currentText.equals("")) {
                    resetUncommitted();
                    super.remove(0, getLength());
                    editor.setForeground(foreground);
                    return;
                }

                // Look for the complete spelling of the text
                for (int x = 0; x < model.getSize(); x++) {

                    // Get the object - (may be null if another thread eliminated between the prior statement and now)
                    JLabel modelObject = (JLabel) model.getElementAt(x);
                    if (modelObject == null) continue;

                    // Get the text of the label
                    String maybeText = modelObject.getText();

                    // If the text is less than what we have typed - ignore it
                    if (maybeText.length() < currentLen) continue;

                    // Chop the text into it's parts
                    String maybeBefore = maybeText.substring(0, currentLen);
                    String maybeAfter = maybeText.substring(currentLen);

                    // Is it equal?
                    if (currentText.equalsIgnoreCase(maybeBefore)) {

                        // Replace the current selection with the label text
                        super.remove(0, getLength());
                        super.insertString(0, maybeBefore, null);

                        // Store the uncommitted text
                        unCommittedText = maybeAfter;

                        // Change to the label's color
                        editor.setForeground(modelObject.getForeground());

                        // return
                        return;
                    }

                }

                // Nothing matches

                // Restore the original text
                // Note: needed to restore the case of the original text
                super.remove(0, getLength());
                super.insertString(0, currentText, null);

                // Reset our uncommitted
                resetUncommitted();

                // Set back the color
                editor.setForeground(foreground);
            }
        }

    }

}

