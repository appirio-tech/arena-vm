package com.topcoder.client.mpsqasApplet.view.component;

/**
 * View abstract class for RoundProblemsPanel.
 *
 * @author mitalub
 */
public abstract class RoundProblemsPanelView extends ComponentView {

    public abstract int getSelectedSingleProblemIndex();

    public abstract Object[] getSelectedTeamProblemPath();
    
    public abstract int getSelectedLongProblemIndex();
}
