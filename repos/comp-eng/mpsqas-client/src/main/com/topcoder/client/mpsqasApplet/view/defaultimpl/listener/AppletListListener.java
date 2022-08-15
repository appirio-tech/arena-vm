package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.lang.reflect.*;
import javax.swing.event.*;

/**
 * AppletListListener implements ListListener and calls a method in a
 * class when an list selection action is performed.
 *
 * @author mitalub
 */
public class AppletListListener implements ListSelectionListener {

    /**
     *  Constructor - sets up the AppletListListener by
     *  storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     */
    public AppletListListener(String parentMethod, Object parent) {
        this(parentMethod, parent, true);
        this.parentMethod = parentMethod;
        this.parent = parent;
    }

    public AppletListListener(String parentMethod, Object parent, boolean passEvent) {
        super();
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.passEvent = passEvent;
    }

    /**
     *  valueChanged invokes the method in the parent when the action is performed.
     *
     *  @param e              The action performed.
     */
    public void valueChanged(ListSelectionEvent e) {
        try {
            Class[] parameterTypes;
            Object[] parameterValues;
            if (passEvent) {
                parameterTypes = new Class[1];
                parameterTypes[0] = ListSelectionEvent.class;
                parameterValues = new Object[1];
                parameterValues[0] = e;
            } else {
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
    private boolean passEvent;
}
