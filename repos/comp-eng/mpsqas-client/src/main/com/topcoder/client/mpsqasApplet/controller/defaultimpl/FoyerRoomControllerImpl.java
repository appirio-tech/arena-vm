package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.FoyerRoomController;
import com.topcoder.client.mpsqasApplet.view.FoyerRoomView;
import com.topcoder.client.mpsqasApplet.model.FoyerRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.NamedIdItem;

/**
 * Default implementation of foyer room controller.
 *
 * @author mitalub
 */
public class FoyerRoomControllerImpl implements FoyerRoomController {

    private FoyerRoomView view;
    private FoyerRoomModel model;

    public void init() {
        view = MainObjectFactory.getFoyerRoomView();
        model = MainObjectFactory.getFoyerRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewProblem() {
        int index = view.getSelectedProblemIndex();
        if (index != -1) {
            NamedIdItem thing = (NamedIdItem) model.getProblemList().get(index);
            if (thing.getType() == NamedIdItem.SINGLE_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewProblem(thing.getId());
            } else if (thing.getType() == NamedIdItem.TEAM_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewTeamProblem(thing.getId());
            } else if (thing.getType() == NamedIdItem.LONG_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewLongProblem(thing.getId());
            } else if (thing.getType() == NamedIdItem.COMPONENT) {
                MainObjectFactory.getMoveRequestProcessor().viewComponent(thing.getId());
            }
        }
    }
}
