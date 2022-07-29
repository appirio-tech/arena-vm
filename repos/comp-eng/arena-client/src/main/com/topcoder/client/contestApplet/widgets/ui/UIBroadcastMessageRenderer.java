package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.BroadcastMessageRenderer;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.ui.UIComponentException;

public class UIBroadcastMessageRenderer extends UILabel {
    private BroadcastMessageRenderer renderer;

    protected Object createComponent() {
        return new BroadcastMessageRenderer();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        renderer = (BroadcastMessageRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("Colors".equalsIgnoreCase(name)) {
            renderer.setColors((String) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
