package com.topcoder.client.mpsqasApplet.model.component;

/**
 * Abstract class for the java doc panel model.
 *
 * @author mitalub
 */
public abstract class JavaDocPanelModel extends ComponentModel {

    public abstract void setPreviewHTML(String previewHTML);

    public abstract String getPreviewHTML();
}
