/**
 * FindAction.java
 *
 * Description:		Called by any find action.  Display's the find dialog and puts focus back on the text area
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.utilities.UnusedCodeCounter;

public class FindAction extends javax.swing.AbstractAction {

    UnusedCode parent;

    public FindAction(UnusedCode parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        parent.getFindDialog().show(); 
        //parent.getTextArea().requestFocus();
    }
}
