package com.topcoder.client.mpsqasApplet.controller;

/**
 * An interface for the Foyer Room Controller.
 *
 * @author mitalub
 */
public interface FoyerRoomController extends Controller {

    /**
     * Called by View when user requests to open a problem with
     * unread correspondence.
     */
    public void processViewProblem();
}
