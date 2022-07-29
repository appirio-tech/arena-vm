package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.RoundRenderer;
import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.ui.UIComponentException;

public class UIRoundRenderer extends UILabel {
    protected Object createComponent() {
        return new RoundRenderer();
    }
}
