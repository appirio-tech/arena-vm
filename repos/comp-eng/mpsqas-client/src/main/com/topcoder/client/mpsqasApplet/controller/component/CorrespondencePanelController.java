package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * An abstract class for a Correspondence Panel Controller.
 *
 * @author mitalub
 */
public abstract class CorrespondencePanelController extends ComponentController {

    /**
     * Called when the user presses the Send button to send a message.
     */
    public abstract void processSendMessage();

    /**
     * Called when the user selects a message for viewing.
     */
    public abstract void processMessageSelected();

    /**
     * Called when the user presses the "Compose" button to write a new
     * message.
     */
    public abstract void processNewMessage();

    /**
     * Called when the user presses the "Reply" button to write a new message.
     */
    public abstract void processReplyMessage();

    /**
     * Called when the user presses the "Cancel" button to cancel the editing
     * message.
     */
    public abstract void processCancelMessage();
}
