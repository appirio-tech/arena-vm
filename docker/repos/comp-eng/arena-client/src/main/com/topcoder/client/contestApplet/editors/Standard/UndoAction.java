/**
 * FindAction.java
 *
 */

package com.topcoder.client.contestApplet.editors.Standard;

public class UndoAction extends javax.swing.AbstractAction {

    StandardEditorPanel parent;

    public UndoAction(StandardEditorPanel parent) {
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
