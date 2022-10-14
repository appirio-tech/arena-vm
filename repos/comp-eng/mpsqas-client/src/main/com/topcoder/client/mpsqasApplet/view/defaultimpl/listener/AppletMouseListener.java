package com.topcoder.client.mpsqasApplet.view.defaultimpl.listener;

import java.awt.event.*;
import java.lang.reflect.*;

/**
 * AppletMouseListener implements MouseListener and calls a method in a
 * class when an action is performed.
 *
 * @author mitalub
 */
public class AppletMouseListener implements MouseListener {

    /**
     *  Constructor - sets up the AppletMouseListener by calling the ActionListener constructor
     *  and storing information on the name of the method / class to call when the action is
     *  performed.
     *
     *  @param parentMethod   Method in parent to call when the action is performed.
     *  @param parent         The Object in which to call the method when the action is performed.
     *  @param mouseActionType The type of mouse action
     */
    public AppletMouseListener(String parentMethod, Object parent, String mouseActionType) {
        this(parentMethod, parent, mouseActionType, true);
    }

    /**
     * Same as first constructor, but offers option of passing event to
     * called method.
     */
    public AppletMouseListener(String parentMethod, Object parent, String mouseActionType, boolean passEvent) {
        super();
        this.passEvent = passEvent;
        this.parentMethod = parentMethod;
        this.parent = parent;
        this.mouseActionType = mouseActionType;
    }

    /**
     *  mouseClicked invokes the method in the parent when the action is performed.
     *
     *  @param e              The action performed.
     */
    public void mouseClicked(MouseEvent e) {
        if (mouseActionType.equals("mouseClicked"))
            invokeMethod(e);
    }

    /**
     * mousePressed calls method if that is the action the listener is waiting for.
     *
     * @param e    The mouse press event
     */
    public void mousePressed(MouseEvent e) {
        if (mouseActionType.equals("mousePressed"))
            invokeMethod(e);
    }

    /**
     * mouseReleased calls the parnent method if that is the action the
     * listener is listening for.
     *
     * @param e  The mouse event
     */
    public void mouseReleased(MouseEvent e) {
        if (mouseActionType.equals("mouseReleased"))
            invokeMethod(e);
    }

    /**
     * mouseEntered calls the parnent method if that is the action the
     * listener is listening for.
     *
     * @param e  The mouse event
     */
    public void mouseEntered(MouseEvent e) {
        if (mouseActionType.equals("mouseEntered"))
            invokeMethod(e);
    }

    /**
     * mouseExited calls the parnent method if that is the action the
     * listener is listening for.
     *
     * @param e  The mouse event
     */
    public void mouseExited(MouseEvent e) {
        if (mouseActionType.equals("mouseExited"))
            invokeMethod(e);
    }

    /**
     * invokeMethod invokes the method in the parent with the passed parameter.
     *
     * @param e  The event to pass to the parent method
     */
    private void invokeMethod(MouseEvent e) {
        try {
            Class[] parameterTypes = {MouseEvent.class};
            Object[] parameterValues = {e};
            if (!passEvent) {
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

    private String mouseActionType;
    private String parentMethod;
    private Object parent;
    private boolean passEvent;
}
