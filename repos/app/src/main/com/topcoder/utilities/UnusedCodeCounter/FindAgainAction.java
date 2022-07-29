/**
 * FindAgainAction.java
 *
 * Description:		Repeats the prior find action
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.utilities.UnusedCodeCounter;

public class FindAgainAction extends javax.swing.AbstractAction {

    UnusedCode parent;

    public FindAgainAction(UnusedCode parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        parent.getFindDialog().findAgain(); 
        parent.getTextArea().requestFocus(); 
    }
}


/* @(#)FindAction.java */
