package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * An abstract class for long problem statement panel controllers.
 *
 * @author mktong
 */
public abstract class LongStatementPanelController extends ComponentController {

    public abstract boolean processParamTypeChange();

    public abstract void processStatementChange();

    public abstract void processPartSelection();

    public abstract void buildSpecifiedConstraints();
}
