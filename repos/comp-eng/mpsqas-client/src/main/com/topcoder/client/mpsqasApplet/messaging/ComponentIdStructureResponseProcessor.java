package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.ComponentIdStructure;

/**
 * Interface for component id structure response processors.
 *
 * @author mitalub
 */
public interface ComponentIdStructureResponseProcessor {

    public void processNewIdStructure(ComponentIdStructure idStructure);
}
