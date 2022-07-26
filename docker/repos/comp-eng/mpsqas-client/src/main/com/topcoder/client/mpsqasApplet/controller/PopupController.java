package com.topcoder.client.mpsqasApplet.controller;

/**
 * Interface for the PopupController, which handles pop up messages.
 */
public interface PopupController extends Controller {

    public void processOk();
    
    /**
     * Close the opened popup window
     */
    public void close();
}
