package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainContestRoomController;
import com.topcoder.client.mpsqasApplet.view.MainContestRoomView;
import com.topcoder.client.mpsqasApplet.model.MainContestRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.ContestInformation;

/**
 * Default implementation of Main Contest Room controller.
 *
 * @author mitalub
 */
public class MainContestRoomControllerImpl implements MainContestRoomController {

    private MainContestRoomView view;
    private MainContestRoomModel model;

    public void init() {
        view = MainObjectFactory.getMainContestRoomView();
        model = MainObjectFactory.getMainContestRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewContest() {
        int index = view.getSelectedContestIndex();
        if (index != -1) {
            MainObjectFactory.getMoveRequestProcessor().viewContest(
                    ((ContestInformation) model.getContests().get(index))
                    .getRoundId());
        }
    }
}
