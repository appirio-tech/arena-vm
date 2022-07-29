package com.topcoder.client.mpsqasApplet.model.component;

/**
 * Abstract class for class editor panel model.
 *
 * @author mitalub
 */
public abstract class ClassEditorPanelModel extends ComponentModel {

    public abstract void setName(String name);

    public abstract String getName();

    public abstract void setText(String text);

    public abstract String getText();

    public abstract void setIsEditable(boolean editable);

    public abstract boolean isEditable();
}
