package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UIAbstractComponent;
import com.topcoder.client.contestApplet.widgets.TCIcon;

public abstract class UITCIcon extends UIAbstractComponent {
    private TCIcon icon;

    protected void initialize() throws UIComponentException {
        super.initialize();

        icon = (TCIcon) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("TC icon is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("selected".equalsIgnoreCase(name)) {
            icon.setSelected(((Boolean) value).booleanValue());
        } else if ("foreground".equalsIgnoreCase(name)) {
            icon.setForeground((Color) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("selected".equalsIgnoreCase(name)) {
            return Boolean.valueOf(icon.isSelected());
        } else if ("foreground".equalsIgnoreCase(name)) {
            return icon.getForeground();
        } else if ("iconheight".equalsIgnoreCase(name)) {
            return new Integer(icon.getIconHeight());
        } else if ("iconwidth".equalsIgnoreCase(name)) {
            return new Integer(icon.getIconWidth());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
