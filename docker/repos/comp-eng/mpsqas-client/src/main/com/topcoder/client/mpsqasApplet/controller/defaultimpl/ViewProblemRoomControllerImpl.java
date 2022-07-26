package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.controller.ViewProblemRoomController;
import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.view.ViewProblemRoomView;
import com.topcoder.client.mpsqasApplet.controller.component.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.util.IdStructureHandler;
import com.topcoder.client.render.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.language.JavaLanguage;

import java.util.ArrayList;

/**
 * Default implementation of ViewProblemRoomController.
 *
 * @author mitalub
 */
public class ViewProblemRoomControllerImpl
        implements ViewProblemRoomController, ProblemUpdateResponseProcessor,
        ProblemIdStructureResponseProcessor, PaymentResponseProcessor {

    private ViewProblemRoomModel model;
    private ViewProblemRoomView view;

    public void init() {
        model = MainObjectFactory.getViewProblemRoomModel();
        view = MainObjectFactory.getViewProblemRoomView();
    }

    public void placeOnHold() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.NEW_PROBLEM_ID_STRUCTURE);
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PAYMENT);
        view.removeAllComponents();
    }

    public void takeOffHold() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_PROBLEM_ID_STRUCTURE);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PAYMENT);

        Object[] component;
        ArrayList components = new ArrayList();

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

        //create and populate the components
        if (model.isStatementEditable() && !(admin && pending)) {
            component = ComponentObjectFactory.createStatementPanel();
            ((StatementPanelModel) component[1]).setComponentInformation(
                    model.getComponentInformation());
            components.add(component);
        }

        component = ComponentObjectFactory.createStatementPreviewPanel();
        ((StatementPreviewPanelModel) component[1]).setProblem(
                model.getProblemInformation());
        ((StatementPreviewPanelModel) component[1]).setType(
                MessageConstants.PROBLEM_STATEMENT);
        if (model.getComponentInformation().isValid()) {
            String html = "";
            try {
                html = new ProblemRenderer(model.getProblemInformation()).toHTML(
                        JavaLanguage.JAVA_LANGUAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((StatementPreviewPanelModel) component[1]).setPreview(html);
        }
/*
    ((StatementPreviewPanelModel)component[1]).setProblemComponent(
            model.getProblemInformation().getProblemComponents()[0]);
    ((StatementPreviewPanelModel)component[1]).setType(
            MessageConstants.COMPONENT_STATEMENT);
    if(model.getComponentInformation().isValid())
    {
      ((StatementPreviewPanelModel)component[1]).setPreview(
              model.getComponentInformation().toHTML(
                      JavaLanguage.JAVA_LANGUAGE));
    }
*/
        components.add(component);

        if (pastProposal) {
            if (!(admin && pending)) {
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
            }

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

        //if the user is an admin and this is not a new problem, give them
        //the admin panel
        if (admin && model.getProblemInformation().getProblemId() != -1) {
            component = ComponentObjectFactory.createAdminProblemPanel();
            ((AdminProblemPanelModel) component[1]).setContainsStatus(true);
            ((AdminProblemPanelModel) component[1]).setStatus(
                    model.getProblemInformation().getStatus());
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
            ((PaymentPanelModel) component[1]).setWriters(
                    model.getComponentInformation().getWriters());
            
            ((PaymentPanelModel) component[1]).setProblemLevel(
                    model.getProblemInformation().getDivision(),
                    model.getProblemInformation().getDifficulty());
            
            ((PaymentPanelModel) component[1]).setPayments(
                    model.getComponentInformation().getWriterPayments(),
                    model.getComponentInformation().getTesterPayments());
            
            ((PaymentPanelModel) component[1]).setRoundID(
                    model.getComponentInformation().getRoundID());
            ((PaymentPanelModel) component[1]).setRoundName(
                    model.getComponentInformation().getRoundName());
            
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

    public void processSubmit() {
        model.getProblemInformation().setDifficulty(view.getDifficulty());
        model.getProblemInformation().setDivision(view.getDivision());
        model.getProblemInformation().setName(model.getComponentInformation()
                .getClassName());
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Submitting problem...", false);
        MainObjectFactory.getProblemRequestProcessor().submitProblem(
                model.getProblemInformation());
    }

    public void processSaveStatement() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending statement to server for saving...", false);
        MainObjectFactory.getProblemRequestProcessor().saveStatement(
                model.getComponentInformation());
    }

    public void processProblemModified(String modifierName) {
        model.setCanSubmit(false);
        model.notifyWatchers(UpdateTypes.PROBLEM_MODIFIED);
    }

    public void processNewIdStructure(ProblemIdStructure idStructure) {
        IdStructureHandler.handleProblemIdStructure(idStructure,
                model.getProblemInformation());
    }

    public void processPaymentResponse(ArrayList writers, ArrayList testers) {
        model.getComponentInformation().setWriterPayments(writers);
        model.getComponentInformation().setTesterPayments(testers);
        model.notifyWatchers(UpdateTypes.PAYMENTS_CHANGE);
    }
}
