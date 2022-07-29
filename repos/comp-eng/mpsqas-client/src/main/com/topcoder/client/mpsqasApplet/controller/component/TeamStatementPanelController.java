package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * An abstract class for problem statement panel controllers.
 *
 * @author mitalub
 */
public abstract class TeamStatementPanelController extends ComponentController {

    public abstract void processStatementChange();

    public abstract void processPartSelection();
}
