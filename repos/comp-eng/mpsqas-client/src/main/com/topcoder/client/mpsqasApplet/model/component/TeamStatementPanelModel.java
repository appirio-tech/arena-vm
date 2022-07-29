package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.shared.problem.Constraint;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

/**
 * An abstract class containing methods for problem statement panel models.
 *
 * @author mitalub
 */
public abstract class TeamStatementPanelModel extends ComponentModel {

    public abstract void setCurrentPart(int currentPart);

    public abstract int getCurrentPart();

    public abstract void setProblemInformation(
            ProblemInformation problemInformation);

    public abstract ProblemInformation getProblemInformation();
}
