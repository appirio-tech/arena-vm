package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JWindow;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIWindow extends UIAWTWindow {
    private JWindow component;

    protected Object createComponent() {
        Object arg = properties.get("owner");
        JWindow window;

        if (arg instanceof Frame) {
            window = new JWindow((Frame) arg);
        } else {
            window = new JWindow((Window) arg);
        }

        window.getContentPane().setLayout(new GridBagLayout());

        return window;
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The owner is not a window or frame.", e);
        }

        component = (JWindow) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            this.component.getContentPane().add((Component) component.getEventSource(), constraints);
        } else {
            throw new UIComponentException("JWindow does not support component class " + component.getClass() + ".");
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("contentpane".equalsIgnoreCase(name)) {
            return component.getContentPane();
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
