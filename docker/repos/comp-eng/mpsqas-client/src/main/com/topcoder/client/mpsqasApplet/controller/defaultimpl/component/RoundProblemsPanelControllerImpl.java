package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.client.mpsqasApplet.controller.component.RoundProblemsPanelController;
import com.topcoder.client.mpsqasApplet.model.component.RoundProblemsPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.RoundProblemsPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.netCommon.mpsqas.*;

/**
 * Default implementation of RoundProblemsPanelController.
 *
 * @author mitalub
 */
public class RoundProblemsPanelControllerImpl
        extends RoundProblemsPanelController {

    RoundProblemsPanelModel model;
    RoundProblemsPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (RoundProblemsPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (RoundProblemsPanelView) view;
    }

    public void processViewSingleProblem() {
        int index = view.getSelectedSingleProblemIndex();
        if (index > -1) {
            int problemId = ((ProblemInformation) model.getSingleProblems().get(index))
                    .getProblemId();
            MainObjectFactory.getMoveRequestProcessor().viewProblem(problemId);
        }
    }

    public void processViewTeamProblem() {
        Object[] problemPath = view.getSelectedTeamProblemPath();

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

    public void processViewLongProblem() {
        int index = view.getSelectedLongProblemIndex();
        if (index > -1) {
            int problemId = ((ProblemInformation) model.getLongProblems().get(index))
                    .getProblemId();
            MainObjectFactory.getMoveRequestProcessor().viewLongProblem(problemId);
        }
    }
    
    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
