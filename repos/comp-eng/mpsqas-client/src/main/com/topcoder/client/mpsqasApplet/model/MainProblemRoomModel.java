package com.topcoder.client.mpsqasApplet.model;

import java.util.HashMap;

/**
 * An abstract class for the main problem room model.
 *
 * @author mitalub
 */
public abstract class MainProblemRoomModel extends Model {

    public abstract void setProblems(HashMap problems);

    public abstract HashMap getProblems();
}
