/*
 * Copyright (C) 2006 - 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.LongStatementPanelModel;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.Constraint;

/**
 * <p>
 * this is the long statement panel model.
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 * <li>Added {@link #SETTINGS} field.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LongStatementPanelModelImpl extends LongStatementPanelModel {

    public final static int DEFINITION = 1,
    METHODS = 2,
    EXPOSED_METHODS = 3,
    INTRODUCTION = 4,
    NOTES = 5,
    CONSTRAINTS = 6,
    EXAMPLES = 7,
    SETTINGS = 8;

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
