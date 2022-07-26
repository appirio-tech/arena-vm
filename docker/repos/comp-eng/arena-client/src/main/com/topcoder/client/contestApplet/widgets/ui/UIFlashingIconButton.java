package com.topcoder.client.contestApplet.widgets.ui;

import javax.swing.Icon;

import com.topcoder.client.contestApplet.widgets.FlashingIconButton;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UIButton;

public class UIFlashingIconButton extends UIButton {
    private FlashingIconButton button;

    protected Object createComponent() {
        if (!properties.containsKey("flashingicon")) {
            throw new IllegalArgumentException("FlashingIconButton needs flashingIcon, nonFlashingIcon, disabledFlashingIcon and disabledNonFlashingIcon properties.");
        }

        return new FlashingIconButton((Icon) properties.get("flashingicon"), (Icon) properties.get("nonflashingicon"),
(Icon) properties.get("disabledflashingicon"), (Icon) properties.get("disablednonflashingicon"));
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("FlashingIconButton needs icons.", e);
        } catch (IllegalArgumentException e) {
            throw new UIComponentException("Required property missing.", e);
        }

        button = (FlashingIconButton) getEventSource();    
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("FlashingIcon".equalsIgnoreCase(name)) {
            // Ignore, use in constructors.
        } else if ("NonFlashingIcon".equalsIgnoreCase(name)) {
            // Ignore, use in constructors.
        } else if ("DisabledFlashingIcon".equalsIgnoreCase(name)) {
            // Ignore, use in constructors.
        } else if ("DisabledNonFlashingIcon".equalsIgnoreCase(name)) {
            // Ignore, use in constructors.
        } else if ("Flashing".equalsIgnoreCase(name)) {
            button.setFlashing(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Flashing".equalsIgnoreCase(name)) {
            return Boolean.valueOf(button.isFlashing());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
