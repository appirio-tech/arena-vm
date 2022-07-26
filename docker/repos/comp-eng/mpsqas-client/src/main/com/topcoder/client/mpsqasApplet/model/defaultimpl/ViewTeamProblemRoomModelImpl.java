package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.model.ViewTeamProblemRoomModel;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.ArrayList;

/**
 * Default implmentation of ViewTeamProblemRoomModel
 *
 * @author mitalub
 */
public class ViewTeamProblemRoomModelImpl extends ViewTeamProblemRoomModel {

    private ProblemInformation problemInformation;
    private boolean canSubmit;
    private boolean isStatementEditable;

    public void init() {
        problemInformation = null;
        canSubmit = true;
        isStatementEditable = false;
    }

    public ProblemInformation getProblemInformation() {
        return problemInformation;
    }

    public void setProblemInformation(
            ProblemInformation problemInformation) {
        this.problemInformation = problemInformation;
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
