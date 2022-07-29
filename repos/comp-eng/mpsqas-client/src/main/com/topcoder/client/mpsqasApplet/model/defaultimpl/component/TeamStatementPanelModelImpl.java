package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.TeamStatementPanelModel;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

public class TeamStatementPanelModelImpl extends TeamStatementPanelModel {

    public final static int DEFINITION = 1,
    INTRODUCTION = 2;

    private int currentPart;
    private ProblemInformation problemInformation;

    public void init() {
        currentPart = DEFINITION;
    }

    public void setCurrentPart(int currentPart) {
        this.currentPart = currentPart;
    }

    public int getCurrentPart() {
        return currentPart;
    }

    public void setProblemInformation(ProblemInformation problemInformation) {
        this.problemInformation = problemInformation;
    }

    public ProblemInformation getProblemInformation() {
        return problemInformation;
    }
}
