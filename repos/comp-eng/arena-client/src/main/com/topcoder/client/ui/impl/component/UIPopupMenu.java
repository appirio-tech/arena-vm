package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.PopupMenuUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIPopupMenu extends UISwingComponent {
    private JPopupMenu component;

    protected Object createComponent() {
        return new JPopupMenu();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JPopupMenu) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Action) {
            this.component.add((Action) component.getEventSource());
        } else if (component.getEventSource() instanceof JMenuItem) {
            this.component.add((JMenuItem) component.getEventSource());
        } else if (component.getEventSource() instanceof Component) {
            this.component.add((Component) component.getEventSource());
        } else {
            throw new UIComponentException("Pop up menu can only have components and menu items.");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("Invoker".equalsIgnoreCase(name)) {
            component.setInvoker((Component) value);
        } else if ("label".equalsIgnoreCase(name)) {
            component.setLabel((String) value);
        } else if ("LightWeightPopupEnabled".equalsIgnoreCase(name)) {
            component.setLightWeightPopupEnabled(((Boolean) value).booleanValue());
        } else if ("PopupSize".equalsIgnoreCase(name)) {
            component.setPopupSize((Dimension) value);
        } else if ("Selected".equalsIgnoreCase(name)) {
            component.setSelected((Component) value);
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            component.setSelectionModel((SingleSelectionModel) value);
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((PopupMenuUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Invoker".equalsIgnoreCase(name)) {
            return component.getInvoker();
        } else if ("Label".equalsIgnoreCase(name)) {
            return component.getLabel();
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            return component.getSelectionModel();
        } else if ("SubElements".equalsIgnoreCase(name)) {
            return component.getSubElements();
        } else if ("BorderPainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isBorderPainted());
        } else if ("LightWeightPopupEnabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isLightWeightPopupEnabled());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("popupmenu".equalsIgnoreCase(name)) {
            component.addPopupMenuListener((UIPopupMenuListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("popupmenu".equalsIgnoreCase(name)) {
            component.removePopupMenuListener((UIPopupMenuListener) listener);
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
            if ((args != null) && (args.length == 2) && (args[0] instanceof Component)) {
                assertArgs(name, args, new Class[] {Component.class, Number.class});
                component.insert((Component) args[0], ((Number) args[1]).intValue());
            } else {
                assertArgs(name, args, new Class[] {Action.class, Number.class});
                component.insert((Action) args[0], ((Number) args[1]).intValue());
            }
            return null;
        } else if ("pack".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.pack();
            return null;
        } else if ("getComponentIndex".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class});
            return new Integer(component.getComponentIndex((Component) args[0]));
        } else if ("isPopupTrigger".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {MouseEvent.class});
            return Boolean.valueOf(component.isPopupTrigger((MouseEvent) args[0]));
        } else if ("remove".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            component.remove(((Number) args[0]).intValue());
            return null;
        } else if ("show".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class, Number.class, Number.class});
            component.show((Component) args[0], ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
