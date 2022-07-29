/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.object;

import com.topcoder.client.mpsqasApplet.*;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.controller.*;
import com.topcoder.client.mpsqasApplet.model.*;
import com.topcoder.client.mpsqasApplet.view.*;
import com.topcoder.client.mpsqasApplet.server.*;
import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.object.ObjectFactory;

/**
 * <p>
 * Manages the major Objects in the applet with static methods.
 * </p>
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Added lookup values related items:
 * {@link #LOOKUP_VALUES}, {@link #getLookupValues()} and {@link #setLookupValues(LookupValues)}.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is expected to be used as static class only.
 * It's statically mutable, but still thread-safe (due to synchronization).
 * </p>
 *
 * @author mitalub, TCSASSEMBLER
 * @version 1.1
 */
public class MainObjectFactory extends ObjectFactory {

    /**
     * <p>
     * Lookup values object.
     * </p>
     *
     * <p>
     * Null initially. Must be set via {@link #setLookupValues(LookupValues)}
     * before obtaining via {@link #getLookupValues()}).
     * Fully mutable (though expected to be set only once).
     * Technically can be any value, but expected to be set to non-null via setter.
     * </p>
     */
    private static LookupValues LOOKUP_VALUES;

    /** Creates if necessary and returns an instance of the MainApplet */
    public static MainApplet getMainApplet() {
        return (MainApplet) getSingletonInstance("MainAppletClass");
    }

    /** Creates if necessary and returns an instance of a PortHandler */
    public static PortHandler getPortHandler() {
        return (PortHandler) getSingletonInstance("PortHandlerClass");
    }

    /** Creates if necessary and returns an instance of a PingResponseProcessor
     * interface. */
    public static PingResponseProcessor getPingResponseProcessor() {
        return (PingResponseProcessor) getSingletonInstance(
                "PingResponseProcessorClass");
    }

    /** Creates if necessary and returns an instance of a ExchangeKeyResponseProcessor
     * interface. */
    public static ExchangeKeyResponseProcessor getExchangeKeyResponseProcessor() {
        return (ExchangeKeyResponseProcessor) getSingletonInstance(
                "ExchangeKeyResponseProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     * IArgEntryRequestProcessor interface.
     */
    public static IArgEntryRequestProcessor getIArgEntryRequestProcessor() {
        return (IArgEntryRequestProcessor) getSingletonInstance(
                "IArgEntryRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     * IPopupRequestProcessor interface.
     */
    public static IPopupRequestProcessor getIPopupRequestProcessor() {
        return (IPopupRequestProcessor) getSingletonInstance(
                "IPopupRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a IMoveRequestProcessor
     * interface. */
    public static IMoveRequestProcessor getIMoveRequestProcessor() {
        return (IMoveRequestProcessor) getSingletonInstance(
                "IMoveRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     *  IStatusMessageRequestProcessor interface. */
    public static IStatusMessageRequestProcessor
            getIStatusMessageRequestProcessor() {
        return (IStatusMessageRequestProcessor) getSingletonInstance(
                "IStatusMessageRequestProcessorClass");
    }

    public static WebServiceRequestProcessor
            getWebServiceRequestProcessor() {
        return (WebServiceRequestProcessor) getSingletonInstance(
                "WebServiceRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     * CorrespondenceRequestProcessor interface. */
    public static CorrespondenceRequestProcessor
            getCorrespondenceRequestProcessor() {
        return (CorrespondenceRequestProcessor) getSingletonInstance(
                "CorrespondenceRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a SolutionRequestProcessor
     * interface. */
    public static SolutionRequestProcessor getSolutionRequestProcessor() {
        return (SolutionRequestProcessor) getSingletonInstance(
                "SolutionRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a LoginRequestProcessor
     * interface. */
    public static LoginRequestProcessor getLoginRequestProcessor() {
        return (LoginRequestProcessor) getSingletonInstance(
                "LoginRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a PingRequestProcessor
     * interface. */
    public static PingRequestProcessor getPingRequestProcessor() {
        return (PingRequestProcessor) getSingletonInstance(
                "PingRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     *  ApplicationRequestProcessor interface. */
    public static ApplicationRequestProcessor getApplicationRequestProcessor() {
        return (ApplicationRequestProcessor) getSingletonInstance(
                "ApplicationRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     *  ContestRequestProcessor interface. */
    public static ContestRequestProcessor getContestRequestProcessor() {
        return (ContestRequestProcessor) getSingletonInstance(
                "ContestRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a
     *  ProblemRequestProcessor interface. */
    public static ProblemRequestProcessor getProblemRequestProcessor() {
        return (ProblemRequestProcessor) getSingletonInstance(
                "ProblemRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a MoveRequestProcessor
     * interface. */
    public static MoveRequestProcessor getMoveRequestProcessor() {
        return (MoveRequestProcessor) getSingletonInstance(
                "MoveRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a UserRequestProcessor
     * interface. */
    public static UserRequestProcessor getUserRequestProcessor() {
        return (UserRequestProcessor) getSingletonInstance(
                "UserRequestProcessorClass");
    }

    /** Creates if necessary and returns an instance of a ResponseHandler */
    public static ResponseHandler getResponseHandler() {
        return (ResponseHandler) getSingletonInstance("ResponseHandlerClass");
    }

    public static EncryptionHandler getEncryptionHandler() {
        return (EncryptionHandler) getSingletonInstance("EncryptionHandlerClass");
    }

    /** Creates if necessary and returns an instance of the MainAppletController.
     */
    public static MainAppletController getMainAppletController() {
        return (MainAppletController) getSingletonInstance(
                "MainAppletControllerClass");
    }

    /** Creates if necessary and returns an instance of the MainAppletModel. */
    public static MainAppletModel getMainAppletModel() {
        return (MainAppletModel) getSingletonInstance("MainAppletModelClass");
    }

    /** Creates if necessary and returns an instance of the MainAppletView. */
    public static MainAppletView getMainAppletView() {
        return (MainAppletView) getSingletonInstance("MainAppletViewClass");
    }

    /** Creates if necessary and returns an instance of the LoginRoomController.
     */
    public static LoginRoomController getLoginRoomController() {
        return (LoginRoomController) getSingletonInstance("LoginRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the LoginRoomModel. */
    public static LoginRoomModel getLoginRoomModel() {
        return (LoginRoomModel) getSingletonInstance("LoginRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the LoginRoomView. */
    public static LoginRoomView getLoginRoomView() {
        return (LoginRoomView) getSingletonInstance("LoginRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the FoyerRoomController.
     */
    public static FoyerRoomController getFoyerRoomController() {
        return (FoyerRoomController) getSingletonInstance("FoyerRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the FoyerRoomModel. */
    public static FoyerRoomModel getFoyerRoomModel() {
        return (FoyerRoomModel) getSingletonInstance("FoyerRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the FoyerRoomView. */
    public static FoyerRoomView getFoyerRoomView() {
        return (FoyerRoomView) getSingletonInstance("FoyerRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the MovingRoomController.
     */
    public static MovingRoomController getMovingRoomController() {
        return (MovingRoomController) getSingletonInstance(
                "MovingRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the MovingRoomModel. */
    public static MovingRoomModel getMovingRoomModel() {
        return (MovingRoomModel) getSingletonInstance("MovingRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the MovingRoomView. */
    public static MovingRoomView getMovingRoomView() {
        return (MovingRoomView) getSingletonInstance("MovingRoomViewClass");
    }


    /** Creates if necessary and returns an instance of the
     MainProblemRoomController. */
    public static MainProblemRoomController getMainProblemRoomController() {
        return (MainProblemRoomController)
                getSingletonInstance("MainProblemRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the MainProblemRoomModel.
     */
    public static MainProblemRoomModel getMainProblemRoomModel() {
        return (MainProblemRoomModel) getSingletonInstance(
                "MainProblemRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the MainProblemRoomView.
     */
    public static MainProblemRoomView getMainProblemRoomView() {
        return (MainProblemRoomView) getSingletonInstance("MainProblemRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the
     MainTeamProblemRoomController. */
    public static MainTeamProblemRoomController getMainTeamProblemRoomController() {
        return (MainTeamProblemRoomController)
                getSingletonInstance("MainTeamProblemRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the MainTeamProblemRoomModel.
     */
    public static MainTeamProblemRoomModel getMainTeamProblemRoomModel() {
        return (MainTeamProblemRoomModel) getSingletonInstance(
                "MainTeamProblemRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the MainTeamProblemRoomView.
     */
    public static MainTeamProblemRoomView getMainTeamProblemRoomView() {
        return (MainTeamProblemRoomView) getSingletonInstance("MainTeamProblemRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the MainLongProblemRoomController. */
   public static MainLongProblemRoomController getMainLongProblemRoomController() {
       return (MainLongProblemRoomController)
               getSingletonInstance("MainLongProblemRoomControllerClass");
   }

   /** Creates if necessary and returns an instance of the MainLongProblemRoomModel. */
   public static MainLongProblemRoomModel getMainLongProblemRoomModel() {
       return (MainLongProblemRoomModel) getSingletonInstance(
               "MainLongProblemRoomModelClass");
   }

   /** Creates if necessary and returns an instance of the MainLongProblemRoomView. */
   public static MainLongProblemRoomView getMainLongProblemRoomView() {
       return (MainLongProblemRoomView) getSingletonInstance("MainLongProblemRoomViewClass");
   }

    /** Creates if necessary and returns an instance of the
     MainContestRoomController. */
    public static MainContestRoomController getMainContestRoomController() {
        return (MainContestRoomController)
                getSingletonInstance("MainContestRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the MainContestRoomModel.
     */
    public static MainContestRoomModel getMainContestRoomModel() {
        return (MainContestRoomModel) getSingletonInstance("MainContestRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the MainContestRoomView.
     */
    public static MainContestRoomView getMainContestRoomView() {
        return (MainContestRoomView) getSingletonInstance("MainContestRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the
     ViewApplicationRoomController. */
    public static ViewApplicationRoomController getViewApplicationRoomController() {
        return (ViewApplicationRoomController)
                getSingletonInstance("ViewApplicationRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     * ViewApplicationRoomModel.
     */
    public static ViewApplicationRoomModel getViewApplicationRoomModel() {
        return (ViewApplicationRoomModel) getSingletonInstance(
                "ViewApplicationRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the
     *  ViewApplicationRoomView.
     */
    public static ViewApplicationRoomView getViewApplicationRoomView() {
        return (ViewApplicationRoomView) getSingletonInstance(
                "ViewApplicationRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the
     ViewContestRoomController. */
    public static ViewContestRoomController getViewContestRoomController() {
        return (ViewContestRoomController)
                getSingletonInstance("ViewContestRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     * ViewContestRoomModel.
     */
    public static ViewContestRoomModel getViewContestRoomModel() {
        return (ViewContestRoomModel) getSingletonInstance(
                "ViewContestRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the
     *  ViewContestRoomView.
     */
    public static ViewContestRoomView getViewContestRoomView() {
        return (ViewContestRoomView) getSingletonInstance("ViewContestRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the
     ViewProblemRoomController. */
    public static ViewProblemRoomController getViewProblemRoomController() {
        return (ViewProblemRoomController)
                getSingletonInstance("ViewProblemRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     * ViewProblemRoomModel.
     */
    public static ViewProblemRoomModel getViewProblemRoomModel() {
        return (ViewProblemRoomModel) getSingletonInstance(
                "ViewProblemRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the
     *  ViewProblemRoomView.
     */
    public static ViewProblemRoomView getViewProblemRoomView() {
        return (ViewProblemRoomView) getSingletonInstance("ViewProblemRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the

     /** Creates if necessary and returns an instance of the
     MainUserRoomController. */
    public static MainUserRoomController getMainUserRoomController() {
        return (MainUserRoomController)
                getSingletonInstance("MainUserRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     ViewUserRoomController. */
    public static ViewUserRoomController getViewUserRoomController() {
        return (ViewUserRoomController)
                getSingletonInstance("ViewUserRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     * ViewUserRoomModel.
     */
    public static ViewUserRoomModel getViewUserRoomModel() {
        return (ViewUserRoomModel) getSingletonInstance("ViewUserRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the
     *  ViewUserRoomView.
     */
    public static ViewUserRoomView getViewUserRoomView() {
        return (ViewUserRoomView) getSingletonInstance("ViewUserRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the MainUserRoomModel.
     */
    public static MainUserRoomModel getMainUserRoomModel() {
        return (MainUserRoomModel) getSingletonInstance("MainUserRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the MainUserRoomView.
     */
    public static MainUserRoomView getMainUserRoomView() {
        return (MainUserRoomView) getSingletonInstance("MainUserRoomViewClass");
    }

    /** Creates if necessary and returns an instance of the
     MainApplicationRoomController. */
    public static MainApplicationRoomController getMainApplicationRoomController() {
        return (MainApplicationRoomController)
                getSingletonInstance("MainApplicationRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the
     *  MainApplicationRoomModel.
     */
    public static MainApplicationRoomModel getMainApplicationRoomModel() {
        return (MainApplicationRoomModel) getSingletonInstance(
                "MainApplicationRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the
     *  MainApplicationRoomView.
     */
    public static MainApplicationRoomView getMainApplicationRoomView() {
        return (MainApplicationRoomView) getSingletonInstance(
                "MainApplicationRoomViewClass");
    }


    /** Creates if necessary and returns an instance of the
     ApplicationRoomController. */
    public static ApplicationRoomController getApplicationRoomController() {
        return (ApplicationRoomController)
                getSingletonInstance("ApplicationRoomControllerClass");
    }

    /** Creates if necessary and returns an instance of the ApplicationRoomModel.
     */
    public static ApplicationRoomModel getApplicationRoomModel() {
        return (ApplicationRoomModel) getSingletonInstance(
                "ApplicationRoomModelClass");
    }

    /** Creates if necessary and returns an instance of the ApplicationRoomView.
     */
    public static ApplicationRoomView getApplicationRoomView() {
        return (ApplicationRoomView) getSingletonInstance("ApplicationRoomViewClass");
    }


    /** Creates if necessary and returns an instance of the MenuController.
     */
    public static MenuController getMenuController() {
        return (MenuController) getSingletonInstance("MenuControllerClass");
    }

    /** Creates if necessary and returns an instance of the MenuModel. */
    public static MenuModel getMenuModel() {
        return (MenuModel) getSingletonInstance("MenuModelClass");
    }

    /** Creates if necessary and returns an instance of the MenuView. */
    public static MenuView getMenuView() {
        return (MenuView) getSingletonInstance("MenuViewClass");
    }

    /** Creates if necessary and returns an instance of the StatusController.
     */
    public static StatusController getStatusController() {
        return (StatusController) getSingletonInstance("StatusControllerClass");
    }

    /** Creates if necessary and returns an instance of the StatusModel. */
    public static StatusModel getStatusModel() {
        return (StatusModel) getSingletonInstance("StatusModelClass");
    }

    /** Creates if necessary and returns an instance of the StatusView. */
    public static StatusView getStatusView() {
        return (StatusView) getSingletonInstance("StatusViewClass");
    }

    /** Creates if necessary and returns an instance of the TestView. */
    public static TestView getTestView() {
        return (TestView) getSingletonInstance("TestViewClass");
    }

    /** Creates if necessary and returns an instance of the TestController. */
    public static TestController getTestController() {
        return (TestController) getSingletonInstance("TestControllerClass");
    }

    /** Creates if necessary and returns an instance of the TestModel. */
    public static TestModel getTestModel() {
        return (TestModel) getSingletonInstance("TestModelClass");
    }

    /** Creates if necessary and returns an instance of the PopupView. */
    public static PopupView getPopupView() {
        return (PopupView) getSingletonInstance("PopupViewClass");
    }

    /** Creates if necessary and returns an instance of the PopupController. */
    public static PopupController getPopupController() {
        return (PopupController) getSingletonInstance("PopupControllerClass");
    }

    /** Creates if necessary and returns an instance of the PopupModel. */
    public static PopupModel getPopupModel() {
        return (PopupModel) getSingletonInstance("PopupModelClass");
    }

    public static ViewTeamProblemRoomController getViewTeamProblemRoomController() {
        return (ViewTeamProblemRoomController) getSingletonInstance(
                "ViewTeamProblemRoomControllerClass");
    }

    public static ViewTeamProblemRoomModel getViewTeamProblemRoomModel() {
        return (ViewTeamProblemRoomModel) getSingletonInstance(
                "ViewTeamProblemRoomModelClass");
    }

    public static ViewTeamProblemRoomView getViewTeamProblemRoomView() {
        return (ViewTeamProblemRoomView) getSingletonInstance(
                "ViewTeamProblemRoomViewClass");
    }
    
    public static ViewLongProblemRoomController getViewLongProblemRoomController() {
        return (ViewLongProblemRoomController) getSingletonInstance(
                "ViewLongProblemRoomControllerClass");
    }

    public static ViewLongProblemRoomModel getViewLongProblemRoomModel() {
        return (ViewLongProblemRoomModel) getSingletonInstance(
                "ViewLongProblemRoomModelClass");
    }

    public static ViewLongProblemRoomView getViewLongProblemRoomView() {
        return (ViewLongProblemRoomView) getSingletonInstance(
                "ViewLongProblemRoomViewClass");
    }

    public static WebServiceRoomController getWebServiceRoomController() {
        return (WebServiceRoomController) getSingletonInstance(
                "WebServiceRoomControllerClass");
    }

    public static WebServiceRoomModel getWebServiceRoomModel() {
        return (WebServiceRoomModel) getSingletonInstance(
                "WebServiceRoomModelClass");
    }

    public static WebServiceRoomView getWebServiceRoomView() {
        return (WebServiceRoomView) getSingletonInstance(
                "WebServiceRoomViewClass");
    }

    public static ViewComponentRoomController getViewComponentRoomController() {
        return (ViewComponentRoomController) getSingletonInstance(
                "ViewComponentRoomControllerClass");
    }

    public static ViewComponentRoomModel getViewComponentRoomModel() {
        return (ViewComponentRoomModel) getSingletonInstance(
                "ViewComponentRoomModelClass");
    }

    public static ViewComponentRoomView getViewComponentRoomView() {
        return (ViewComponentRoomView) getSingletonInstance(
                "ViewComponentRoomViewClass");
    }

    /**
     * Gets lookup values object.
     *
     * @return Lookup values.
     * @since 1.1
     */
    public static LookupValues getLookupValues() {
        synchronized (MainObjectFactory.class) {
            return LOOKUP_VALUES;
        }
    }

    /**
     * Sets lookup values.
     *
     * @param lookupValues Lookup values.
     * @throws IllegalArgumentException If argument is null.
     * @since 1.1
     */
    public static void setLookupValues(LookupValues lookupValues) {
        if (lookupValues == null) throw new IllegalArgumentException("lookupValues must be not null.");
        synchronized (MainObjectFactory.class) {
            LOOKUP_VALUES = lookupValues;
        }
    }
}
