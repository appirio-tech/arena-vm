package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.DateRenderer;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.ui.UIComponentException;

public class UIDateRenderer extends UILabel {
    private DateRenderer renderer;

    protected Object createComponent() {
        return new DateRenderer();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        renderer = (DateRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("Colors".equalsIgnoreCase(name)) {
            renderer.setColors((String) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
