package com.topcoder.client.ui.impl.component;

import javax.swing.JPasswordField;

import com.topcoder.client.ui.UIComponentException;

public class UIPasswordField extends UITextField {
    private JPasswordField component;

    protected Object createComponent() {
        return new JPasswordField();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JPasswordField) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("echochar".equalsIgnoreCase(name)) {
            component.setEchoChar(((Character) value).charValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("echochar".equalsIgnoreCase(name)) {
            return Character.valueOf(component.getEchoChar());
        } else if ("echocharisset".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.echoCharIsSet());
        } else if ("password".equalsIgnoreCase(name)) {
            return component.getPassword();
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
