package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.PopupModel;

/**
 * Default implementation of PopupModel.
 *
 * @author mitalub
 */
public class PopupModelImpl extends PopupModel {

    String text;
    boolean isVisible;

    public void init() {
        text = "";
        isVisible = false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
