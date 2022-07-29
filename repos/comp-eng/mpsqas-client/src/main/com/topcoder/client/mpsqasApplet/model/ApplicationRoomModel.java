package com.topcoder.client.mpsqasApplet.model;

/**
 * Abstract class defining Application Room Model.
 *
 * @author mitalub
 */
public abstract class ApplicationRoomModel extends Model {

    public abstract void setType(int applicationType);

    public abstract int getType();
}
