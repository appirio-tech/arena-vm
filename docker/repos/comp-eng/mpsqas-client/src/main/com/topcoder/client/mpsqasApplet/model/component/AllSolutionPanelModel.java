package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.shared.problem.DataType;

import java.util.ArrayList;

/**
 * Abstract class defining methods for the all solution panel model.
 *
 * @author mitalub
 */
public abstract class AllSolutionPanelModel extends ComponentModel {

    public abstract void setParamTypes(DataType[] paramTypes);

    public abstract DataType[] getParamTypes();

    public abstract void setSolutions(ArrayList solutions);

    public abstract ArrayList getSolutions();
}
