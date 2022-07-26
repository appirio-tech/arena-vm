package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainProblemRoomController;
import com.topcoder.client.mpsqasApplet.view.MainProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.MainProblemRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.ArrayList;

/**
 * Default implementation of MainProblemRoomController.
 *
 * @author mitalub
 */
public class MainProblemRoomControllerImpl implements MainProblemRoomController {

    private MainProblemRoomView view;
    private MainProblemRoomModel model;

    public void init() {
        view = MainObjectFactory.getMainProblemRoomView();
        model = MainObjectFactory.getMainProblemRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewProblem(int tableTypeId) {
        int index = view.getSelectedProblemIndex(tableTypeId);
        if (index != -1) {
            MainObjectFactory.getMoveRequestProcessor().viewProblem(
                    ((ProblemInformation) ((ArrayList) model.getProblems()
                    .get(new Integer(tableTypeId))).get(index)).getProblemId());
        }
    }

    public void processCreateProblem() {
        MainObjectFactory.getMoveRequestProcessor().createProblem();
    }
}
