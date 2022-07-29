/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

/**
 * GotoAction.java
 *
 * Description:		Called when a goto action is done
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.utilities.UnusedCodeCounter;

import com.topcoder.client.launcher.common.Utility;

/**
 * 
 * Called when a goto action is done.
 *
 * <p>
 * Changes in version 1.0 (Release Assembly - TopCoder Competition Engine Improvement Series 4):
 * <ol>
 *      <li>Update {@link #actionPerformed(java.awt.event.ActionEvent e)} method.</li>
 * </ol>
 * </p>
 * @author Tim "Pops" Roberts, TCSASSEMBLER
 * @version 1.0
 *
 */
public class GotoAction extends javax.swing.AbstractAction {

    UnusedCode parent;

    public GotoAction(UnusedCode parent) {
        this.parent = parent;
    }
    /**
     * the component action performed.
     * @param e the awt event.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Get the text area
        javax.swing.JTextArea textArea = parent.getTextArea();

        // Ask for what line to goto
        String ss = Utility.input("Goto Line", "Enter the line number:", parent);
        if (ss == null) {
            return;
        }

        try {
            // Get the line count
            int lines = textArea.getLineCount();

            // Parse the number entered and goto it
            int num = Integer.parseInt(ss);
            if (num < 0 || num > lines) {
                Utility.showMessage("Goto Line", "There are only " + lines + " lines.", parent);
            } else {
                textArea.setCaretPosition(textArea.getLineStartOffset(num - 1));
                textArea.requestFocus();
            }
        } catch (Exception ex) {
            Utility.showMessage("Goto Line", "Please enter an integer value.", parent);
        }
    }
}

