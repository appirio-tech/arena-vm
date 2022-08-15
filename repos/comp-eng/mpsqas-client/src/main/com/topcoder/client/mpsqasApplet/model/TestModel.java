package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.shared.problem.DataType;

/**
 * Abstract class for the TestModel.
 *
 * @author mitalub
 */
public abstract class TestModel extends Model {

    public abstract void setIsVisible(boolean isVisible);

    public abstract boolean isVisible();

    public abstract void setDataTypes(DataType[] dataTypes);

    public abstract DataType[] getDataTypes();

    public abstract void setTestType(int testType);

    public abstract int getTestType();
}
