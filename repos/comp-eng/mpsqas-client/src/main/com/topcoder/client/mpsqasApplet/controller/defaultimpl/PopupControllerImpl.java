package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.PopupController;
import com.topcoder.client.mpsqasApplet.messaging.PopupResponseProcessor;
import com.topcoder.client.mpsqasApplet.model.PopupModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.PopupView;

/**
 * Default implementation of PopupController, which handles popping messages
 * up to the user.
 */
public class PopupControllerImpl implements PopupController,
        PopupResponseProcessor {

    private PopupModel model;
    private PopupView view;

    public void init() {
        model = MainObjectFactory.getPopupModel();
        view = MainObjectFactory.getPopupView();

        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.POPUP);
    }

    public void takeOffHold() {
    }

    public void placeOnHold() {
    }

    /**
     * Handles a user's clicking on ok
     */
    public void processOk() {
        //hide test window.
        model.setIsVisible(false);
        model.notifyWatchers();
    }

    /**
     * Close popup window
     */
    public void close() {
        model.setIsVisible(false);
        model.notifyWatchers(UpdateTypes.VISIBILITY);
    }
    
    /**
     * Makes the pop up visible and sets it text.
     *
     */
    public void popupMessage(String message) {
        model.setIsVisible(true);
        model.setText(message);
        model.notifyWatchers();
    }
}
