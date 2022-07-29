package com.topcoder.client.contestApplet.widgets.ui;

import com.topcoder.client.contestApplet.widgets.ClipboardEditorPane;
import com.topcoder.client.ui.impl.component.UIEditorPane;
import com.topcoder.client.ui.UIComponentException;

public class UIClipboardEditorPane extends UIEditorPane {
    private ClipboardEditorPane pane;

    protected Object createComponent() {
        return new ClipboardEditorPane();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        pane = (ClipboardEditorPane) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("clipboardenabled".equalsIgnoreCase(name)) {
            pane.setClipboardEnabled(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("clipboardenabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(pane.isClipboardEnabled());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
