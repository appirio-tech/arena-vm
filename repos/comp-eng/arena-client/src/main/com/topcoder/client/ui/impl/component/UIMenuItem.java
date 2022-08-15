package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.MenuItemUI;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIMenuItem extends UIAbstractButton {
    private JMenuItem component;

    protected Object createComponent() {
        return new JMenuItem();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JMenuItem) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("Accelerator".equalsIgnoreCase(name)) {
            component.setAccelerator((KeyStroke) value);
        } else if ("Armed".equalsIgnoreCase(name)) {
            component.setArmed(((Boolean) value).booleanValue());
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((MenuItemUI) value);
        } else if ("SelectionBackground".equalsIgnoreCase(name)) {
            UIManager.put("MenuItem.selectionBackground", (Color) value);
            component.setUI(component.getUI());
        } else if ("SelectionForeground".equalsIgnoreCase(name)) {
            UIManager.put("MenuItem.selectionForeground", (Color) value);
            component.setUI(component.getUI());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Accelerator".equalsIgnoreCase(name)) {
            return component.getAccelerator();
        } else if ("Armed".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isArmed());
        } else if ("SubElements".equalsIgnoreCase(name)) {
            return component.getSubElements();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("MenuDragMouse".equalsIgnoreCase(name)) {
            component.addMenuDragMouseListener((UIMenuDragMouseListener) listener);
        } else if ("MenuKey".equalsIgnoreCase(name)) {
            component.addMenuKeyListener((UIMenuKeyListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("MenuDragMouse".equalsIgnoreCase(name)) {
            component.removeMenuDragMouseListener((UIMenuDragMouseListener) listener);
        } else if ("MenuKey".equalsIgnoreCase(name)) {
            component.removeMenuKeyListener((UIMenuKeyListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }
}
