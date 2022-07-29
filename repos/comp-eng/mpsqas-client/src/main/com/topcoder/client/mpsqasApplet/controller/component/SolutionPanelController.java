package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * Abstract class defining methods for the soluton panel controller.
 *
 * @author mitalub
 */
public abstract class SolutionPanelController extends ComponentController {

    public abstract void processCompile();

    public abstract void processTest();

    public abstract void processSolutionChange();
    
    /**
     * Called when the solutions language may be changed
     */
    public abstract void processLanguageChange();
    
    /**
     * Runs system tests against the current solution
     */
    public abstract void processSystemTest();
}
