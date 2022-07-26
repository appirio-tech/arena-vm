package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.ClassEditorPanelModel;

/**
 * Default implementation of the class editor panel model.
 *
 * @author mitalub
 */
public class ClassEditorPanelModelImpl extends ClassEditorPanelModel {

    private String name;
    private String text;
    private boolean editable;

    public void init() {
        name = "";
        text = "";
        editable = true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setIsEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }
}
