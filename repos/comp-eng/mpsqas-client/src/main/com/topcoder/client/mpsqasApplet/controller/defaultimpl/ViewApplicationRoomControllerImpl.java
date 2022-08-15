package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.ViewApplicationRoomController;
import com.topcoder.client.mpsqasApplet.view.ViewApplicationRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;

/**
 * Default implementation of View Application Room controller.
 *
 * @author mitalub
 */
public class ViewApplicationRoomControllerImpl
        implements ViewApplicationRoomController {

    private ViewApplicationRoomView view;
    private ViewApplicationRoomModel model;

    public void init() {
        view = MainObjectFactory.getViewApplicationRoomView();
        model = MainObjectFactory.getViewApplicationRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processReply() {
        MainObjectFactory.getApplicationRequestProcessor().sendReply(
                view.accepted(), view.getMessage());
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Application reply sent to server.", false);
    }
}
