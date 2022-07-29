package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;

/**
 * AppletActionListener implements ActionListener and calls a method in a
 * class when an action is performed.
 *
 * @author mitalub
 */
public class AppletActionListener implements ActionListener {

    /**
     *  Constructor - sets up the AppletActionListener by calling the ActionListener constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     */
    public AppletActionListener(String parentMethod, Object parent) {
        this(parentMethod, parent, true);
    }

    /**
     * Like previous contructor, but specifying false for last arg results in the
     * ActionEvent not being passed when the method is called.
     */
    public AppletActionListener(String parentMethod, Object parent, boolean passActionEvent) {
        super();
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.passActionEvent = passActionEvent;
    }

    /**
     *  actionPerformed invokes the method in the parent when the action is performed.
     *
     *  @param e              The action performed.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            Class[] parameterTypes;
            Object[] parameterValues;
            if (passActionEvent) {
                parameterTypes = new Class[1];
                parameterTypes[0] = ActionEvent.class;
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
    private boolean passActionEvent;
}
