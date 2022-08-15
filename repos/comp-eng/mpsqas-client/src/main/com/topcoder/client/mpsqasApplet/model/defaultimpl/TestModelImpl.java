package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.TestModel;
import com.topcoder.shared.problem.DataType;

/**
 * Default implementation of TestModel.
 *
 * @author mitalub
 */
public class TestModelImpl extends TestModel {

    int testType;
    boolean isVisible;
    DataType[] dataTypes;

    public void init() {
        isVisible = false;
        dataTypes = new DataType[0];
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setDataTypes(DataType[] dataTypes) {
        this.dataTypes = dataTypes;
    }

    public DataType[] getDataTypes() {
        return dataTypes;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public int getTestType() {
        return testType;
    }
}
