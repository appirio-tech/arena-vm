package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.Constraint;

/**
 * An abstract class containing methods for long problem statement panel models.
 *
 * @author mktong
 */
public abstract class LongStatementPanelModel extends ComponentModel {

    public abstract void setCurrentPart(int currentPart);

    public abstract int getCurrentPart();

    public abstract void setSpecifiedConstraints(
            Constraint[] specifiedConstraints);

    public abstract Constraint[] getSpecifiedConstraints();

    public abstract void setComponentInformation(
            ComponentInformation componentInformation);

    public abstract ComponentInformation getComponentInformation();
}
