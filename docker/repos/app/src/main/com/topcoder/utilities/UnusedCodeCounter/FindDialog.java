/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

/**
 * FindDialog.java
 *
 * Description:		Allows the user to enter find criteria and options
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.utilities.UnusedCodeCounter;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Allows the user to enter find criteria and options
 *
 * <p>
 * Changes in version 1.0 (Release Assembly - TopCoder Competition Engine Improvement Series 4):
 * <ol>
 *      <li>remove the import depend.</p>
 * </ol>
 * </p>
 * @author Tim "Pops" Roberts, TCSASSEMBLER
 * @version 1.0
 *
 */
public class FindDialog extends KeyAdapter implements ItemListener, ActionListener {

    JFrame frame;
    JDialog dial;
    JTextArea target;
    boolean backwardDir = false;
    boolean ignoreCase = true;
    boolean wrapAround = true;
    boolean closeAfterFind = true;

    JTextField findField = new JTextField(25);
    JCheckBox findOption = new JCheckBox("Find Backwards", backwardDir);
    JCheckBox caseOption = new JCheckBox("Ignore Case", ignoreCase);
    JCheckBox wrapOption = new JCheckBox("Wrap Around", wrapAround);
    JCheckBox closeOption = new JCheckBox("Close After Find", closeAfterFind);
    JButton findButton = new JButton("Find");

    public FindDialog(JFrame panel, JTextArea target) {

        // Save the target
        this.target = target;
        //this.frame	= frame;

        // Find the JFrame
        frame = panel;

        // Create the dialog
        dial = new JDialog(frame, "Find Text", false);

        // Setup the content pane
        Container contentPane = dial.getContentPane();

        // Add listeners
        findOption.addItemListener(this);
        caseOption.addItemListener(this);
        wrapOption.addItemListener(this);
        closeOption.addItemListener(this);
        findButton.addActionListener(this);
        dial.addKeyListener(this);
        findField.addKeyListener(this);

        JLabel findLabel = new JLabel("Find:");

        // Create the FIND box
        Box find = Box.createHorizontalBox();
        find.add(Box.createHorizontalStrut(5));
        find.add(findLabel);
        find.add(Box.createHorizontalStrut(5));
        find.add(findField);
        find.add(Box.createHorizontalStrut(10));
        find.add(findButton);
        find.add(Box.createHorizontalStrut(10));

        // Create the options box
        int spacing = 40;
        Box optionFind = Box.createHorizontalBox();
        optionFind.add(Box.createHorizontalStrut(spacing));
        optionFind.add(findOption);
        optionFind.add(Box.createHorizontalGlue());

        Box optionCase = Box.createHorizontalBox();
        optionCase.add(Box.createHorizontalStrut(spacing));
        optionCase.add(caseOption);
        optionCase.add(Box.createHorizontalGlue());

        Box optionWrap = Box.createHorizontalBox();
        optionWrap.add(Box.createHorizontalStrut(spacing));
        optionWrap.add(wrapOption);
        optionWrap.add(Box.createHorizontalGlue());

        Box optionClose = Box.createHorizontalBox();
        optionClose.add(Box.createHorizontalStrut(spacing));
        optionClose.add(closeOption);
        optionClose.add(Box.createHorizontalGlue());

        // Combine them in all
        Box all = Box.createVerticalBox();
        all.add(Box.createVerticalStrut(15));
        all.add(find);
        all.add(Box.createVerticalStrut(2));
        all.add(optionFind);
        all.add(Box.createVerticalStrut(2));
        all.add(optionCase);
        all.add(Box.createVerticalStrut(2));
        all.add(optionWrap);
        all.add(Box.createVerticalStrut(2));
        all.add(optionClose);
        //all.add(Box.createVerticalStrut(2));

        // Add them to the dialog
        dial.getContentPane().add(all);
        dial.setResizable(false);
        dial.setSize(new Dimension(397, 179));
        dial.pack();

        dial.setLocationRelativeTo(frame);
    }

    public void show() {
        // Show the dialog and bring it to the front
        dial.setVisible(true);
        dial.toFront();
        findField.requestFocus();
        findField.selectAll();
    }

    public void hide() {
        dial.setVisible(false);
    }

    public void findAgain() {
        findButton.doClick();
    }

    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
            findButton.doClick();
            break;

        case KeyEvent.VK_ESCAPE:
            hide();
            break;
        }
    }

    public void itemStateChanged(ItemEvent e) {
        // Decide which check box made it
        Object source = e.getItemSelectable();
        if (source == findOption) {
            backwardDir = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == caseOption) {
            ignoreCase = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == wrapOption) {
            wrapAround = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == closeOption) {
            closeAfterFind = e.getStateChange() == ItemEvent.SELECTED;
        } 
    }

    public void actionPerformed(ActionEvent e) {

        int origPos, fromPos;

        // Get the find text stuff
        String findText = findField.getText();
        int findTextLen = findText.length();

        // Get the text to look in stuff
        String lookText = target.getText();
        int lookTextLen = lookText.length();

        // Decide the direction
        int direction = backwardDir ? -1 : 1;

        // Get the from TO
        origPos = target.getCaretPosition();
        fromPos = origPos + direction;
        //toPos	= (backwardDir ? 0: lookText.length()-1);

        boolean tempWrapAround = wrapAround;

        while (true) {
            // Look for the text
            for (int pos = fromPos; pos >= 0 && pos < lookTextLen; pos += direction) {
                // Does the text match, if so - highlight it
                if (lookText.regionMatches(ignoreCase, pos, findText, 0, findTextLen)) {
                    target.setCaretPosition(pos + findTextLen);
                    target.moveCaretPosition(pos);
                    if (closeAfterFind) hide();
                    return;
                }
            }

            // Check to see if we should wrap around
            if (tempWrapAround) {
                tempWrapAround = false;
                fromPos = backwardDir ? lookTextLen - 1 : 0;
                //toPos	= origPos + direction * -1;
            } else {
                JOptionPane.showMessageDialog(frame, "The search string was not found.", "Find", JOptionPane.WARNING_MESSAGE);
                if (closeAfterFind) hide();
                return;
            }
        }

    }

}
