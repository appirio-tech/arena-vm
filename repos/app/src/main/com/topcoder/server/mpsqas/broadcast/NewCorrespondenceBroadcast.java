package com.topcoder.server.mpsqas.broadcast;

import com.topcoder.netCommon.mpsqas.Correspondence;

/**
 * @author mitalub
 */
public class NewCorrespondenceBroadcast extends Broadcast {

    public static final int PROBLEM_CORRESPONDENCE = 1;
    public static final int COMPONENT_CORRESPONDENCE = 2;

    private int type;
    private int id;
    private Correspondence correspondence;

    public NewCorrespondenceBroadcast(Correspondence correspondence,
            int type, int id) {
        this.correspondence = correspondence;
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public Correspondence getCorrespondence() {
        return this.correspondence;
    }
}
