package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.ViewUserRoomController;
import com.topcoder.client.mpsqasApplet.view.ViewUserRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewUserRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.*;

import java.util.HashMap;

/**
 * Default implementation of View User Room controller.
 *
 * @author mitalub
 */
public class ViewUserRoomControllerImpl
        implements ViewUserRoomController {

    private ViewUserRoomView view;
    private ViewUserRoomModel model;

    public void init() {
        view = MainObjectFactory.getViewUserRoomView();
        model = MainObjectFactory.getViewUserRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processSave() {
        try {
            HashMap payments = new HashMap();
            for (int i = 0; i < model.getUserInformation().getProblems().size(); i++) {
                payments.put(new Integer(((ProblemInformation)
                        model.getUserInformation().getProblems().get(i)).getProblemId()),
                        new Double(view.getPending(i)));
            }
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Saving pending payments...", false);
            MainObjectFactory.getUserRequestProcessor().savePendingPayments(payments);
        } catch (Exception e) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Payment amounts must be decimal values.", true);
        }
    }

    public void processViewProblem() {
        int index = view.getSelectedProblemIndex();
        if (index != -1) {
            ProblemInformation problem = (ProblemInformation)
                    model.getUserInformation().getProblems().get(index);
            if (problem.getProblemTypeID() == ApplicationConstants.SINGLE_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewProblem(
                        problem.getProblemId());
            } else if (problem.getProblemTypeID() == ApplicationConstants.TEAM_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewTeamProblem(
                        problem.getProblemId());
            } else if (problem.getProblemTypeID() == ApplicationConstants.LONG_PROBLEM) {
                MainObjectFactory.getMoveRequestProcessor().viewLongProblem(
                        problem.getProblemId());
            }
        }
    }
}
