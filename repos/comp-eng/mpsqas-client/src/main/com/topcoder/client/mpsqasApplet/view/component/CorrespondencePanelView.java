package com.topcoder.client.mpsqasApplet.view.component;

/**
 * An abstract class for the Correspondence Panel View.
 */
public abstract class CorrespondencePanelView extends ComponentView {

    public abstract int getSelectedMessageIndex();

    public abstract String getMessageText();

    public abstract int[] getSelectedReceiversIndices();
}
