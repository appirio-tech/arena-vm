package com.topcoder.client.mpsqasApplet.messaging;

/**
 * An interface for application response processors.
 */
public interface ApplicationResponseProcessor {

    public void processApplicationReply(boolean success, String message);
}
