package com.topcoder.client.ui.impl.component;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Rectangle;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIAWTFrame extends UIAWTWindow {
    private Frame component;

    protected Object createComponent() {
        Frame frame = new Frame();
        frame.setLayout(new GridBagLayout());

        return frame;
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (Frame) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("extendedstate".equalsIgnoreCase(name)) {
            component.setExtendedState(((Number) value).intValue());
        } else if ("iconimage".equalsIgnoreCase(name)) {
            component.setIconImage((Image) value);
        } else if ("maximizedbounds".equalsIgnoreCase(name)) {
            component.setMaximizedBounds((Rectangle) value);
        } else if ("menubar".equalsIgnoreCase(name)) {
            component.setMenuBar((MenuBar) value);
        } else if ("resizable".equalsIgnoreCase(name)) {
            component.setResizable(((Boolean) value).booleanValue());
        } else if ("title".equalsIgnoreCase(name)) {
            component.setTitle((String) value);
        } else if ("undecorated".equalsIgnoreCase(name)) {
            component.setUndecorated(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("extendedstate".equalsIgnoreCase(name)) {
            return new Integer(component.getExtendedState());
        } else if ("iconimage".equalsIgnoreCase(name)) {
            return component.getIconImage();
        } else if ("maximizedbounds".equalsIgnoreCase(name)) {
            return component.getMaximizedBounds();
        } else if ("menubar".equalsIgnoreCase(name)) {
            return component.getMenuBar();
        } else if ("title".equalsIgnoreCase(name)) {
            return component.getTitle();
        } else if ("resizable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isResizable());
        } else if ("undecorated".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isUndecorated());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("remove".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {MenuComponent.class});
            component.remove((MenuComponent) args[0]);
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
