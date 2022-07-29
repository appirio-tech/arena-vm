package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * Abstract class defining methods for the all soluton panel controller.
 *
 * @author mitalub
 */
public abstract class AllSolutionPanelController extends ComponentController {

    public abstract void processSystemTestAll();

    public abstract void processTestAll();

    public abstract void processSolutionSelected();
}
