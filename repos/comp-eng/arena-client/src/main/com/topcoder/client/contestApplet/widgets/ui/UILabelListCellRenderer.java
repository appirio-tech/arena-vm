package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.contestApplet.widgets.JLabelListCellRenderer;

public class UILabelListCellRenderer extends UILabel {
    private JLabelListCellRenderer renderer;

    protected Object createComponent() {
        return new JLabelListCellRenderer();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        renderer = (JLabelListCellRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("selectedbackground".equalsIgnoreCase(name)) {
            renderer.setSelectedBackground((Color) value);
        } else if ("unselectedbackground".equalsIgnoreCase(name)) {
            renderer.setUnselectedBackground((Color) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
