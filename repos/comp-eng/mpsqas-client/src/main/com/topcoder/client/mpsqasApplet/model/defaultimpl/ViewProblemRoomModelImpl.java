package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Default implmentation of ViewProblemRoomModel
 *
 * @author mitalub
 */
public class ViewProblemRoomModelImpl extends ViewProblemRoomModel {

    private ProblemInformation problemInformation;
    private boolean canSubmit;
    private boolean isStatementEditable;

    public void init() {
        problemInformation = null;
        canSubmit = true;
        isStatementEditable = true;
    }

    public ProblemInformation getProblemInformation() {
        return problemInformation;
    }

    public void setProblemInformation(ProblemInformation problemInformation) {
        this.problemInformation = problemInformation;
    }

    public ComponentInformation getComponentInformation() {
        ComponentInformation ci = (ComponentInformation) problemInformation.getProblemComponents()[0];
        return (ComponentInformation) problemInformation.getProblemComponents()[0];
    }

    public void setComponentInformation(ComponentInformation componentInformation) {
        this.problemInformation.setProblemComponents(
                new ProblemComponent[]{componentInformation});
    }

    public void setCanSubmit(boolean canSubmit) {
        this.canSubmit = canSubmit;
    }

    public boolean canSubmit() {
        return canSubmit;
    }

    public void setIsStatementEditable(boolean isStatementEditable) {
        this.isStatementEditable = isStatementEditable;
    }

    public boolean isStatementEditable() {
        return isStatementEditable;
    }
}
