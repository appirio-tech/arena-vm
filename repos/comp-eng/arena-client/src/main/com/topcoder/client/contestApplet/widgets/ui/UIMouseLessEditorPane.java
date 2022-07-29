package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.MouseLessEditorPane;
import com.topcoder.client.ui.impl.component.UIEditorPane;

public class UIMouseLessEditorPane extends UIEditorPane {
    protected Object createComponent() {
        return new MouseLessEditorPane();
    }
}
