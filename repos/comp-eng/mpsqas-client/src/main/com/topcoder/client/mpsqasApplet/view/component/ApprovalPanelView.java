package com.topcoder.client.mpsqasApplet.view.component;

/**
 * Defines methods of the approval panel view.
 *
 * @author mitalub
 */
public abstract class ApprovalPanelView extends ComponentView {

    public abstract boolean isAccepted();

    public abstract String getMessage();
}
