package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.AdminProblemPanelController;
import com.topcoder.client.mpsqasApplet.view.component.AdminProblemPanelView;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.model.component.AdminProblemPanelModel;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.netCommon.mpsqas.UserInformation;

import java.util.ArrayList;

/**
 * Implementation of admin problem panel controller.
 *
 * @author mitalub
 */
public class AdminProblemPanelControllerImpl extends AdminProblemPanelController {

    private AdminProblemPanelModel model;
    private AdminProblemPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (AdminProblemPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (AdminProblemPanelView) view;
    }

    public void processSubmit() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending information to server for saving...", false);
        ArrayList testerIds = new ArrayList();
        for (int i = 0; i < model.getScheduledTesters().size(); i++) {
            testerIds.add(new Integer(
                    ((UserInformation) model.getScheduledTesters().get(i)).getUserId()));
        }

        if (model.getSolutions() != null && !model.containsStatus()) {
            //component room, save solution and testers
            MainObjectFactory.getProblemRequestProcessor().saveAdminProblemInfo(
                    -1, view.getPrimarySolution(), testerIds);
        } else if (model.getSolutions() == null && model.containsStatus()) {
            //team problem room, save status and testers
            MainObjectFactory.getProblemRequestProcessor().saveAdminProblemInfo(
                    view.getStatus(), -1, testerIds);
        } else if (model.getSolutions() != null && model.containsStatus()) {
            //regular problem room, save all three
            MainObjectFactory.getProblemRequestProcessor().saveAdminProblemInfo(
                    view.getStatus(), view.getPrimarySolution(), testerIds);
        }
    }

    public void processAddTester() {
        int index = view.getSelectedAvailableTesterIndex();
        if (index != -1) {
            Object tester = model.getAvailableTesters().get(index);
            if (!model.getScheduledTesters().contains(tester)) {
                model.getScheduledTesters().add(tester);
                model.notifyWatchers(UpdateTypes.SCHEDULED_TESTERS);
            }
        }
    }

    public void processRemoveTester() {
        int index = view.getSelectedScheduledTesterIndex();
        if (index != -1 && index < model.getScheduledTesters().size()) {
            model.getScheduledTesters().remove(index);
            model.notifyWatchers(UpdateTypes.SCHEDULED_TESTERS);
        }
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
