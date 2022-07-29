package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
 *
 * @author mitalub
 */
public class AppletCellEditorListener implements CellEditorListener {

    /**
     */
    public AppletCellEditorListener(String parentMethod, Object parent, String type) {
        this(parentMethod, parent, type, true);
    }

    public AppletCellEditorListener(String parentMethod, Object parent,
            String type, boolean passObject) {
        super();
        this.passObject = passObject;
        this.type = type;
        this.parentMethod = parentMethod;
        this.parent = parent;
    }

    /**
     */
    public void editingStopped(ChangeEvent e) {
        if (type.equals("editingStopped")) {
            invokeMethod(e);
        }
    }

    public void editingCanceled(ChangeEvent e) {
        if (type.equals("editingCanceled")) {
            invokeMethod(e);
        }
    }

    private void invokeMethod(ChangeEvent e) {
        try {
            Class[] parameterTypes = {ChangeEvent.class};
            Object[] parameterValues = {e};
            if (!passObject) {
                parameterTypes = new Class[0];
                parameterValues = new Object[0];
            }
            Method method = parent.getClass().getMethod(parentMethod, parameterTypes);
            method.invoke(parent, parameterValues);
        } catch (Exception ex) {
            System.out.println("Error invokating method when action performed:");
            System.out.println(parent.getClass() + ", " + parentMethod);
            ex.printStackTrace();
        }
    }

    private String parentMethod;
    private Object parent;
    private String type;
    private boolean passObject;
}
