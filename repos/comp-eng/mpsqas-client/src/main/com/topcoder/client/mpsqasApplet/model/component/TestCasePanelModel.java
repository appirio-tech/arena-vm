package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.TestCase;

/**
 * An abstract class containing methods for problem statement panel models.
 *
 * @author mitalub
 */
public abstract class TestCasePanelModel extends ComponentModel {

    public abstract void setCurrentCaseIndex(int currentCaseIndex);

    public abstract int getCurrentCaseIndex();

    public abstract void setCurrentCase(TestCase currentCase);

    public abstract TestCase getCurrentCase();

    public abstract void setComponentInformation(
            ComponentInformation componentInformation);

    public abstract ComponentInformation getComponentInformation();
}
