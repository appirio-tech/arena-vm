package com.topcoder.client.mpsqasApplet.messaging;

/**
 * Interface for a problem update response processor, which processes responses
 * related to problems.
 */
public interface ProblemUpdateResponseProcessor {

    public void processProblemModified(String modifierName);
}
