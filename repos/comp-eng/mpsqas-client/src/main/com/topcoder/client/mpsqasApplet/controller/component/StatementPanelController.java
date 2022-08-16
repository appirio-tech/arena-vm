package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * An abstract class for problem statement panel controllers.
 *
 * @author mitalub
 */
public abstract class StatementPanelController extends ComponentController {

    public abstract void processParamTypeChange();

    public abstract void processStatementChange();

    public abstract void processPartSelection();

    public abstract void buildSpecifiedConstraints();
}
