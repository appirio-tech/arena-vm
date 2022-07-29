/**
 * RedoAction.java
 *
 */

package com.topcoder.utilities.UnusedCodeCounter;

public class RedoAction extends javax.swing.AbstractAction {

    UnusedCode parent;

    public RedoAction(UnusedCode parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        parent.doRedo();
    }
    
    public void updateRedoState()
    {
        //todo: disable button here if necessary
    }
}
