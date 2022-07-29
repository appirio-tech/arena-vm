package com.topcoder.client.mpsqasApplet.view.component;

/**
 * Abstract class defining methods for the all solution panel view.
 *
 * @author mitalub
 */
public abstract class AllSolutionPanelView extends ComponentView {

    public abstract int getSelectedSolutionIndex();

    public abstract void setPreviewText(String preview);
}
