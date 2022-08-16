package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.TabbedPaneUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UITabbedPane extends UISwingComponent {
    private JTabbedPane component;

    protected Object createComponent() {
        return new JTabbedPane();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTabbedPane) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            this.component.add((Component) component.getEventSource());
        } else {
            throw new UIComponentException("This container does not support " + component.getClass() + ".");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("model".equalsIgnoreCase(name)) {
            component.setModel((SingleSelectionModel) value);
        } else if ("SelectedComponent".equalsIgnoreCase(name)) {
            component.setSelectedComponent((Component) value);
        } else if ("SelectedIndex".equalsIgnoreCase(name)) {
            component.setSelectedIndex(((Number) value).intValue());
        } else if ("TabLayoutPolicy".equalsIgnoreCase(name)) {
            component.setTabLayoutPolicy(((Number) value).intValue());
        } else if ("TabPlacement".equalsIgnoreCase(name)) {
            component.setTabPlacement(((Number) value).intValue());
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((TabbedPaneUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Model".equalsIgnoreCase(name)) {
            return component.getModel();
        } else if ("SelectedComponent".equalsIgnoreCase(name)) {
            return component.getSelectedComponent();
        } else if ("SelectedIndex".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedIndex());
        } else if ("TabCount".equalsIgnoreCase(name)) {
            return new Integer(component.getTabCount());
        } else if ("TabLayoutPolicy".equalsIgnoreCase(name)) {
            return new Integer(component.getTabLayoutPolicy());
        } else if ("TabPlacement".equalsIgnoreCase(name)) {
            return new Integer(component.getTabPlacement());
        } else if ("TabRunCount".equalsIgnoreCase(name)) {
            return new Integer(component.getTabRunCount());
        } else if ("UI".equalsIgnoreCase(name)) {
            return component.getUI();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("change".equalsIgnoreCase(name)) {
            component.addChangeListener((UIChangeListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("change".equalsIgnoreCase(name)) {
            component.removeChangeListener((UIChangeListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("addTab".equalsIgnoreCase(name)) {
            if (args != null && args.length == 2) {
                assertArgs(name, args, new Class[] {String.class, Component.class});
                component.addTab((String) args[0], (Component) args[1]);
            } else if (args != null && args.length == 3) {
                assertArgs(name, args, new Class[] {String.class, Icon.class, Component.class});
                component.addTab((String) args[0], (Icon) args[1], (Component) args[2]);
            } else {
                assertArgs(name, args, new Class[] {String.class, Icon.class, Component.class, String.class});
                component.addTab((String) args[0], (Icon) args[1], (Component) args[2], (String) args[3]);
            }

            return null;
        } else if ("getBackgroundAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getBackgroundAt(((Number) args[0]).intValue());
        } else if ("getBoundsAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getBoundsAt(((Number) args[0]).intValue());
        } else if ("getComponentAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getComponentAt(((Number) args[0]).intValue());
        } else if ("getDisabledIconAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getDisabledIconAt(((Number) args[0]).intValue());
        } else if ("getDisplayedMnemonicIndexAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return new Integer(component.getDisplayedMnemonicIndexAt(((Number) args[0]).intValue()));
        } else if ("getForegroundAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getForegroundAt(((Number) args[0]).intValue());
        } else if ("getIconAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getIconAt(((Number) args[0]).intValue());
        } else if ("getMnemonicAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return new Integer(component.getMnemonicAt(((Number) args[0]).intValue()));
        } else if ("getTitleAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getTitleAt(((Number) args[0]).intValue());
        } else if ("getToolTipTextAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getToolTipTextAt(((Number) args[0]).intValue());
        } else if ("isEnabledAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return Boolean.valueOf(component.isEnabledAt(((Number) args[0]).intValue()));
        } else if ("indexAtLocation".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return new Integer(component.indexAtLocation(((Number) args[0]).intValue(), ((Number) args[1]).intValue()));
        } else if ("indexOfComponent".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class});
            return new Integer(component.indexOfComponent((Component) args[0]));
        } else if ("indexOfTab".equalsIgnoreCase(name)) {
            if (args != null && args.length == 1 && args[0] instanceof Icon) {
                return new Integer(component.indexOfTab((Icon) args[0]));
            }
            assertArgs(name, args, new Class[] {String.class});
            return new Integer(component.indexOfTab((String) args[0]));
        } else if ("insertTab".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class, Icon.class, Component.class, String.class, Number.class});
            component.insertTab((String) args[0], (Icon) args[1], (Component) args[2], (String) args[3], ((Number) args[4]).intValue());
            return null;
        } else if ("isEnabledAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return Boolean.valueOf(component.isEnabledAt(((Number) args[0]).intValue()));
        } else if ("removeTabAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            component.removeTabAt(((Number) args[0]).intValue());
            return null;
        } else if ("setBackgroundAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Color.class});
            component.setBackgroundAt(((Number) args[0]).intValue(), (Color) args[1]);
            return null;
        } else if ("setComponentAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Component.class});
            component.setComponentAt(((Number) args[0]).intValue(), (Component) args[1]);
            return null;
        } else if ("setDisabledIconAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Icon.class});
            component.setDisabledIconAt(((Number) args[0]).intValue(), (Icon) args[1]);
            return null;
        } else if ("setDisplayedMnemonicIndexAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.setDisplayedMnemonicIndexAt(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("setEnabledAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Boolean.class});
            component.setEnabledAt(((Number) args[0]).intValue(), ((Boolean) args[1]).booleanValue());
            return null;
        } else if ("setForegroundAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Color.class});
            component.setForegroundAt(((Number) args[0]).intValue(), (Color) args[1]);
            return null;
        } else if ("setIconAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Icon.class});
            component.setIconAt(((Number) args[0]).intValue(), (Icon) args[1]);
            return null;
        } else if ("setMnemonicAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.setMnemonicAt(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("setTitleAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, String.class});
            component.setTitleAt(((Number) args[0]).intValue(), (String) args[1]);
            return null;
        } else if ("setToolTipTextAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, String.class});
            component.setToolTipTextAt(((Number) args[0]).intValue(), (String) args[1]);
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
