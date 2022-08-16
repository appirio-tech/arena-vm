/**
 * FindAction.java
 *
 */

package com.topcoder.utilities.UnusedCodeCounter;

public class UndoAction extends javax.swing.AbstractAction {

    UnusedCode parent;

    public UndoAction(UnusedCode parent) {
        this.parent = parent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        parent.doUndo();
    }
    
    public void updateUndoState()
    {
        //todo: disable button here if necessary
    }
}
