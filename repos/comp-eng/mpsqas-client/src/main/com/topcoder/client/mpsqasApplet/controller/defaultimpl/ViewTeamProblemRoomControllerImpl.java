package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ViewTeamProblemRoomModel;
import com.topcoder.client.mpsqasApplet.view.ViewTeamProblemRoomView;
import com.topcoder.client.mpsqasApplet.controller.ViewTeamProblemRoomController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.common.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.util.IdStructureHandler;
import com.topcoder.client.render.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.language.JavaLanguage;

import java.util.*;

/**
 * Default implementation of ViewTeamProblemRoomController.
 *
 * @author mitalub
 */
public class ViewTeamProblemRoomControllerImpl
        implements ViewTeamProblemRoomController,
        ProblemUpdateResponseProcessor,
        ProblemIdStructureResponseProcessor {

    private ViewTeamProblemRoomModel model;
    private ViewTeamProblemRoomView view;

    public void init() {
        model = MainObjectFactory.getViewTeamProblemRoomModel();
        view = MainObjectFactory.getViewTeamProblemRoomView();
    }

    public void placeOnHold() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.NEW_PROBLEM_ID_STRUCTURE);
        view.removeAllComponents();
    }

    public void takeOffHold() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_PROBLEM_ID_STRUCTURE);

        ArrayList components = new ArrayList();
        Object[] component;

        boolean pastProposal = model.getProblemInformation().getStatus() ==
                StatusConstants.PROPOSAL_APPROVED
                || model.getProblemInformation().getStatus() ==
                StatusConstants.SUBMISSION_PENDING_APPROVAL
                || model.getProblemInformation().getStatus() ==
                StatusConstants.SUBMISSION_REJECTED
                || model.getProblemInformation().getStatus() ==
                StatusConstants.SUBMISSION_APPROVED
                || model.getProblemInformation().getStatus() ==
                StatusConstants.TESTING
                || model.getProblemInformation().getStatus() ==
                StatusConstants.FINAL_TESTING
                || model.getProblemInformation().getStatus() ==
                StatusConstants.READY
                || model.getProblemInformation().getStatus() ==
                StatusConstants.USED;
        boolean pending = model.getProblemInformation().getStatus() ==
                StatusConstants.PROPOSAL_PENDING_APPROVAL
                || model.getProblemInformation().getStatus() ==
                StatusConstants.SUBMISSION_PENDING_APPROVAL;
        boolean admin = MainObjectFactory.getMainAppletModel().isAdmin();

        if (model.isStatementEditable() && !(admin && pending)) {
            component = ComponentObjectFactory.createTeamStatementPanel();
            ((TeamStatementPanelModel) component[1]).setProblemInformation(
                    model.getProblemInformation());
            components.add(component);
        }

        component = ComponentObjectFactory.createStatementPreviewPanel();
        ((StatementPreviewPanelModel) component[1]).setProblem(
                model.getProblemInformation());
        ((StatementPreviewPanelModel) component[1]).setType(
                MessageConstants.PROBLEM_STATEMENT);
        if (model.getProblemInformation().isValid()) {
            String html = "";
            try {
                html = new ProblemRenderer(model.getProblemInformation()).toHTML(
                        JavaLanguage.JAVA_LANGUAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((StatementPreviewPanelModel) component[1]).setPreview(html);
        }
        components.add(component);

        if (!(admin && pending)) {
            component = ComponentObjectFactory.createComponentsPanel();
            ((ComponentsPanelModel) component[1]).setComponents(
                    new ArrayList(Arrays.asList(model.getProblemInformation()
                    .getProblemComponents())));
            ((ComponentsPanelModel) component[1]).setProblemInformation(
                    model.getProblemInformation());
            ((ComponentsPanelModel) component[1]).setIsEditable(
                    model.isStatementEditable());
            ((ComponentsPanelModel) component[1]).setCanMove(true);
            components.add(component);
        }

        component = ComponentObjectFactory.createCorrespondencePanel();
        ((CorrespondencePanelModel) component[1]).setMessages(
                model.getProblemInformation().getCorrespondence());
        ((CorrespondencePanelModel) component[1]).setReceivers(
                model.getProblemInformation().getCorrespondenceReceivers());
        components.add(component);

        //If the user is an admin and this isn't a new problem, give them
        //the admin panel
        if (admin && model.getProblemInformation().getProblemId() != -1) {
            component = ComponentObjectFactory.createAdminProblemPanel();
            ((AdminProblemPanelModel) component[1]).setSolutions(null);
            ((AdminProblemPanelModel) component[1]).setContainsStatus(true);
            ((AdminProblemPanelModel) component[1]).setStatus(
                    model.getProblemInformation().getStatus());
            ((AdminProblemPanelModel) component[1]).setAvailableTesters(
                    model.getProblemInformation().getAvailableTesters());
            ((AdminProblemPanelModel) component[1]).setScheduledTesters(
                    model.getProblemInformation().getScheduledTesters());

            components.add(component);
        }

        if (admin && pending) {
            component = ComponentObjectFactory.createApprovalPanel();
            components.add(component);
        }

        for (int i = 0; i < components.size(); i++) {
            ((ComponentController) ((Object[]) components.get(i))[0]).setMainController(
                    this);
            model.addWatcher((ComponentController) ((Object[]) components.get(i))[0]);
            ((ComponentModel) ((Object[]) components.get(i))[1]).setMainModel(model);
            ((ComponentModel) ((Object[]) components.get(i))[1]).notifyWatchers();
            view.addComponent((ComponentView) ((Object[]) components.get(i))[2]);
        }
    }

    /**
     * Submits the changes to the server by calling the request processor
     * method.
     */
    public void processSubmit() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Submitting Team Problem...", false);
        MainObjectFactory.getProblemRequestProcessor().submitProblem(
                model.getProblemInformation());
    }

    public void processSaveStatement() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending statement to server for saving...", false);
        MainObjectFactory.getProblemRequestProcessor().saveStatement(
                model.getProblemInformation());
    }

    public void processNewIdStructure(ProblemIdStructure idStructure) {
        IdStructureHandler.handleProblemIdStructure(idStructure,
                model.getProblemInformation());
    }

    public void processProblemModified(String modifierName) {
        model.setCanSubmit(false);
        model.notifyWatchers(UpdateTypes.PROBLEM_MODIFIED);
    }
}
