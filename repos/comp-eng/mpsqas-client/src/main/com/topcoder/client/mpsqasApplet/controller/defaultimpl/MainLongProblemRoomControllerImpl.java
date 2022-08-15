package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainLongProblemRoomController;
import com.topcoder.client.mpsqasApplet.view.MainLongProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.MainLongProblemRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.ArrayList;

/**
 * Default implementation of MainLongProblemRoomController.
 *
 * @author mitalub
 */
public class MainLongProblemRoomControllerImpl implements MainLongProblemRoomController {

    private MainLongProblemRoomView view;
    private MainLongProblemRoomModel model;

    public void init() {
        view = MainObjectFactory.getMainLongProblemRoomView();
        model = MainObjectFactory.getMainLongProblemRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewProblem(int tableTypeId) {
        int index = view.getSelectedProblemIndex(tableTypeId);
        if (index != -1) {
            MainObjectFactory.getMoveRequestProcessor().viewLongProblem(
                    ((ProblemInformation) ((ArrayList) model.getProblems()
                    .get(new Integer(tableTypeId))).get(index)).getProblemId());
        }
    }

    public void processCreateProblem() {
        MainObjectFactory.getMoveRequestProcessor().createLongProblem();
    }
}
