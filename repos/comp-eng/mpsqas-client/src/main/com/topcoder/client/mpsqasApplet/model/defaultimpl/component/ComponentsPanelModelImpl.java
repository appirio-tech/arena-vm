package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.client.mpsqasApplet.model.component.ComponentsPanelModel;

import java.util.ArrayList;

/**
 * Default implementation of the components panel model.
 *
 * @author mitalub
 */
public class ComponentsPanelModelImpl extends ComponentsPanelModel {

    private ArrayList webServiceObjects;
    private Object[][] data;
    private ProblemInformation problemInformation;
    private ArrayList components;
    private boolean isEditable;
    private boolean canMove;

    public void init() {
        canMove = true;
        webServiceObjects = new ArrayList();
        data = null;
        components = new ArrayList();
        problemInformation = null;
        isEditable = true;
    }

    public void setWebServiceObjects(ArrayList webServiceObjects) {
        this.webServiceObjects = webServiceObjects;
    }

    public ArrayList getWebServiceObjects() {
        return webServiceObjects;
    }

    public void setWebServiceTableData(Object[][] data) {
        this.data = data;
    }

    public Object[][] getWebServiceTableData() {
        return data;
    }

    public void setComponents(ArrayList components) {
        this.components = components;
    }

    public ArrayList getComponents() {
        return components;
    }

    public void setProblemInformation(ProblemInformation problemInformation) {
        this.problemInformation = problemInformation;
    }

    public ProblemInformation getProblemInformation() {
        return problemInformation;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean canMove() {
        return canMove;
    }
}
