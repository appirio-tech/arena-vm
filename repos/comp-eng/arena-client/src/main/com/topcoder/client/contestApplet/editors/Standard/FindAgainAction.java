/**
 * FindAgainAction.java
 *
 * Description:		Repeats the prior find action
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.Standard;

public class FindAgainAction extends javax.swing.AbstractAction {

    StandardEditorPanel parent;

    public FindAgainAction(StandardEditorPanel parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        parent.getFindDialog().findAgain();
        parent.getTextPane().requestFocus();
    }
}


/* @(#)FindAction.java */
