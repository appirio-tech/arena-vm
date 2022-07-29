package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.ApprovalPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ApprovalPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.ApprovalPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.messaging.ProblemRequestProcessor;
import com.topcoder.client.mpsqasApplet.util.Watchable;

/**
 * Default implementation of the approval panel controller.
 *
 * @author mitalub
 */
public class ApprovalPanelControllerImpl extends ApprovalPanelController {

    private ApprovalPanelModel model;
    private ApprovalPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (ApprovalPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (ApprovalPanelView) view;
    }

    public void processSubmit() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending reply to server...", false);
        MainObjectFactory.getProblemRequestProcessor().submitPendingReply(
                view.isAccepted(), view.getMessage());
    }

    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
