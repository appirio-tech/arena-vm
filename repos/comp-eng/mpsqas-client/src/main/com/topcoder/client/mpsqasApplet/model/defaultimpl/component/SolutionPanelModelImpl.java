package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.client.mpsqasApplet.model.component.SolutionPanelModel;
import com.topcoder.netCommon.mpsqas.SolutionInformation;
import com.topcoder.shared.problem.DataType;

public class SolutionPanelModelImpl extends SolutionPanelModel {

    private DataType[] paramTypes;
    private SolutionInformation solution;
    private String className;
    private ComponentInformation component;

    public void init() {
        paramTypes = new DataType[0];
        className = "";
        solution = null;
        component = null;
    }

    public void setComponentInformation(ComponentInformation component) {
        this.component = component;
    }

    public ComponentInformation getComponentInformation() {
        return component;
    }

    public void setParamTypes(DataType[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public DataType[] getParamTypes() {
        return paramTypes;
    }

    public void setSolutionInformation(SolutionInformation solution) {
        this.solution = solution;
    }

    public SolutionInformation getSolutionInformation() {
        return solution;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
