package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.GridBagLayout;
import javax.swing.ImageIcon;

import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.ui.impl.component.UIPanel;
import com.topcoder.client.ui.UIComponentException;

public class UIBottomRightIconPanel extends UIPanel {
    protected Object createComponent() {
        if (properties.get("imageicon") == null) {
            throw new IllegalArgumentException("Property 'imageicon' is missing.");
        }

        BottomRightIconPanel panel = new BottomRightIconPanel((ImageIcon) properties.get("imageicon"));;

        return panel;
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The image icon is not an image icon.", e);
        } catch (IllegalArgumentException e) {
            throw new UIComponentException("There must be an image icon specified.", e);
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("imageicon".equalsIgnoreCase(name)) {
            // Do nothing, set in the constructor already.
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
