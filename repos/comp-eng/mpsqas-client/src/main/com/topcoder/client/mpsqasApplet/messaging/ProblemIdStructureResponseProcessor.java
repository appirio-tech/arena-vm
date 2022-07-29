package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.ProblemIdStructure;

/**
 * Interface for problem id structure response processors.
 *
 * @author mitalub
 */
public interface ProblemIdStructureResponseProcessor {

    public void processNewIdStructure(ProblemIdStructure idStructure);
}
