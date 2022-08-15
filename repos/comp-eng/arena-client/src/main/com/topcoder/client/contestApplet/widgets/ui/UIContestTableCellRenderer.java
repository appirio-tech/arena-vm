package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.ContestTableCellRenderer;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.table.UIDefaultTableCellRenderer;

public class UIContestTableCellRenderer extends UIDefaultTableCellRenderer {
    private ContestTableCellRenderer component;

    protected Object createComponent() {
        return new ContestTableCellRenderer();
    }

    protected void initialize() {
        super.initialize();
        component = (ContestTableCellRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("FontName".equalsIgnoreCase(name)) {
            component.setFontName((String) value);
        } else if ("FontSize".equalsIgnoreCase(name)) {
            component.setFontSize(((Number) value).intValue());
        } else if ("Value".equalsIgnoreCase(name)) {
            component.setValue(value);
        } else if ("EnableBlankDisplay".equalsIgnoreCase(name)) {
            component.setEnableBlankDisplay(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
