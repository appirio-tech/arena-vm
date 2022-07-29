package com.topcoder.client.mpsqasApplet.view.component;

/**
 * An abstract class defining methods for a team statement panel view.
 *
 * @author mitalub
 */
public abstract class TeamStatementPanelView extends ComponentView {

    public abstract int getSelectedPart();

    public abstract String getProblemName();

    public abstract String getIntroduction();
}
