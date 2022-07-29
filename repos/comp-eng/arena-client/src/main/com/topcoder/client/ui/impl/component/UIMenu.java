package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.Point;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIMenu extends UISwingComponent {
    private JMenu component;

    protected Object createComponent() {
        return new JMenu();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JMenu) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Action) {
            this.component.add((Action) component.getEventSource());
        } else if (component.getEventSource() instanceof JMenuItem) {
            this.component.add((JMenuItem) component.getEventSource());
        } else if (component.getEventSource() instanceof Component) {
            this.component.add((Component) component.getEventSource());
        } else {
            throw new UIComponentException("Menu can only have components and menu items.");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("Accelerator".equalsIgnoreCase(name)) {
            component.setAccelerator((KeyStroke) value);
        } else if ("ComponentOrientation".equalsIgnoreCase(name)) {
            component.setComponentOrientation((ComponentOrientation) value);
        } else if ("Delay".equalsIgnoreCase(name)) {
            component.setDelay(((Number) value).intValue());
        } else if ("Model".equalsIgnoreCase(name)) {
            component.setModel((ButtonModel) value);
        } else if ("PopupMenuVisible".equalsIgnoreCase(name)) {
            component.setPopupMenuVisible(((Boolean) value).booleanValue());
        } else if ("Selected".equalsIgnoreCase(name)) {
            component.setSelected(((Boolean) value).booleanValue());
        } else if ("MenuLocation".equalsIgnoreCase(name)) {
            component.setMenuLocation(((Point) value).x, ((Point) value).y);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Component".equalsIgnoreCase(name)) {
            return component.getComponent();
        } else if ("Delay".equalsIgnoreCase(name)) {
            return new Integer(component.getDelay());
        } else if ("ItemCount".equalsIgnoreCase(name)) {
            return new Integer(component.getItemCount());
        } else if ("SubElements".equalsIgnoreCase(name)) {
            return component.getSubElements();
        } else if ("MenuComponentCount".equalsIgnoreCase(name)) {
            return new Integer(component.getMenuComponentCount());
        } else if ("MenuComponents".equalsIgnoreCase(name)) {
            return component.getMenuComponents();
        } else if ("PopupMenu".equalsIgnoreCase(name)) {
            return component.getPopupMenu();
        } else if ("PopupMenuVisible".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isPopupMenuVisible());
        } else if ("Selected".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isSelected());
        } else if ("TearOff".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isTearOff());
        } else if ("TopLevelMenu".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isTopLevelMenu());
        } else if ("PopupMenuVisible".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isPopupMenuVisible());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("menu".equalsIgnoreCase(name)) {
            component.addMenuListener((UIMenuListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("menu".equalsIgnoreCase(name)) {
            component.removeMenuListener((UIMenuListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("addSeparator".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.addSeparator();
            return null;
        } else if ("insert".equalsIgnoreCase(name)) {
            if ((args != null) && (args.length == 2) && (args[0] instanceof JMenuItem)) {
                assertArgs(name, args, new Class[] {JMenuItem.class, Number.class});
                component.insert((JMenuItem) args[0], ((Number) args[1]).intValue());
            } else {
                assertArgs(name, args, new Class[] {Action.class, Number.class});
                component.insert((Action) args[0], ((Number) args[1]).intValue());
            }
            return null;
        } else if ("insertSeparator".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            component.insertSeparator(((Number) args[0]).intValue());
            return null;
        } else if ("isMenuComponent".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class});
            return Boolean.valueOf(component.isMenuComponent((Component) args[0]));
        } else if ("getItem".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getItem(((Number) args[0]).intValue());
        } else if ("getMenuComponent".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getMenuComponent(((Number) args[0]).intValue());
        } else if ("remove".equalsIgnoreCase(name)) {
            if ((args != null) && (args.length == 1) && (args[0] instanceof JMenuItem)) {
                component.remove((JMenuItem) args[0]);
            } else if ((args != null) && (args.length == 1) && (args[0] instanceof Component)) {
                component.remove((Component) args[0]);
            } else {
                assertArgs(name, args, new Class[] {Number.class});
                component.remove(((Number) args[0]).intValue());
            }
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
