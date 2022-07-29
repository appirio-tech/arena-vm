package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.IconBooleanRenderer;
import com.topcoder.client.ui.impl.component.UILabel;

public class UIIconBooleanRenderer extends UILabel {
    protected Object createComponent() {
        return new IconBooleanRenderer();
    }
}
