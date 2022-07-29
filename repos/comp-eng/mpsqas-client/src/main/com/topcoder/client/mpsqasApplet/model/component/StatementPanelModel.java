package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.Constraint;

/**
 * An abstract class containing methods for problem statement panel models.
 *
 * @author mitalub
 */
public abstract class StatementPanelModel extends ComponentModel {

    public abstract void setCurrentPart(int currentPart);

    public abstract int getCurrentPart();

    public abstract void setSpecifiedConstraints(
            Constraint[] specifiedConstraints);

    public abstract Constraint[] getSpecifiedConstraints();

    public abstract void setComponentInformation(
            ComponentInformation componentInformation);

    public abstract ComponentInformation getComponentInformation();
}
