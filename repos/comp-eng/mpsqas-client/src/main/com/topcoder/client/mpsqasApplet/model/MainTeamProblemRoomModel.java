package com.topcoder.client.mpsqasApplet.model;

import java.util.HashMap;

/**
 * An abstract class for the main team problem room model.
 *
 * @author mitalub
 */
public abstract class MainTeamProblemRoomModel extends Model {

    public abstract void setProblems(HashMap problems);

    public abstract HashMap getProblems();
}
