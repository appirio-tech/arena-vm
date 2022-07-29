package com.topcoder.client.mpsqasApplet.model;

import java.util.ArrayList;

/**
 * An abstract class interface for the Foyer Room Model.
 *
 * @author mitalub
 */
public abstract class FoyerRoomModel extends Model {

    public abstract void setProblemList(ArrayList problems);

    public abstract ArrayList getProblemList();

    public abstract String[] getProblemNameList();

    public abstract void setIsFullRoom(boolean fullRoom);

    public abstract boolean isFullRoom();
}
