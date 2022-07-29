package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.Correspondence;

/**
 * Interface for CorrespondenceResponseProcessor's.
 */
public interface CorrespondenceResponseProcessor {

    public void processNewCorrespondence(Correspondence correspondence);
}
