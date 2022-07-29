package com.topcoder.client.mpsqasApplet.controller;

/**
 * Interface for the TestController, which handles user testing.
 */
public interface TestController extends Controller {

    public void processTest();

    /**
     * Close the opened test window
     */
    public void close();
}
