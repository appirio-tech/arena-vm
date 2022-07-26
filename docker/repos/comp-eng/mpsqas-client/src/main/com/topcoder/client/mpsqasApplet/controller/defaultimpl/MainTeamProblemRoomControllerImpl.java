package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainTeamProblemRoomController;
import com.topcoder.client.mpsqasApplet.view.MainTeamProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.MainTeamProblemRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.HiddenValue;

/**
 * Default implementation of MainTeamProblemRoomController.
 *
 * @author mitalub
 */
public class MainTeamProblemRoomControllerImpl implements MainTeamProblemRoomController {

    private MainTeamProblemRoomView view;
    private MainTeamProblemRoomModel model;

    public void init() {
        view = MainObjectFactory.getMainTeamProblemRoomView();
        model = MainObjectFactory.getMainTeamProblemRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewProblem(int tableTypeId) {
        Object[] problemPath = view.getSelectedProblemPath(tableTypeId);

        if (problemPath != null) {
            //if the length of the path is 2, we have just the tree root (all problems
            //and a problem root, so we are looking at a the main problem
            if (problemPath.length == 2) {
                //the team problem id is in a hidden value in the first column
                MainObjectFactory.getMoveRequestProcessor().viewTeamProblem(
                        ((HiddenValue) ((MutableTreeTableNode) problemPath[
                        problemPath.length - 1]).getValueInColumn(0)).getValue());
            } else //otherwise it is a problem component
            {
                //the component id is in a hidden value in the first column
                MainObjectFactory.getMoveRequestProcessor().viewComponent(
                        ((HiddenValue) ((MutableTreeTableNode) problemPath[
                        problemPath.length - 1]).getValueInColumn(0)).getValue());
            }
        }
    }

    public void processCreateProblem() {
        MainObjectFactory.getMoveRequestProcessor().createTeamProblem();
    }
}
