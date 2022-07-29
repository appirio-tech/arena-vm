package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.ContestInformation;

/**
 * An interface specifying methods to handle contest updates.
 *
 * @author mitalub
 */
public interface ContestUpdateResponseProcessor {

    public void processNewSchedule(ContestInformation contestInfo);
}
