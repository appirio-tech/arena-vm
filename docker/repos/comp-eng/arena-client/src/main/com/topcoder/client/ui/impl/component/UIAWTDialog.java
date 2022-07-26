package com.topcoder.client.ui.impl.component;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagLayout;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UIAWTDialog extends UIAWTWindow {
    private Dialog component;

    protected Object createComponent() {
        Dialog dialog;

        if (properties.get("owner") instanceof Dialog) {
            dialog = new Dialog((Dialog) properties.get("owner"));
        } else {
            dialog = new Dialog((Frame) properties.get("owner"));
        }

        dialog.setLayout(new GridBagLayout());

        return dialog;
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The owner must be either a dialog or a frame.");
        }

        component = (Dialog) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("modal".equalsIgnoreCase(name)) {
            component.setModal(((Boolean) value).booleanValue());
        } else if ("resizable".equalsIgnoreCase(name)) {
            component.setResizable(((Boolean) value).booleanValue());
        } else if ("title".equalsIgnoreCase(name)) {
            component.setTitle((String) value);
        } else if ("Undecorated".equalsIgnoreCase(name)) {
            component.setUndecorated(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("modal".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isModal());
        } else if ("title".equalsIgnoreCase(name)) {
            return component.getTitle();
        } else if ("resizable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isResizable());
        } else if ("Undecorated".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isUndecorated());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
