package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.controller.ViewComponentRoomController;
import com.topcoder.client.mpsqasApplet.model.ViewComponentRoomModel;
import com.topcoder.client.mpsqasApplet.view.ViewComponentRoomView;
import com.topcoder.client.mpsqasApplet.controller.component.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.util.IdStructureHandler;
import com.topcoder.client.render.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.language.JavaLanguage;

import java.util.ArrayList;

/**
 * Default implementation of ViewComponentRoomController.
 *
 * @author mitalub
 */
public class ViewComponentRoomControllerImpl
        implements ViewComponentRoomController,
        ProblemUpdateResponseProcessor,
        ComponentIdStructureResponseProcessor {

    private ViewComponentRoomModel model;
    private ViewComponentRoomView view;

    public void init() {
        model = MainObjectFactory.getViewComponentRoomModel();
        view = MainObjectFactory.getViewComponentRoomView();
    }

    public void placeOnHold() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.NEW_COMPONENT_ID_STRUCTURE);
        view.removeAllComponents();
    }

    public void takeOffHold() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_COMPONENT_ID_STRUCTURE);

        Object[] component;
        ArrayList components = new ArrayList();

        boolean pastProposal = model.getComponentInformation().getStatus() ==
                StatusConstants.PROPOSAL_APPROVED
                || model.getComponentInformation().getStatus() ==
                StatusConstants.SUBMISSION_PENDING_APPROVAL
                || model.getComponentInformation().getStatus() ==
                StatusConstants.SUBMISSION_REJECTED
                || model.getComponentInformation().getStatus() ==
                StatusConstants.SUBMISSION_APPROVED
                || model.getComponentInformation().getStatus() ==
                StatusConstants.TESTING
                || model.getComponentInformation().getStatus() ==
                StatusConstants.FINAL_TESTING
                || model.getComponentInformation().getStatus() ==
                StatusConstants.READY
                || model.getComponentInformation().getStatus() ==
                StatusConstants.USED;


        //create and populate the components
        if (model.isStatementEditable()) {
            component = ComponentObjectFactory.createStatementPanel();
            ((StatementPanelModel) component[1]).setComponentInformation(
                    model.getComponentInformation());
            components.add(component);
        }

        component = ComponentObjectFactory.createStatementPreviewPanel();
        ((StatementPreviewPanelModel) component[1]).setType(
                MessageConstants.COMPONENT_STATEMENT);
        ((StatementPreviewPanelModel) component[1]).setProblemComponent(
                model.getComponentInformation());
        if (model.getComponentInformation().isValid()) {
            String html = "";
            try {
                html = new ProblemComponentRenderer(model.getComponentInformation()).toHTML(
                        JavaLanguage.JAVA_LANGUAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((StatementPreviewPanelModel) component[1]).setPreview(html);
        }
        components.add(component);

        if (pastProposal) {
            component = ComponentObjectFactory.createTestCasePanel();
            ((TestCasePanelModel) component[1]).setComponentInformation(
                    model.getComponentInformation());
            components.add(component);

            component = ComponentObjectFactory.createSolutionPanel();
            ((SolutionPanelModel) component[1]).setComponentInformation(
                    model.getComponentInformation());
            ((SolutionPanelModel) component[1]).setParamTypes(
                    model.getComponentInformation().getParamTypes());
            ((SolutionPanelModel) component[1]).setClassName(
                    model.getComponentInformation().getClassName());
            ((SolutionPanelModel) component[1]).setSolutionInformation(
                    model.getComponentInformation().getSolution());
            components.add(component);

            component = ComponentObjectFactory.createAllSolutionPanel();
            ((AllSolutionPanelModel) component[1]).setParamTypes(
                    model.getComponentInformation().getParamTypes());
            ((AllSolutionPanelModel) component[1]).setSolutions(
                    model.getComponentInformation().getAllSolutions());
            components.add(component);
        }

        component = ComponentObjectFactory.createCorrespondencePanel();
        ((CorrespondencePanelModel) component[1]).setMessages(
                model.getComponentInformation().getCorrespondence());
        ((CorrespondencePanelModel) component[1]).setReceivers(
                model.getComponentInformation().getCorrespondenceReceivers());
        components.add(component);

        //if the user is and admin and this is not a new component
        //give them the admin panel
        if (MainObjectFactory.getMainAppletModel().isAdmin()
                && model.getComponentInformation().getComponentId() != -1) {
            component = ComponentObjectFactory.createAdminProblemPanel();
            ((AdminProblemPanelModel) component[1]).setContainsStatus(false);
            ((AdminProblemPanelModel) component[1]).setSolutions(
                    model.getComponentInformation().getAllSolutions());
            ((AdminProblemPanelModel) component[1]).setAvailableTesters(
                    model.getComponentInformation().getAvailableTesters());
            ((AdminProblemPanelModel) component[1]).setScheduledTesters(
                    model.getComponentInformation().getScheduledTesters());
            components.add(component);
            
            //give them a payment panel
            component = ComponentObjectFactory.createPaymentPanel();
            ((PaymentPanelModel) component[1]).setTesters(
                    model.getComponentInformation().getScheduledTesters());
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

    public void processSave() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Submitting component...", false);
        MainObjectFactory.getProblemRequestProcessor().saveComponent(
                model.getComponentInformation());
    }

    public void processNewIdStructure(ComponentIdStructure idStructure) {
        IdStructureHandler.handleComponentIdStructure(idStructure,
                model.getComponentInformation());
    }

    public void processProblemModified(String modifierName) {
        model.setCanSubmit(false);
        model.notifyWatchers(UpdateTypes.PROBLEM_MODIFIED);
    }
}
