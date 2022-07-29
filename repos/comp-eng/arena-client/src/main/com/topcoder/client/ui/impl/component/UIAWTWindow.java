package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.Window;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIAWTWindow extends UIContainer {
    private Window component;

    protected Object createComponent() {
        Object arg = properties.get("owner");
        Window window;

        if (arg instanceof Frame) {
            window = new Window((Frame) arg);
        } else {
            window = new Window((Window) arg);
        }

        window.setLayout(new GridBagLayout());

        return window;
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The owner is not a window or frame.", e);
        }

        component = (Window) getEventSource();
    }

    protected void destroyImpl() {
        component.dispose();
        super.destroyImpl();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("owner".equalsIgnoreCase(name)) {
            if (component.getOwner() != value) {
                throw new UIComponentException("The window owner can only be set before usage.");
            }
        } else if ("focusablewindowstate".equalsIgnoreCase(name)) {
            component.setFocusableWindowState(((Boolean) value).booleanValue());
        } else if ("focuscycleroot".equalsIgnoreCase(name)) {
            component.setFocusCycleRoot(((Boolean) value).booleanValue());
        } else if ("locationrelativeto".equalsIgnoreCase(name)) {
            component.setLocationRelativeTo((Component) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("focusablewindowstate".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getFocusableWindowState());
        } else if ("focusowner".equalsIgnoreCase(name)) {
            return component.getFocusOwner();
        } else if ("mostrecentfocusowner".equalsIgnoreCase(name)) {
            return component.getMostRecentFocusOwner();
        } else if ("ownedwindows".equalsIgnoreCase(name)) {
            return component.getOwnedWindows();
        } else if ("owner".equalsIgnoreCase(name)) {
            return component.getOwner();
        } else if ("toolkit".equalsIgnoreCase(name)) {
            return component.getToolkit();
        } else if ("warningstring".equalsIgnoreCase(name)) {
            return component.getWarningString();
        } else if ("active".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isActive());
        } else if ("focusablewindow".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocusableWindow());
        } else if ("focuscycleroot".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocusCycleRoot());
        } else if ("focused".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocused());
        } else if ("showing".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isShowing());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("windowfocus".equalsIgnoreCase(name)) {
            component.addWindowFocusListener((UIWindowFocusListener) listener);
        } else if ("window".equalsIgnoreCase(name)) {
            component.addWindowListener((UIWindowListener) listener);
        } else if ("windowstate".equalsIgnoreCase(name)) {
            component.addWindowStateListener((UIWindowStateListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("windowfocus".equalsIgnoreCase(name)) {
            component.removeWindowFocusListener((UIWindowFocusListener) listener);
        } else if ("window".equalsIgnoreCase(name)) {
            component.removeWindowListener((UIWindowListener) listener);
        } else if ("windowstate".equalsIgnoreCase(name)) {
            component.removeWindowStateListener((UIWindowStateListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("hide".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.hide();
            return null;
        } else if ("show".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.show();
            return null;
        } else if ("pack".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.pack();
            return null;
        } else if ("toback".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.toBack();
            return null;
        } else if ("tofront".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.toFront();
            return null;
        } else if ("dispose".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.dispose();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
