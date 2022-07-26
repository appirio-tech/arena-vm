package com.topcoder.client.mpsqasApplet.model;

import java.util.ArrayList;

/**
 * An abstract class defining the Main Application Room Model.
 *
 * @author mitalub
 */
public abstract class MainApplicationRoomModel extends Model {

    public abstract void setApplications(ArrayList contests);

    public abstract ArrayList getApplications();
}
