package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIContainer extends UIAWTComponent {
    private Container component;

    protected Object createComponent() {
        Container container = new Container();
        container.setLayout(new GridBagLayout());

        return container;
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (Container) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            this.component.add((Component) component.getEventSource(), constraints);
        } else {
            throw new UIComponentException("This container does not support " + component.getClass() + ".");
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("insets".equalsIgnoreCase(name)) {
            return component.getInsets();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("container".equalsIgnoreCase(name)) {
            component.addContainerListener((UIContainerListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("container".equalsIgnoreCase(name)) {
            component.removeContainerListener((UIContainerListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("transferfocusdowncycle".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.transferFocusDownCycle();
            return null;
        } else if ("removeAll".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.removeAll();
            return null;
        } else if ("add".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class, GridBagConstraints.class});
            component.add((Component) args[0], (GridBagConstraints) args[1]);
            return null;
        } else if ("validate".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.validate();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
