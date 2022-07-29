package com.topcoder.client.ui.impl.component;

import javax.swing.JRadioButtonMenuItem;

public class UIRadioButtonMenuItem extends UIMenuItem {
    protected Object createComponent() {
        return new JRadioButtonMenuItem();
    }
}
