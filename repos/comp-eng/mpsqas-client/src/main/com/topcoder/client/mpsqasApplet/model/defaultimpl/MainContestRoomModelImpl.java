package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainContestRoomModel;

import java.util.ArrayList;

/**
 * Default implementation of Main Contest Room model.
 *
 * @author mitalub
 */
public class MainContestRoomModelImpl extends MainContestRoomModel {

    ArrayList contests;

    public void init() {
        contests = new ArrayList();
    }

    public void setContests(ArrayList contests) {
        this.contests = contests;
    }

    public ArrayList getContests() {
        return contests;
    }
}
