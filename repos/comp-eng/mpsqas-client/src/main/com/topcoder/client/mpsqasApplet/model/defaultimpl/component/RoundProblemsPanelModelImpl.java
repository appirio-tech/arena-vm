package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.RoundProblemsPanelModel;

import java.util.ArrayList;

public class RoundProblemsPanelModelImpl extends RoundProblemsPanelModel {

    private ArrayList singleProblems;
    private ArrayList teamProblems;
    private ArrayList longProblems;

    public void init() {
        singleProblems = new ArrayList();
        teamProblems = new ArrayList();
        longProblems = new ArrayList();
    }

    public void setSingleProblems(ArrayList singleProblems) {
        this.singleProblems = singleProblems;
    }

    public ArrayList getSingleProblems() {
        return singleProblems;
    }

    public void setTeamProblems(ArrayList teamProblems) {
        this.teamProblems = teamProblems;
    }

    public ArrayList getTeamProblems() {
        return teamProblems;
    }
    
    public void setLongProblems(ArrayList longProblems) {
        this.longProblems = longProblems;
    }

    public ArrayList getLongProblems() {
        return longProblems;
    }
}
