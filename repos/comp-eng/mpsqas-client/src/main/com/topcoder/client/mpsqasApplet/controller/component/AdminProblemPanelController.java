package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * Abstract class defining methods for the admin problem panel controller.
 *
 * @author mitalub
 */
public abstract class AdminProblemPanelController extends ComponentController {

    public abstract void processSubmit();

    public abstract void processAddTester();

    public abstract void processRemoveTester();
}
