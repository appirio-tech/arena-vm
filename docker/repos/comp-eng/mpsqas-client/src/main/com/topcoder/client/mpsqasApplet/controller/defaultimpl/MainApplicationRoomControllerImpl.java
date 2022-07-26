package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainApplicationRoomController;
import com.topcoder.client.mpsqasApplet.messaging.MoveRequestProcessor;
import com.topcoder.client.mpsqasApplet.model.MainApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MainApplicationRoomView;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;

import java.util.ArrayList;

/**
 * Default implementation of main application room controller.
 *
 * @author mitalub
 */
public class MainApplicationRoomControllerImpl
        implements MainApplicationRoomController {

    private MainApplicationRoomView view;
    private MainApplicationRoomModel model;

    public void init() {
        view = MainObjectFactory.getMainApplicationRoomView();
        model = MainObjectFactory.getMainApplicationRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    /** Moves to the view application room for the application. */
    public void processViewApplication() {
        int index = view.getSelectedApplicationIndex();
        if (index != -1)
            MainObjectFactory.getMoveRequestProcessor().viewApplication((
                    (ApplicationInformation) model.getApplications().get(index))
                    .getId());
    }
}
