package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.SolutionInformation;
import com.topcoder.shared.problem.DataType;
import com.topcoder.netCommon.mpsqas.ComponentInformation;

/**
 * Abstract class defining methods for the solution panel model.
 *
 * @author mitalub
 */
public abstract class SolutionPanelModel extends ComponentModel {

    public abstract void setComponentInformation(ComponentInformation component);

    public abstract ComponentInformation getComponentInformation();

    public abstract void setParamTypes(DataType[] paramTypes);

    public abstract DataType[] getParamTypes();

    public abstract void setSolutionInformation(SolutionInformation info);

    public abstract SolutionInformation getSolutionInformation();

    public abstract void setClassName(String className);

    public abstract String getClassName();
}
