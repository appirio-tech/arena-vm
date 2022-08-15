package com.topcoder.client.mpsqasApplet.model;

import java.util.ArrayList;

import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.ComponentInformation;

/**
 * An abstract class definining the methods in a View Long Problem Room Model.
 *
 * @author mktong
 */
public abstract class ViewLongProblemRoomModel extends Model {

    public abstract ProblemInformation getProblemInformation();

    public abstract void setProblemInformation(ProblemInformation info);

    public abstract ComponentInformation getComponentInformation();

    public abstract void setComponentInformation(
            ComponentInformation componentInformation);

    public abstract void setCanSubmit(boolean canSubmit);

    public abstract boolean canSubmit();

    public abstract void setIsStatementEditable(boolean isStatementEditable);

    public abstract boolean isStatementEditable();
}
