package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.contestApplet.widgets.DiamondIcon;

public class UIDiamondIcon extends UITCIcon {
    private DiamondIcon icon;

    protected Object createComponent() {
        Color color = (Color) properties.get("foreground");
        Number height = (Number) properties.get("iconheight");
        Number width = (Number) properties.get("iconwidth");

        if (height != null && width != null) {
            return new DiamondIcon(color, true, height.intValue(), width.intValue());
        }

        return new DiamondIcon(color);
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The parameter type is invalid.", e);
        }

        icon = (DiamondIcon) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("iconheight".equalsIgnoreCase(name)) {
            // Accept
        } else if ("iconwidth".equalsIgnoreCase(name)) {
            // Accept
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
