package com.topcoder.client.mpsqasApplet.controller;

/**
 * Interface for Main User Room controller.
 *
 * @author mitalub
 */
public interface MainUserRoomController extends Controller {

    /** Handle's selection of a user for viewing more details. */
    public abstract void processViewUser();

    /** Handle's pressing of Pay button. */
    public abstract void processPay();
}
