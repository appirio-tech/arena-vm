package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JTextField;

final class IntegerField extends JTextField implements ViewField {

    private static final int COLUMNS = 11;

    private final String defaultValue;

    IntegerField() {
        this("");
    }

    IntegerField(int initValue) {
        this("" + initValue);
    }

    private IntegerField(String init) {
        super(COLUMNS);
        defaultValue = "" + init;
    }

    public Object getFieldValue() throws Exception {
        return Integer.decode(getText().trim());
    }

    public void clear() {
        setText(defaultValue);
    }

}
