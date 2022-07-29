package com.topcoder.client.mpsqasApplet.messaging;

import java.util.ArrayList;

/**
 * Interface for pending application update response processors.
 *
 * @author mitalub
 */
public interface ApplicationUpdateResponseProcessor {

    public void processNewApplicationList(ArrayList applications);
}
