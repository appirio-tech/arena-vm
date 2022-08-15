package com.topcoder.client.mpsqasApplet.messaging;

/**
 * Interface for an internal status message request processor.
 *
 * @author mitalub
 */
public interface IStatusMessageRequestProcessor {

    public void addMessage(String message, boolean urgent);
}
