package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.controller.ViewContestRoomController;
import com.topcoder.client.mpsqasApplet.model.ViewContestRoomModel;
import com.topcoder.client.mpsqasApplet.view.ViewContestRoomView;
import com.topcoder.client.mpsqasApplet.controller.component.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.view.component.*;

/**
 * Default implementation of ViewContestRoomController.
 *
 * @author mitalub
 */
public class ViewContestRoomControllerImpl
        implements ViewContestRoomController {

    private final static int GENERAL_CONTEST_INFO_PANEL = 0;
    private final static int ROUND_PROBLEMS_PANEL = 1;

    private ViewContestRoomModel model;
    private ViewContestRoomView view;

    public void init() {
        model = MainObjectFactory.getViewContestRoomModel();
        view = MainObjectFactory.getViewContestRoomView();
    }

    public void placeOnHold() {
        view.removeAllComponents();
    }

    public void takeOffHold() {
        int[] panels = new int[2];
        Object[] componentStuff = new Object[3];

        panels[0] = GENERAL_CONTEST_INFO_PANEL;
        panels[1] = ROUND_PROBLEMS_PANEL;

        for (int i = 0; i < panels.length; i++) {
            switch (panels[i]) {
            case GENERAL_CONTEST_INFO_PANEL:
                componentStuff = ComponentObjectFactory
                        .createGeneralContestInfoPanel();
                ((GeneralContestInfoPanelModel) componentStuff[1])
                        .setContestInformation(model.getContestInformation());
                break;
            case ROUND_PROBLEMS_PANEL:
                componentStuff = ComponentObjectFactory.createRoundProblemsPanel();
                ((RoundProblemsPanelModel) componentStuff[1]).setSingleProblems(
                        model.getContestInformation().getSingleProblems());
                ((RoundProblemsPanelModel) componentStuff[1]).setTeamProblems(
                        model.getContestInformation().getTeamProblems());
                ((RoundProblemsPanelModel) componentStuff[1]).setLongProblems(
                        model.getContestInformation().getLongProblems());
                break;
            default:
                System.out.println("Unrecognized panel: " + i);
            }
            ((ComponentController) componentStuff[0]).setMainController(this);
            model.addWatcher((ComponentController) componentStuff[0]);
            ((ComponentModel) componentStuff[1]).setMainModel(model);
            ((ComponentModel) componentStuff[1]).notifyWatchers();
            view.addComponent((ComponentView) componentStuff[2]);
        }
    }
}
