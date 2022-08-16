package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;

public final class TestCase implements Serializable {

    private int componentId;
    private int testCaseId;
    private ExpectedResult expectedResultAttr;
    private int testOrder;
    private boolean modified;
    private ArrayList testCaseArgs;

    public TestCase() {
        componentId = 0;
        testCaseId = 0;
        expectedResultAttr = new ExpectedResult();
        testOrder = 0;
        modified = false;
        testCaseArgs = new ArrayList();
    }

    // set
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public void setExpectedResult(ExpectedResult expectedResultAttr) {
        this.expectedResultAttr = expectedResultAttr;
    }

    public void setTestOrder(int testOrder) {
        this.testOrder = testOrder;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setTestCaseArgs(ArrayList testCaseArgs) {
        this.testCaseArgs = testCaseArgs;
    }

    // get
    public int getComponentId() {
        return componentId;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public ExpectedResult getExpectedResult() {
        return expectedResultAttr;
    }

    public int getTestOrder() {
        return testOrder;
    }

    public boolean getModified() {
        return modified;
    }

    public ArrayList getTestCaseArgs() {
        return testCaseArgs;
    }

}

