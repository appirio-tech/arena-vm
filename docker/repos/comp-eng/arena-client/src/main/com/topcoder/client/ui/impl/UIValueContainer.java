package com.topcoder.client.ui.impl;

import java.awt.GridBagConstraints;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;

/**
 * Defines a container to hold an object value. This container acts like a UI component. It accepts any property
 * reading and writing, which is redirected to the object value.
 * 
 * @author Qi Liu
 * @version $Id: UIValueContainer.java 72387 2008-08-19 07:14:04Z qliu $
 */
public class UIValueContainer implements UIComponent {
    private Object value;

    private UIComponent parent;

    public void create() throws UIComponentException {
    }

    public void destroy() {
    }

    public void addChild(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Not allowed.");
    }

    public void setProperty(String name, Object value) throws UIComponentException {
        this.value = value;
    }

    public Object getProperty(String name) throws UIComponentException {
        return value;
    }

    public void addEventListener(String name, UIEventListener listener) throws UIComponentException {
        throw new UIComponentException("The event '" + name + "' does not exist.");
    }

    public void removeEventListener(String name, UIEventListener listener) throws UIComponentException {
        throw new UIComponentException("The event '" + name + "' does not exist.");
    }

    public Object performAction(String name) throws UIComponentException {
        return performAction(name, null);
    }

    public Object performAction(String name, Object[] args) throws UIComponentException {
        throw new UIComponentException("The action '" + name + "' cannot be performed.");
    }

    public Object getEventSource() {
        return value;
    }

    public UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }
}
