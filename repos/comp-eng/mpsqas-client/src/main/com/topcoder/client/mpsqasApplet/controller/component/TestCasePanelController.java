package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * An abstract class for problem statement panel controllers.
 *
 * @author mitalub
 */
public abstract class TestCasePanelController extends ComponentController {

    public abstract void processAddTestCase();

    public abstract void processDeleteTestCase();

    public abstract void processTestTestCase();

    public abstract void processTestCaseSelected();

    public abstract void processFlagsChange();

    public abstract void processCurrentCaseChange();

    public abstract void processAddRandomTestCases();

    public abstract void processMoveTestCaseUp();

    public abstract void processMoveTestCaseDown();
}
