/**
 * GotoAction.java
 *
 * Description:		Called when a goto action is done
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.Standard;

import com.topcoder.client.contestApplet.common.Common;

public class GotoAction extends javax.swing.AbstractAction {

    StandardEditorPanel parent;

    public GotoAction(StandardEditorPanel parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Get the text area
        javax.swing.JTextPane textPane = parent.getTextPane();

        // Ask for what line to goto
        String ss = Common.input("Goto Line", "Enter the line number:", parent);
        if (ss == null) {
            return;
        }

        try {
            // Get the line count
            int lines = textPane.getDocument().getDefaultRootElement().getElementCount();

            // Parse the number entered and goto it
            int num = Integer.parseInt(ss);
            String lineString = (lines <= 1) ? " line." : " lines.";
            String isAre = (lines <= 1) ? " is " : " are ";
            if (num < 0 || num > lines) {
                Common.showMessage("Goto Line", "There" + isAre + "only " + lines + lineString, parent);
            } else {          
                textPane.setCaretPosition(textPane.getDocument().getDefaultRootElement().getElement(num-1).getStartOffset());
                textPane.requestFocus();
            }
        } catch (Exception ex) {
            Common.showMessage("Goto Line", "Please enter an integer value.", parent);
        }
    }
}

