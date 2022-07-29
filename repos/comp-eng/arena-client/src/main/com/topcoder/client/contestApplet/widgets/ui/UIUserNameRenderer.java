package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;

import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameRenderer;

public class UIUserNameRenderer extends UILabel {
    private UserNameRenderer renderer;

    protected Object createComponent() {
        return new UserNameRenderer();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        renderer = (UserNameRenderer) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("model".equalsIgnoreCase(name)) {
            renderer.setModel((Contestant) value);
        } else if ("currentuserbackground".equalsIgnoreCase(name)) {
            renderer.setCurrentUserBackground((Color) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
