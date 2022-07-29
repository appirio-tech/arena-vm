package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.MenuBarUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UIMenuBar extends UISwingComponent {
    private JMenuBar component;

    protected Object createComponent() {
        return new JMenuBar();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JMenuBar) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof JMenu) {
            this.component.add((JMenu) component.getEventSource());
        } else {
            throw new UIComponentException("Only menus can be nested under the menu bar.");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("BorderPainted".equalsIgnoreCase(name)) {
            component.setBorderPainted(((Boolean) value).booleanValue());
        } else if ("HelpMenu".equalsIgnoreCase(name)) {
            component.setHelpMenu((JMenu) value);
        } else if ("Margin".equalsIgnoreCase(name)) {
            component.setMargin((Insets) value);
        } else if ("Selected".equalsIgnoreCase(name)) {
            component.setSelected((Component) value);
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            component.setSelectionModel((SingleSelectionModel) value);
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((MenuBarUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Component".equalsIgnoreCase(name)) {
            return component.getComponent();
        } else if ("HelpMenu".equalsIgnoreCase(name)) {
            return component.getHelpMenu();
        } else if ("Margin".equalsIgnoreCase(name)) {
            return component.getMargin();
        } else if ("MenuCount".equalsIgnoreCase(name)) {
            return new Integer(component.getMenuCount());
        } else if ("SubElements".equalsIgnoreCase(name)) {
            return component.getSubElements();
        } else if ("UI".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("BorderPainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isBorderPainted());
        } else if ("Selected".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isSelected());
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            return component.getSelectionModel();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("getMenu".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getMenu(((Number) args[0]).intValue());
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
