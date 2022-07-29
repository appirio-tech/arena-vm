/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.client.mpsqasApplet.controller.*;
import com.topcoder.client.mpsqasApplet.model.*;
import com.topcoder.client.mpsqasApplet.view.*;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MainAppletView;
import com.topcoder.client.mpsqasApplet.messaging.LoginResponseProcessor;
import com.topcoder.client.mpsqasApplet.messaging.MoveResponseProcessor;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.shared.problem.ProblemComponent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of the Main Applet controller.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #processAcceptedLogin(boolean, boolean, boolean, LookupValues)}
 * to support {@link com.topcoder.netCommon.mpsqas.LookupValues} argument and save it to
 * {@link com.topcoder.client.mpsqasApplet.object.MainObjectFactory}.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class mutates not thread-safe model, thus it's not thread-safe.
 * </p>
 *
 * @author mitalub, TCSASSEMBLER
 * @version 1.1
 */
public class MainAppletControllerImpl implements MainAppletController,
        LoginResponseProcessor,
        MoveResponseProcessor {

    private MainAppletModel model;
    private MainAppletView view;

    //Controller methods-----------------------------------------------

    /**
     * Initializes things, stores the model and view and registers this with
     * with the response handler.
     */
    public void init() {
        model = MainObjectFactory.getMainAppletModel();
        view = MainObjectFactory.getMainAppletView();

        processWindowLocationChange();

        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.LOGIN);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.MOVE);
    }

    /** Does nothing, this controller must always be running. */
    public void placeOnHold() {
    }

    /** Does nothing, this controller must always be running. */
    public void takeOffHold() {
    }

    //MainAppletController methods---------------------------------------

    /**
     * Calls the appropriate request processor to make a relative move request.
     * In some certain cases, this method contains hacks to do relative
     * moves without talking to the server (going back and forth between
     * component rooms and view team problem rooms)
     */
    public void doRelativeMove(int whereTo) {
        //otherwise, talk to the server about it
        MainObjectFactory.getMoveRequestProcessor().moveRelative(whereTo);
    }

    /**
     * Jump to a problem matching the String
     */
    public void jump(String pattern){
        MainObjectFactory.getMoveRequestProcessor().jump(pattern);
    }

    /**
     * Toggles the location of the status window.
     */
    public void reverseStatusWindow() {
        model.setIsStatusPoppedUp(!model.isStatusPoppedUp());
        model.notifyWatchers();
    }

    /**
     * Stores new window bounds & dimensions in the model.
     */
    public void processWindowLocationChange() {
        //make sure they aren't null because the view might call this before
        //things are propertly stored.
        if (view != null && model != null) {
            model.setWinSize(view.getWinSize());
            model.setBounds(view.getBounds());
        }
    }

    /**
     * Hides the window.
     */
    public void hide() {
        if (model.getCurrentRoomController() != null) {
            model.getCurrentRoomController().placeOnHold();
        }
        view.close();
    }

    /**
     * Closes the applet.
     */
    public void close() {
        MainObjectFactory.getMainApplet().close();
    }

    /**
     * Update the currently displayed room.
     *
     * @param roomModel The new room's model.
     * @param roomView The new room's view.
     * @param roomController The new room's controller.
     * @param full True if this is a full room and should get a menu,
     *             toolbar, and status window.
     */
    private synchronized void swapToRoom(Model roomModel,
            View roomView, Controller roomController, boolean full) {
        roomModel.notifyWatchers();

        if (model.getCurrentRoomController() != null) {
            model.getCurrentRoomController().placeOnHold();
        }

        roomController.takeOffHold();

        model.setHasExtras(full);
        model.setCurrentRoomModel(roomModel);
        model.setCurrentRoomController(roomController);
        model.setCurrentRoomView(roomView);
        model.notifyWatchers();
    }

    //LoginResponseProcessor methods-----------------------------------

    /** Does nothing, this controller does not care. */
    public void processRefusedLogin(String reason) {
    }

    /**
     * <p>
     * Processes accepted login event.
     * </p>
     *
     * <p>
     * It changes administrator status if necessary and stores writer / tester status.
     * Also it saves the lookup values provided from backend.
     * </p>
     * @param isAdmin Flag indicating if logged in user is administrator.
     * @param isWriter Flag indicating if logged in user is writer.
     * @param isTester Flag indicating if logged in user is tester.
     * @param lookupValues Lookup values.
     */
    public void processAcceptedLogin(boolean isAdmin, boolean isWriter,
            boolean isTester, LookupValues lookupValues) {
        MainObjectFactory.setLookupValues(lookupValues);
        MainObjectFactory.getIMoveRequestProcessor().loadMovingRoom();
        model.setIsAdmin(isAdmin);
        model.setIsWriter(isWriter);
        model.setIsTester(isTester);
        model.notifyWatchers();
    }

    //MoveResponseProcessor methods-----------------------------------

    /**
     * Gets components and populates for the Application Room.
     */
    public void loadApplicationRoom(int applicationType) {
        ApplicationRoomModel model =
                MainObjectFactory.getApplicationRoomModel();
        ApplicationRoomView view =
                MainObjectFactory.getApplicationRoomView();
        ApplicationRoomController controller =
                MainObjectFactory.getApplicationRoomController();
        model.setType(applicationType);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for a Foyer Room.
     */
    public void loadFoyerRoom(ArrayList problems) {
        FoyerRoomModel model =
                MainObjectFactory.getFoyerRoomModel();
        FoyerRoomView view =
                MainObjectFactory.getFoyerRoomView();
        FoyerRoomController controller =
                MainObjectFactory.getFoyerRoomController();
        model.setProblemList(problems);
        model.setIsFullRoom(problems != null);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for a Main Application Room.
     */
    public void loadMainApplicationRoom(ArrayList applications) {
        MainApplicationRoomModel model =
                MainObjectFactory.getMainApplicationRoomModel();
        MainApplicationRoomView view =
                MainObjectFactory.getMainApplicationRoomView();
        MainApplicationRoomController controller =
                MainObjectFactory.getMainApplicationRoomController();
        model.setApplications(applications);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for a Main Problem Room.
     */
    public void loadMainProblemRoom(HashMap problems) {
        MainProblemRoomModel model =
                MainObjectFactory.getMainProblemRoomModel();
        MainProblemRoomView view =
                MainObjectFactory.getMainProblemRoomView();
        MainProblemRoomController controller =
                MainObjectFactory.getMainProblemRoomController();
        model.setProblems(problems);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the Main User Room.
     */
    public void loadMainUserRoom(ArrayList users) {
        MainUserRoomModel model =
                MainObjectFactory.getMainUserRoomModel();
        MainUserRoomView view =
                MainObjectFactory.getMainUserRoomView();
        MainUserRoomController controller =
                MainObjectFactory.getMainUserRoomController();
        model.setUsers(users);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the Main Contest Room.
     */
    public void loadMainContestRoom(ArrayList contests) {
        MainContestRoomModel model =
                MainObjectFactory.getMainContestRoomModel();
        MainContestRoomView view =
                MainObjectFactory.getMainContestRoomView();
        MainContestRoomController controller =
                MainObjectFactory.getMainContestRoomController();
        model.setContests(contests);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the View Application Room.
     */
    public void loadViewApplicationRoom(ApplicationInformation application) {
        ViewApplicationRoomModel model =
                MainObjectFactory.getViewApplicationRoomModel();
        ViewApplicationRoomView view =
                MainObjectFactory.getViewApplicationRoomView();
        ViewApplicationRoomController controller =
                MainObjectFactory.getViewApplicationRoomController();
        model.setApplicationInformation(application);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the View Contest Room.
     */
    public void loadViewContestRoom(ContestInformation contest) {
        ViewContestRoomModel model =
                MainObjectFactory.getViewContestRoomModel();
        ViewContestRoomView view =
                MainObjectFactory.getViewContestRoomView();
        ViewContestRoomController controller =
                MainObjectFactory.getViewContestRoomController();
        model.setContestInformation(contest);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the View Problem Room.
     */
    public void loadViewProblemRoom(ProblemInformation problem,
            boolean statementEditable) {
        ViewProblemRoomModel model =
                MainObjectFactory.getViewProblemRoomModel();
        ViewProblemRoomView view =
                MainObjectFactory.getViewProblemRoomView();
        ViewProblemRoomController controller =
                MainObjectFactory.getViewProblemRoomController();
        model.setProblemInformation(problem);
        model.setCanSubmit(true);
        model.setIsStatementEditable(statementEditable);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the View User Room.
     */
    public void loadViewUserRoom(UserInformation user) {
        ViewUserRoomModel model =
                MainObjectFactory.getViewUserRoomModel();
        ViewUserRoomView view =
                MainObjectFactory.getViewUserRoomView();
        ViewUserRoomController controller =
                MainObjectFactory.getViewUserRoomController();
        model.setUserInformation(user);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the Moving Room.
     */
    public void loadMovingRoom() {
        MovingRoomModel model =
                MainObjectFactory.getMovingRoomModel();
        MovingRoomView view =
                MainObjectFactory.getMovingRoomView();
        MovingRoomController controller =
                MainObjectFactory.getMovingRoomController();
        swapToRoom(model, view, controller, false);
    }

    /**
     * Gets components and populates the model for the Login Room.
     */
    public void loadLoginRoom() {
        LoginRoomModel model =
                MainObjectFactory.getLoginRoomModel();
        LoginRoomView view =
                MainObjectFactory.getLoginRoomView();
        LoginRoomController controller =
                MainObjectFactory.getLoginRoomController();
        swapToRoom(model, view, controller, false);
    }

    public void loadViewTeamProblemRoom(ProblemInformation problemInfo,
            boolean isStatementEditable) {
        ViewTeamProblemRoomModel model =
                MainObjectFactory.getViewTeamProblemRoomModel();
        ViewTeamProblemRoomView view =
                MainObjectFactory.getViewTeamProblemRoomView();
        ViewTeamProblemRoomController controller =
                MainObjectFactory.getViewTeamProblemRoomController();
        model.setCanSubmit(true);
        model.setProblemInformation(problemInfo);
        model.setIsStatementEditable(isStatementEditable);
        swapToRoom(model, view, controller, true);
    }
    
    public void loadViewLongProblemRoom(ProblemInformation problemInfo,
            boolean isStatementEditable) {
        ViewLongProblemRoomModel model =
                MainObjectFactory.getViewLongProblemRoomModel();
        ViewLongProblemRoomView view =
                MainObjectFactory.getViewLongProblemRoomView();
        ViewLongProblemRoomController controller =
                MainObjectFactory.getViewLongProblemRoomController();
        model.setCanSubmit(true);
        model.setProblemInformation(problemInfo);
        model.setIsStatementEditable(isStatementEditable);
        swapToRoom(model, view, controller, true);
    }

    /**
     * Gets components and populates the model for the Main Team Problem Room.
     */
    public void loadMainTeamProblemRoom(HashMap problems) {
        MainTeamProblemRoomModel model =
                MainObjectFactory.getMainTeamProblemRoomModel();
        MainTeamProblemRoomView view =
                MainObjectFactory.getMainTeamProblemRoomView();
        MainTeamProblemRoomController controller =
                MainObjectFactory.getMainTeamProblemRoomController();
        model.setProblems(problems);
        swapToRoom(model, view, controller, true);
    }
    
    /**
     * Gets components and populates the model for the Main Long Problem Room.
     */
    public void loadMainLongProblemRoom(HashMap problems) {
        MainLongProblemRoomModel model =
                MainObjectFactory.getMainLongProblemRoomModel();
        MainLongProblemRoomView view =
                MainObjectFactory.getMainLongProblemRoomView();
        MainLongProblemRoomController controller =
                MainObjectFactory.getMainLongProblemRoomController();
        model.setProblems(problems);
        swapToRoom(model, view, controller, true);
    }

    public void loadEmptyViewProblemRoom() {
        ViewProblemRoomModel model =
                MainObjectFactory.getViewProblemRoomModel();
        ViewProblemRoomView view =
                MainObjectFactory.getViewProblemRoomView();
        ViewProblemRoomController controller =
                MainObjectFactory.getViewProblemRoomController();
        model.setProblemInformation(new ProblemInformation());
        model.getProblemInformation().setProblemTypeID(
                ApplicationConstants.SINGLE_PROBLEM);
        model.getProblemInformation().setProblemComponents(
                new ProblemComponent[]{new ComponentInformation()});
        model.setIsStatementEditable(true);
        model.setCanSubmit(true);
        swapToRoom(model, view, controller, true);
    }

    public void loadEmptyViewTeamProblemRoom() {
        ViewTeamProblemRoomModel model =
                MainObjectFactory.getViewTeamProblemRoomModel();
        ViewTeamProblemRoomView view =
                MainObjectFactory.getViewTeamProblemRoomView();
        ViewTeamProblemRoomController controller =
                MainObjectFactory.getViewTeamProblemRoomController();
        model.setProblemInformation(new ProblemInformation());
        model.getProblemInformation().setProblemTypeID(
                ApplicationConstants.TEAM_PROBLEM);
        model.setIsStatementEditable(true);
        model.setCanSubmit(true);
        swapToRoom(model, view, controller, true);
    }
    
    public void loadEmptyViewLongProblemRoom() {
        ViewLongProblemRoomModel model =
                MainObjectFactory.getViewLongProblemRoomModel();
        ViewLongProblemRoomView view =
                MainObjectFactory.getViewLongProblemRoomView();
        ViewLongProblemRoomController controller =
                MainObjectFactory.getViewLongProblemRoomController();
        model.setProblemInformation(new ProblemInformation());
        model.getProblemInformation().setProblemTypeID(
                ApplicationConstants.LONG_PROBLEM);
        model.getProblemInformation().setProblemComponents(
                new ProblemComponent[]{new ComponentInformation()});
        model.setIsStatementEditable(true);
        model.setCanSubmit(true);
        swapToRoom(model, view, controller, true);
    }

    public void loadWebServiceRoom(WebServiceInformation webServiceInformation,
            boolean editable) {
        WebServiceRoomModel model =
                MainObjectFactory.getWebServiceRoomModel();
        WebServiceRoomView view =
                MainObjectFactory.getWebServiceRoomView();
        WebServiceRoomController controller =
                MainObjectFactory.getWebServiceRoomController();
        model.setWebServiceInformation(webServiceInformation);
        model.setClassViews(new HashMap());
        model.setIsEditable(editable);
        swapToRoom(model, view, controller, true);
    }

    public void loadViewComponentRoom(ComponentInformation componentInformation,
            boolean isStatementEditable) {
        ViewComponentRoomModel model =
                MainObjectFactory.getViewComponentRoomModel();
        ViewComponentRoomView view =
                MainObjectFactory.getViewComponentRoomView();
        ViewComponentRoomController controller =
                MainObjectFactory.getViewComponentRoomController();
        model.setComponentInformation(componentInformation);
        model.setIsStatementEditable(isStatementEditable);
        model.setCanSubmit(true);
        swapToRoom(model, view, controller, true);
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.controller.MainAppletController#clearStatus()
     */
    public void clearStatus() {
        StatusModel statusModel = MainObjectFactory.getStatusModel();
        statusModel.clearStatusMessages();
        statusModel.notifyWatchers();
    }
}
