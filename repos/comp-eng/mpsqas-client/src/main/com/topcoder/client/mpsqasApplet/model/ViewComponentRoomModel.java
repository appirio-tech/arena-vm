package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Abstract class for the view component room model.
 *
 * @author mitalub
 */
public abstract class ViewComponentRoomModel extends Model {

    public abstract void setComponentInformation(ComponentInformation info);

    public abstract ComponentInformation getComponentInformation();

    public abstract void setCanSubmit(boolean canSubmit);

    public abstract boolean canSubmit();

    public abstract void setIsStatementEditable(boolean statementEditable);

    public abstract boolean isStatementEditable();
}
