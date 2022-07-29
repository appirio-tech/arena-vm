package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;

/**
 *  AppletComponentListener implements ComponentListener and calls a method in
 *  a specified class whenever an action is performed.
 *
 * @author mitalub
 */
public class AppletComponentListener implements ComponentListener {

    /**
     *  Constructor - sets up the AppletComponentListener by calling the ComponentListener constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param listenerMethod Method in ComponentListener to act on.
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     */
    public AppletComponentListener(String listenerMethod, String parentMethod, Object parent) {
        this(listenerMethod, parentMethod, parent, true);
    }

    /**
     *  Constructor - sets up the AppletComponentListener by calling the ComponentListener constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param listenerMethod Method in ComponentListener to act on.
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     *  @param passObject     True if the ComponentListener should be passed to the parentMethod.
     */
    public AppletComponentListener(String listenerMethod, String parentMethod, Object parent, boolean passObject) {
        super();
        this.listenerMethod = listenerMethod;
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.passObject = passObject;
    }

    public void componentHidden(ComponentEvent e) {
        if (listenerMethod.equals("componentHidden")) {
            invoke(e);
        }
    }

    public void componentMoved(ComponentEvent e) {
        if (listenerMethod.equals("componentMoved")) {
            invoke(e);
        }
    }

    public void componentResized(ComponentEvent e) {
        if (listenerMethod.equals("componentResized"))
            invoke(e);
    }

    public void componentShown(ComponentEvent e) {
        if (listenerMethod.equals("componentShown")) {
            invoke(e);
        }
    }

    /**
     * invoke envokes the method in the parent class (specified through the constructor)
     * with the ComponentEvent as an argument.
     *
     * @param e   The window event.
     */
    public void invoke(ComponentEvent e) {
        try {
            Class[] parameterTypes = {ComponentEvent.class};
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

