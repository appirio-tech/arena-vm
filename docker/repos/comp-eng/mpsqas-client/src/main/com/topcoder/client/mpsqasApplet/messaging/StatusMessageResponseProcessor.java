package com.topcoder.client.mpsqasApplet.messaging;

/**
 * Interface for response processors interested in status messages.
 *
 * @author mitalub
 */
public interface StatusMessageResponseProcessor {

    public void processNewMessage(String message, boolean urgent);
}
