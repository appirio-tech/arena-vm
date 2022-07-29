package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainProblemRoomModel;

import java.util.HashMap;

/**
 * Default implementation of MainProblemRoomModel.
 *
 * @author mitalub
 */
public class MainProblemRoomModelImpl extends MainProblemRoomModel {

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
