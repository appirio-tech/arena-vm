package com.topcoder.client.ui.impl.component;

import javax.swing.JButton;

import com.topcoder.client.ui.UIComponentException;

public class UIButton extends UIAbstractButton {
    private JButton component;

    protected Object createComponent() {
        return new JButton();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JButton) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("defaultcapable".equalsIgnoreCase(name)) {
            component.setDefaultCapable(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("defaultbutton".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isDefaultButton());
        } else if ("defaultcapable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isDefaultCapable());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
