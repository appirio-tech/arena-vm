/**
 * RedoAction.java
 *
 */

package com.topcoder.client.contestApplet.editors.Standard;

public class RedoAction extends javax.swing.AbstractAction {

    StandardEditorPanel parent;

    public RedoAction(StandardEditorPanel parent) {
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
