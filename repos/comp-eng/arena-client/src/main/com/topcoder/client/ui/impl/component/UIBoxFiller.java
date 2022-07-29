package com.topcoder.client.ui.impl.component;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.Box;

public class UIBoxFiller extends UISwingComponent {
    protected Object createComponent() {
        return new Box.Filler(new Dimension(0,0), new Dimension(0,0), new Dimension(0,0));
    }
}
