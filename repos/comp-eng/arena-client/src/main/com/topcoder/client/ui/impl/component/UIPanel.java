package com.topcoder.client.ui.impl.component;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.plaf.PanelUI;

import com.topcoder.client.ui.UIComponentException;

public class UIPanel extends UISwingComponent {
    private JPanel component;

    protected Object createComponent() {
        JPanel panel;
        if (properties.get("doublebuffered") != null) {
            panel = new JPanel(((Boolean) properties.get("doublebuffered")).booleanValue());
        } else {
            panel = new JPanel();
        }
        panel.setLayout(new GridBagLayout());

        return panel;
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JPanel) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("ui".equalsIgnoreCase(name)) {
            component.setUI((PanelUI) value);
        } else if ("doublebuffered".equalsIgnoreCase(name)) {
            //accept
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("ui".equalsIgnoreCase(name)) {
            return component.getUI();
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
