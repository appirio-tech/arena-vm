package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import java.util.HashMap;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.SolutionPanelController;
import com.topcoder.client.mpsqasApplet.model.ViewComponentRoomModel;
import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.SolutionPanelModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.view.component.SolutionPanelView;
import com.topcoder.netCommon.mpsqas.MessageConstants;

/**
 * Default implementation of SolutionPanelController .
 *
 * @author mitalub
 */
public class SolutionPanelControllerImpl extends SolutionPanelController {

    SolutionPanelModel model;
    SolutionPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (SolutionPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (SolutionPanelView) view;
    }

    /**
     * Calls the compile request processor to send a compile request to the
     * server.
     */
    public void processCompile() {
        if (model.getComponentInformation().getComponentId() == -1) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "This component does not yet exist on the server, please submit "
                    + "it first.", true);
        } else {
            //fileName = className + .java"
            String fileName = model.getClassName() + "." + view.getLanguage().getDefaultExtension();
            String text = view.getSolutionText();

            //codeFiles HashMap contains just one file
            HashMap codeFiles = new HashMap();
            codeFiles.put(fileName, text);

            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Compiling...", false);
            MainObjectFactory.getSolutionRequestProcessor().compile(codeFiles,
                    view.getLanguage());
        }
    }

    /**
     * Calls the internal arg entry request processor to get the args from
     * the user to test the solution.
     */
    public void processTest() {
        if (model.getComponentInformation().getComponentId() == -1) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "This component does not yet exist on the server, please submit "
                    + "it first.", true);
        } else {
            MainObjectFactory.getIArgEntryRequestProcessor().getArgs(
                    model.getParamTypes(), MessageConstants.TEST_ONE);
        }
    }

    /**
     * Called when the solution text may have changed to keep view in
     * sync with model.
     */
    public void processSolutionChange() {
        model.getSolutionInformation().setText(view.getSolutionText());
    }

    /**
     * Called when the language selection may have changed to keep view in
     * sync with model.
     */
    public void processLanguageChange() {
        model.getSolutionInformation().setLanguage(view.getLanguage());
        view.update(UpdateTypes.LANGUAGE_CHANGE);
    }
    
    /**
     * Sends a system test request to the server
     * for the current solution.
     */
    public void processSystemTest() {
        if (model.getComponentInformation().getComponentId() == -1) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "This component does not yet exist on the server, please submit "
                    + "it first.", true);
        } else {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Sending system test request ...", false);
            MainObjectFactory.getSolutionRequestProcessor().systemTest(MessageConstants.TEST_ONE);
        }
    }
    /**
     * Updates the param types in the model and passes the notification to the
     * component model's watchers.
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
