package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainLongProblemRoomModel;

import java.util.HashMap;

/**
 * Default implementation of MainLongProblemRoomModel.
 *
 * @author mktong
 */
public class MainLongProblemRoomModelImpl extends MainLongProblemRoomModel {

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
