package com.topcoder.client.mpsqasApplet.view.component;

/**
 * Abstract class defining methods in the admin problem panel view.
 */
public abstract class AdminProblemPanelView extends ComponentView {

    public abstract int getSelectedAvailableTesterIndex();

    public abstract int getSelectedScheduledTesterIndex();

    public abstract int getStatus();

    public abstract int getPrimarySolution();
}
