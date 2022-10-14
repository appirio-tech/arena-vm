package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.server.contest.RandomRoomAssigner;

import javax.swing.JTextField;

final class DoubleField extends JTextField implements ViewField {

    private static final int COLUMNS = 11;

    private final String defaultValue;

    DoubleField() {
        this("" + RandomRoomAssigner.DEFAULT_P);
    }

    DoubleField(double initValue) {
        this("" + initValue);
    }

    private DoubleField(String init) {
        super(COLUMNS);
        defaultValue = "" + init;
    }

    public Object getFieldValue() {
        try {
            return new Double(getText().trim());
        } catch (NumberFormatException e) {
            return new Double(RandomRoomAssigner.DEFAULT_P);
        }
    }

    public void clear() {
        setText(defaultValue);
    }

}
