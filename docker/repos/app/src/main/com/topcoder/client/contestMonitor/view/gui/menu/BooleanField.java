package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JCheckBox;

final class BooleanField extends JCheckBox implements ViewField {

    private final boolean defaultState;

    BooleanField(boolean defaultState) {
        this.defaultState = defaultState;
    }

    public Object getFieldValue() {
        return isSelected() ? Boolean.TRUE : Boolean.FALSE;
    }

    public void clear() {
        setSelected(defaultState);
    }

}
