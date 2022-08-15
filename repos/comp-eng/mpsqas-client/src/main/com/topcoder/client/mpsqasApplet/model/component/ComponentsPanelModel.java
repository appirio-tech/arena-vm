package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.ArrayList;

/**
 * Abstract class defining methods for a components view model.
 *
 * @author mitalub
 */
public abstract class ComponentsPanelModel extends ComponentModel {

    public abstract void setWebServiceObjects(ArrayList webServiceObjects);

    public abstract ArrayList getWebServiceObjects();

    public abstract void setWebServiceTableData(Object[][] data);

    public abstract Object[][] getWebServiceTableData();

    public abstract void setComponents(ArrayList components);

    public abstract ArrayList getComponents();

    public abstract void setProblemInformation(
            ProblemInformation problemInformation);

    public abstract ProblemInformation getProblemInformation();

    public abstract void setIsEditable(boolean isEditable);

    public abstract boolean isEditable();

    public abstract void setCanMove(boolean canMove);

    public abstract boolean canMove();
}
