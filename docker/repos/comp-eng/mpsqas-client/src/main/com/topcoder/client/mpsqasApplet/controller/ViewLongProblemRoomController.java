package com.topcoder.client.mpsqasApplet.controller;

/**
 * Interface for a View Long Problem Room controller.
 *
 * @author mktong
 */
public interface ViewLongProblemRoomController extends Controller {

    public void processSubmit();

    public void processSaveStatement();
    
    /**
     * Cancels all pending scheduled test for the user
     */
    public void processCancelTests();
}
