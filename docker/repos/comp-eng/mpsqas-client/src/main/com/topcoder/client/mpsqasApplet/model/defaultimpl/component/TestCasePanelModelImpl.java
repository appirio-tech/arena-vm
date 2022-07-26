package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.TestCasePanelModel;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.netCommon.mpsqas.ComponentInformation;

public class TestCasePanelModelImpl extends TestCasePanelModel {

    private TestCase currentCase;
    private int currentCaseIndex;
    private ComponentInformation component;

    public void init() {
        currentCase = null;
        currentCaseIndex = -1;
    }

    public void setCurrentCase(TestCase currentCase) {
        this.currentCase = currentCase;
    }

    public TestCase getCurrentCase() {
        return currentCase;
    }

    public void setCurrentCaseIndex(int currentCaseIndex) {
        this.currentCaseIndex = currentCaseIndex;
    }

    public int getCurrentCaseIndex() {
        return currentCaseIndex;
    }

    public void setComponentInformation(ComponentInformation component) {
        this.component = component;
    }

    public ComponentInformation getComponentInformation() {
        return component;
    }
}
