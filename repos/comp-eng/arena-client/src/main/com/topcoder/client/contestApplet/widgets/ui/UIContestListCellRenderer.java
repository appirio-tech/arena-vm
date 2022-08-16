package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.contestApplet.widgets.ContestListCellRenderer;

public class UIContestListCellRenderer extends UILabel {
    private ContestListCellRenderer renderer;

    protected Object createComponent() {
        return new ContestListCellRenderer();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        renderer = (ContestListCellRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("selectedforeground".equalsIgnoreCase(name)) {
            renderer.setSelectedForeground((Color) value);
        } else if ("selectedbackground".equalsIgnoreCase(name)) {
            renderer.setSelectedBackground((Color) value);
        } else if ("unselectedforeground".equalsIgnoreCase(name)) {
            renderer.setUnselectedForeground((Color) value);
        } else if ("unselectedbackground".equalsIgnoreCase(name)) {
            renderer.setUnselectedBackground((Color) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
