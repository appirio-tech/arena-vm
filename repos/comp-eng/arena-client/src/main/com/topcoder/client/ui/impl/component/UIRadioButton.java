package com.topcoder.client.ui.impl.component;

import javax.swing.JRadioButton;

public class UIRadioButton extends UIToggleButton {
    protected Object createComponent() {
        return new JRadioButton();
    }
}
