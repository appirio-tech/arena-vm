package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;

/**
 * AppletFocusListener implements FocusListener and calls a method in a
 * class when a focus changes
 *
 * @author mitalub
 */
public class AppletFocusListener implements FocusListener {

    /**
     *  Constructor - sets up the AppletFocusListener by calling the FocusListener constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     *  @param type           The type of focus change (focusLost / focusGained)
     */
    public AppletFocusListener(String parentMethod, Object parent, String type) {
        this(parentMethod, parent, type, true);
    }

    public AppletFocusListener(String parentMethod, Object parent, String type, boolean passAction) {
        super();
        this.passAction = passAction;
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.type = type;
    }

    /**
     *  focusLost invokes the method in the parent when the action is performed.
     *
     *  @param e              The action performed.
     */
    public void focusLost(FocusEvent e) {
        if (!type.equals("focusLost")) return;
        try {
            Class[] parameterTypes = {ActionEvent.class};
            Object[] parameterValues = {e};
            if (!passAction) {
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

    /**
     *  focusGained invokes the method in the parent when the action is performed.
     *
     *  @param e              The action performed.
     */
    public void focusGained(FocusEvent e) {
        if (!type.equals("focusGained")) return;
        try {
            Class[] parameterTypes = {ActionEvent.class};
            Object[] parameterValues = {e};
            if (!passAction) {
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
    private boolean passAction;
}
