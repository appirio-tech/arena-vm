package com.topcoder.client.mpsqasApplet.messaging;

import java.util.ArrayList;

/**
 * Interface for pending problem update response processors.
 *
 * @author mitalub
 */
public interface PendingUpdateResponseProcessor {

    public void processNewProposalList(ArrayList proposals);

    public void processNewSubmissionList(ArrayList submissions);
}
