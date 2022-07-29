package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.Correspondence;

/**
 * Interface defining methods for a CorrespondenceRequestProcessor.
 *
 * @author mitalub
 */
public interface CorrespondenceRequestProcessor {

    public void sendCorrespondence(Correspondence correspondence);
}
