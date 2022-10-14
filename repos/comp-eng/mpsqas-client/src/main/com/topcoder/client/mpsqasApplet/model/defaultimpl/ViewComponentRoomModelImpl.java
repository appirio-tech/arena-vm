package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ViewComponentRoomModel;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Implementation of the view component room model.
 *
 * @author mitalub
 */
public class ViewComponentRoomModelImpl extends ViewComponentRoomModel {

    private ComponentInformation componentInformation;
    private boolean canSubmit;
    private boolean isStatementEditable;
    private ProblemInformation problemInformation;

    public void init() {
        componentInformation = null;
        problemInformation = null;
        canSubmit = true;
        isStatementEditable = false;
    }

    public void setComponentInformation(ComponentInformation info) {
        this.componentInformation = info;
    }

    public ComponentInformation getComponentInformation() {
        return componentInformation;
    }

    public void setIsStatementEditable(boolean isStatementEditable) {
        this.isStatementEditable = isStatementEditable;
    }

    public boolean isStatementEditable() {
        return isStatementEditable;
    }

    public void setCanSubmit(boolean canSubmit) {
        this.canSubmit = canSubmit;
    }

    public boolean canSubmit() {
        return canSubmit;
    }
}
