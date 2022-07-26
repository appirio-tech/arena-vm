package com.topcoder.client.mpsqasApplet.controller;

import com.topcoder.client.mpsqasApplet.messaging.LoginResponseProcessor;

/**
 * An interface for the Login Room Controller.
 *
 * @author mitalub
 */
public interface LoginRoomController extends Controller {

    /** Handles user pressing Login button. */
    void processLoginPressed();
}
