package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JTextField;

final class StringField extends JTextField implements ViewField {

    private String defaultValue = "";

    StringField() {
        super();
    }

    StringField(String init) {
        super(init);
        defaultValue = init;
    }

    StringField(int columns) {
        super(columns);
    }

    public Object getFieldValue() {
        return getText();
    }

    public void clear() {
        setText(defaultValue);
    }

}
