package com.topcoder.client.ui.impl.component;

import javax.swing.JCheckBox;

import com.topcoder.client.ui.UIComponentException;

public class UICheckBox extends UIToggleButton {
    private JCheckBox component;

    protected Object createComponent() {
        return new JCheckBox();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JCheckBox) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("BorderPaintedFlat".equalsIgnoreCase(name)) {
            component.setBorderPaintedFlat(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("BorderPaintedFlat".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isBorderPaintedFlat());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
