package com.topcoder.client.mpsqasApplet.model;

import java.util.ArrayList;

/**
 * An abstract class defining the Main Contest Room Model.
 *
 * @author mitalub
 */
public abstract class MainContestRoomModel extends Model {

    public abstract void setContests(ArrayList contests);

    public abstract ArrayList getContests();
}
