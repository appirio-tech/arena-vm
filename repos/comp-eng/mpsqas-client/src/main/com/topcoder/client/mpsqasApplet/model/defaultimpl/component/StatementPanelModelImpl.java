/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.StatementPanelModel;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.Constraint;

/**
 * <p>
 * this is the srm statement panel model.
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Added {@link #SETTINGS} field.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.0
 */
public class StatementPanelModelImpl extends StatementPanelModel {

    public final static int DEFINITION = 1,
    INTRODUCTION = 2,
    NOTES = 3,
    CONSTRAINTS = 4,
    EXAMPLES = 5,
    SETTINGS = 6;

    private Constraint[] specifiedConstraints;
    private int currentPart;
    private ComponentInformation componentInformation;

    public void init() {
    }

    public void setCurrentPart(int currentPart) {
        this.currentPart = currentPart;
    }

    public int getCurrentPart() {
        return currentPart;
    }

    public void setSpecifiedConstraints(Constraint[] specifiedConstraints) {
        this.specifiedConstraints = specifiedConstraints;
    }

    public Constraint[] getSpecifiedConstraints() {
        return specifiedConstraints;
    }

    public void setComponentInformation(ComponentInformation componentInformation) {
        this.componentInformation = componentInformation;
    }

    public ComponentInformation getComponentInformation() {
        return componentInformation;
    }
}
