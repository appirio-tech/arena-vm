package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JTextField;

final class OptionalIntegerField extends JTextField implements ViewField {

    private static final int COLUMNS = 11;

    private final String defaultValue;

    OptionalIntegerField() {
        this("");
    }

    OptionalIntegerField(int initValue) {
        this("" + initValue);
    }

    private OptionalIntegerField(String init) {
        super(COLUMNS);
        defaultValue = "" + init;
    }

    public Object getFieldValue() throws Exception {
        String contents = getText().trim();
        if (contents.equals("")) {
            return null;
        }
        return Integer.decode(contents);
    }

    public void clear() {
        setText(defaultValue);
    }

}
