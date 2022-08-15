package com.topcoder.client.mpsqasApplet.model.component;

import java.util.ArrayList;

/**
 * Interface for the model for the RoundProblemsPanel.
 *
 * @author mitalub
 */
public abstract class RoundProblemsPanelModel extends ComponentModel {

    public abstract void setSingleProblems(ArrayList singleProblems);

    public abstract ArrayList getSingleProblems();

    public abstract void setTeamProblems(ArrayList teamProblems);

    public abstract ArrayList getTeamProblems();
    
    public abstract void setLongProblems(ArrayList longProblems);

    public abstract ArrayList getLongProblems();
}
