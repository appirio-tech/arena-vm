package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.ui.impl.component.UILabel;
import com.topcoder.client.contestApplet.panels.table.RankRenderer;

public class UIRankRenderer extends UILabel {
    protected Object createComponent() {
        return new RankRenderer();
    }
}
