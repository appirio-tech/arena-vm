package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.netCommon.mpsqas.ProblemInformation;

/**
 * Abstract class for the view team problem room model.
 *
 * @author mitalub
 */
public abstract class ViewTeamProblemRoomModel extends Model {

    public abstract void setProblemInformation(ProblemInformation
            problemInformation);

    public abstract ProblemInformation getProblemInformation();

    public abstract void setCanSubmit(boolean canSubmit);

    public abstract boolean canSubmit();

    public abstract void setIsStatementEditable(boolean isStatementEditable);

    public abstract boolean isStatementEditable();
}
