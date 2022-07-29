package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.AllSolutionPanelModel;
import com.topcoder.shared.problem.DataType;

import java.util.ArrayList;

public class AllSolutionPanelModelImpl extends AllSolutionPanelModel {

    private DataType[] paramTypes;
    private ArrayList solutions;

    public void init() {
    }

    public void setParamTypes(DataType[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public DataType[] getParamTypes() {
        return paramTypes;
    }

    public void setSolutions(ArrayList solutions) {
        this.solutions = solutions;
    }

    public ArrayList getSolutions() {
        return solutions;
    }
}
