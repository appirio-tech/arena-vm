package com.topcoder.client.mpsqasApplet.view.defaultimpl.widget;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * A Panel that is a JTextField in a JPanel where the width if the TextField
 * can be specified as a percent of the total panel width.  This TextField
 * is more immune to collapsing to nothing as a result of resizing than
 * a simple JTextField.
 *
 * @author mitalub
 */
public class PanelTextField extends JPanel {

    private JTextField textField;

    /**
     * Constructs a PanelTextField that is equivalent to a text field
     * filling a panel horizontally.
     */
    public PanelTextField() {
        this(1);
    }

    /**
     * Constructs a PanelTextField with a TextField anchored to the left
     * whose width is <code>percentWidth</code> of the total panel.
     * <code>percentWidth</code> must be between 0 and 1.
     */
    public PanelTextField(double percentWidth) {
        this(percentWidth, "");
    }

    /**
     * Constructs a PanelTextField with a TextField anchored to the left
     * whose width is <code>percentWidth</code> of the total panel and whose
     * initial text is <code>text</code>.
     * <code>percentWidth</code> must be between 0 and 1.
     */
    public PanelTextField(double percentWidth, String text) {
        int width = (int) (100 * percentWidth);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        textField = new JTextField(text);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, width, 1);
        layout.setConstraints(textField, gbc);
        add(textField);

        if (percentWidth < 1) {
            JComponent spacer = new JLabel();
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100 - width, 0);
            layout.setConstraints(spacer, gbc);
            add(spacer);
        }
    }

    /**
     * Sets the text in the text field.
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Returns the text in the text field.
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Sets the editableness of the text field.
     */
    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }

    /**
     * Returns the <code>JTextField</code> used, so other methods can be called
     * on it.
     */
    public JTextField getJTextField() {
        return textField;
    }
}
