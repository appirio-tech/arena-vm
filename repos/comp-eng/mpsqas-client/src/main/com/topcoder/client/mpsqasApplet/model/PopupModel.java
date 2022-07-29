package com.topcoder.client.mpsqasApplet.model;

/**
 * Abstract class for the PopupModel.
 *
 * @author mitalub
 */
public abstract class PopupModel extends Model {

    public abstract void setText(String text);

    public abstract String getText();

    public abstract void setIsVisible(boolean isVisible);

    public abstract boolean isVisible();
}
