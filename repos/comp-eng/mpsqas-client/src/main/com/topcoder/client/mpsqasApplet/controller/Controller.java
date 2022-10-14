package com.topcoder.client.mpsqasApplet.controller;

/**
 * An interface for all controllers.
 *
 * @author mitalub
 */
public interface Controller {

    /**
     * Called immediatly after Object's creation.  Put init stuff here and
     * not in constructor (will cause a stack overflow exception).
     */
    public void init();

    /**
     *  Called when Object is no longer active (Controller is not controlling
     *  anything currently in view of the user.
     */
    public void placeOnHold();

    /**
     * Called when Object becomes the active Controller.
     */
    public void takeOffHold();
}
