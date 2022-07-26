package com.topcoder.client.mpsqasApplet.messaging;

import java.util.ArrayList;

/**
 * An interface defining methods for a ContestRequestProcessor, requests
 * relating to contests.
 *
 * @author mitalub
 */
public interface ContestRequestProcessor {

    public void verifyContest();

    public void scheduleProblems(ArrayList scheduledProblems);
}
