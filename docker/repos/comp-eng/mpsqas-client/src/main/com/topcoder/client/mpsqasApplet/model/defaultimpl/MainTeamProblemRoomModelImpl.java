package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainTeamProblemRoomModel;

import java.util.HashMap;

/**
 * Default implementation of MainTeamProblemRoomModel.
 *
 * @author mitalub
 */
public class MainTeamProblemRoomModelImpl extends MainTeamProblemRoomModel {

    public HashMap problems;

    public void init() {
        problems = new HashMap();
    }

    public void setProblems(HashMap problems) {
        this.problems = problems;
    }

    public HashMap getProblems() {
        return problems;
    }
}
