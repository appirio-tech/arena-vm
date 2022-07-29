package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.component.JavaDocPanelModel;

/**
 * Default implementation of the java doc panel model.
 *
 * @author mitalub
 */
public class JavaDocPanelModelImpl extends JavaDocPanelModel {

    private String previewHTML;

    public void init() {
        previewHTML = "";
    }

    public void setPreviewHTML(String previewHTML) {
        this.previewHTML = previewHTML;
    }

    public String getPreviewHTML() {
        return previewHTML;
    }
}
