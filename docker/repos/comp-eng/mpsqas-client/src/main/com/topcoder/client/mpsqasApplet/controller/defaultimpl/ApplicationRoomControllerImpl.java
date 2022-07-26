package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.ApplicationRoomController;
import com.topcoder.client.mpsqasApplet.model.ApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.view.ApplicationRoomView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;

/**
 * Default implementation of Application Room Controller.
 *
 * @author mitalub
 */
public class ApplicationRoomControllerImpl implements ApplicationRoomController {

    ApplicationRoomModel model;
    ApplicationRoomView view;

    public void init() {
        model = MainObjectFactory.getApplicationRoomModel();
        view = MainObjectFactory.getApplicationRoomView();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processSendApplication() {
        MainObjectFactory.getApplicationRequestProcessor().sendApplication(
                view.getContents());
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending application to server...", false);
    }
}
