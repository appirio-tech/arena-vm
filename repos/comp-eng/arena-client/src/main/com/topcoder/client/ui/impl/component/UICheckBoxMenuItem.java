package com.topcoder.client.ui.impl.component;

import javax.swing.JCheckBoxMenuItem;

import com.topcoder.client.ui.UIComponentException;

public class UICheckBoxMenuItem extends UIMenuItem {
    private JCheckBoxMenuItem component;

    protected Object createComponent() {
        return new JCheckBoxMenuItem();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JCheckBoxMenuItem) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("State".equalsIgnoreCase(name)) {
            component.setState(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("State".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getState());
        } else if ("SelectedObjects".equalsIgnoreCase(name)) {
            return component.getSelectedObjects();
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
