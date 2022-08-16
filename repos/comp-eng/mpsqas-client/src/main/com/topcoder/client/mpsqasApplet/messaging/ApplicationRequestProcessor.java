package com.topcoder.client.mpsqasApplet.messaging;

/**
 * An interface for the Application Request Processor.
 *
 * @author mitalub
 */
public interface ApplicationRequestProcessor {

    public void sendApplication(String contents);

    public void sendReply(boolean accepted, String message);
}
