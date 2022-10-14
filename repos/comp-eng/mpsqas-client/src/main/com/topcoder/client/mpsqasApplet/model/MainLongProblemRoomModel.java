package com.topcoder.client.mpsqasApplet.model;

import java.util.HashMap;

/**
 * An abstract class for the main long problem room model.
 *
 * @author mktong
 */
public abstract class MainLongProblemRoomModel extends Model {

    public abstract void setProblems(HashMap problems);

    public abstract HashMap getProblems();
}
