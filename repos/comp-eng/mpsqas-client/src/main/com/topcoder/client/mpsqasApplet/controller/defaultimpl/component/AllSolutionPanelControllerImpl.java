package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.AllSolutionPanelController;
import com.topcoder.client.mpsqasApplet.model.component.AllSolutionPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.AllSolutionPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.client.mpsqasApplet.model.ViewComponentRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.problem.DataType;

import java.util.ArrayList;

/**
 * Default implementation of AllSolutionPanelController .
 *
 * @author mitalub
 */
public class AllSolutionPanelControllerImpl
        extends AllSolutionPanelController {

    AllSolutionPanelModel model;
    AllSolutionPanelView view;

    public void init() {
    }

    public void setModel(ComponentModel model) {
        this.model = (AllSolutionPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (AllSolutionPanelView) view;
    }

    public void processSystemTestAll() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "System testing all...", false);
        MainObjectFactory.getSolutionRequestProcessor().systemTestAll();
    }

    public void processTestAll() {
        MainObjectFactory.getIArgEntryRequestProcessor().getArgs(
                model.getParamTypes(), MessageConstants.TEST_ALL);
    }

    public void processSolutionSelected() {
        int index = view.getSelectedSolutionIndex();
        if (index == -1) {
            view.setPreviewText("");
        } else {
            view.setPreviewText(((SolutionInformation) (model.getSolutions()
                    .get(index))).getText());
        }
    }

    public void close() {
    }

    /**
     * Updates the param types in the model and passes the notification to
     * the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        if (arg != null && arg.equals(UpdateTypes.PARAM_TYPES)) {
            if (model.getMainModel() instanceof ViewProblemRoomModel) {
                model.setParamTypes(((ViewProblemRoomModel) model.getMainModel())
                        .getComponentInformation().getParamTypes());
            } else if (model.getMainModel() instanceof ViewComponentRoomModel) {
                model.setParamTypes(((ViewComponentRoomModel) model.getMainModel())
                        .getComponentInformation().getParamTypes());
            }
        }

        model.notifyWatchers(arg);
    }
}
