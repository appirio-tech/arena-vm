package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.contestApplet.widgets.FillingOvalIcon;

public class UIFillingOvalIcon extends UITCIcon {
    private FillingOvalIcon icon;

    protected Object createComponent() {
        return new FillingOvalIcon();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        icon = (FillingOvalIcon) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("percentage".equalsIgnoreCase(name)) {
            icon.setPercentage(((Number) value).doubleValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
