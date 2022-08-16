package com.topcoder.client.contestApplet.common;

import java.awt.event.ActionListener;

public final class MenuItemInfo {

    private final String text;
    private final ActionListener actionListener;
    private final String propertyName;
    private final char mnemonic;
    private boolean hasMnemonic = false;
    private boolean enabled = true;

    public MenuItemInfo(String text, ActionListener actionListener) {
        this(text, actionListener, null);
    }

    public MenuItemInfo(String text, ActionListener actionListener, String propertyName) {
        this(text, '\0', actionListener, propertyName);
    }

    public MenuItemInfo(String text, char mnemonic, ActionListener actionListener) {
        this(text, mnemonic, actionListener, null);
    }

    public MenuItemInfo(String text, char mnemonic, ActionListener actionListener, String propertyName) {
        this.text = text;
        this.actionListener = actionListener;
        this.propertyName = propertyName;
        this.mnemonic = mnemonic;
        this.hasMnemonic = mnemonic != '\0';
    }

    public boolean getHasMnemonic() {
        return hasMnemonic;
    }

    public char getMnemonic() {
        return mnemonic;
    }

    public String getText() {
        return text;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
