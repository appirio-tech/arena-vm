package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;

/**
 *  AppletWindowListener implements WindowAdapter and calls a method in
 *  a specified class whenever an action is performed.
 *
 * @author mitalub
 */
public class AppletWindowListener extends WindowAdapter {

    /**
     *  Constructor - sets up the AppletWindowListener by calling the WindowAdapter constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param listenerMethod Method in WindowListener to act on.
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     */
    public AppletWindowListener(String listenerMethod, String parentMethod, Object parent) {
        this(listenerMethod, parentMethod, parent, true);
    }

    /**
     *  Constructor - sets up the AppletWindowListener by calling the WindowAdapter constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param listenerMethod Method in WindowListener to act on.
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     *  @param passObject     True if the WindowEvent should be passed to the parentMethod.
     */
    public AppletWindowListener(String listenerMethod, String parentMethod, Object parent, boolean passObject) {
        super();
        this.listenerMethod = listenerMethod;
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.passObject = passObject;
    }

    /**
     * windowClosing is called when the window is closing.
     *
     * @param e  The window closing event.
     */
    public void windowClosing(WindowEvent e) {
        if (listenerMethod.equals("windowClosing"))
            invoke(e);
    }

    /**
     * windowClosed is called when the window is closed.
     *
     * @param e  The window closed event.
     */
    public void windowClosed(WindowEvent e) {
        if (listenerMethod.equals("windowClosed"))
            invoke(e);
    }

    /**
     * windowActivate is called when the window is activated
     *
     * @param e  The window activating event.
     */
    public void windowActivated(WindowEvent e) {
        if (listenerMethod.equals("windowActivated"))
            invoke(e);
    }

    /**
     * invoke envokes the method in the parent class (specified through the constructor)
     * with the WindowEvent as an argument.
     *
     * @param e   The window event.
     */
    public void invoke(WindowEvent e) {
        try {
            Class[] parameterTypes = {WindowEvent.class};
            Object[] parameterValues = {e};
            if (!passObject) {
                parameterTypes = new Class[0];
                parameterValues = new Object[0];
            }

            Method method = parent.getClass().getMethod(parentMethod, parameterTypes);
            method.invoke(parent, parameterValues);
        } catch (Exception ex) {
            System.out.println("Error invokating method when action performed:");
            System.out.println("CLASS: " + parent.getClass().toString());
            System.out.println("METHOD: " + parentMethod);
            ex.printStackTrace();
        }
    }

    // class variables
    private String listenerMethod = null;
    private String parentMethod = null;
    private Object parent = null;
    private boolean passObject;
}

