package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MenuController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.model.MenuModel;
import com.topcoder.client.mpsqasApplet.common.MenuConstants;
import com.topcoder.client.mpsqasApplet.common.Descriptions;

import java.net.URL;

/**
 * An implementation of the Menu Controller, reacts to menu choices by
 * calling request methods in appropriate request processors.
 *
 * @author mitalub
 */
public class MenuControllerImpl implements MenuController {

    private MenuModel model;

    /**
     * Stores the model.
     */
    public void init() {
        model = MainObjectFactory.getMenuModel();

        //create menus
        resetMenus();
    }

    /** Does nothing. */
    public void placeOnHold() {
    }

    /** Does nothing. */
    public void takeOffHold() {
    }

    /**
     * Resets the menus to match the current admin status.
     */
    private void resetMenus() {
        if (MainObjectFactory.getMainAppletModel().isAdmin()) {
            model.setMenuHeaders(MenuConstants.A_MENU_HEADERS);
            model.setMenuItems(MenuConstants.A_MENU_ITEMS);
            model.setMenuIds(MenuConstants.A_MENU_IDS);
        } else {
            model.setMenuHeaders(MenuConstants.MENU_HEADERS);
            model.setMenuItems(MenuConstants.MENU_ITEMS);
            model.setMenuIds(MenuConstants.MENU_IDS);
        }
    }

    /**
     * Reacts to a menu choice by calling the method in the appropriate request
     * processor.
     *
     * @param choiceId The choice id of the menu option choosen, enumerated in
     *                 <code>MenuConstants</code>.
     */
    public void processMenuChoice(int choiceId) {
        switch (choiceId) {
        case MenuConstants.EXIT:
            System.exit(0);
            break;
        case MenuConstants.FOYER:
            MainObjectFactory.getMoveRequestProcessor().loadFoyerRoom();
            break;
        case MenuConstants.MAIN_INDIVIDUAL_PROBLEM_ROOM:
            MainObjectFactory.getMoveRequestProcessor().loadMainProblemRoom();
            break;
        case MenuConstants.UPCOMING_CONTESTS:
            MainObjectFactory.getMoveRequestProcessor().loadMainContestRoom();
            break;
        case MenuConstants.PROBLEM_WRITER_APPLICATION:
            MainObjectFactory.getMoveRequestProcessor().loadWriterApplication();
            break;
        case MenuConstants.PROBLEM_TESTER_APPLICATION:
            MainObjectFactory.getMoveRequestProcessor().loadTesterApplication();
            break;
        case MenuConstants.PENDING_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadPendingApprovalRoom();
            break;
        case MenuConstants.PENDING_APPLICATIONS:
            MainObjectFactory.getMoveRequestProcessor().loadMainApplicationRoom();
            break;
        case MenuConstants.ALL_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadAllProblems();
            break;
        case MenuConstants.ALL_TEAM_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadAllTeamProblems();
            break;
        case MenuConstants.ALL_LONG_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadAllLongProblems();
            break;
        case MenuConstants.USERS:
            MainObjectFactory.getMoveRequestProcessor().loadMainUserRoom();
            break;
        case MenuConstants.CONTENTS:
            loadHelp();
            break;
        case MenuConstants.CHANGE_LOG:
            MainObjectFactory.getIPopupRequestProcessor().popupMessage(
                    Descriptions.CHANGE_LOG);
            break;
        case MenuConstants.ABOUT:
            MainObjectFactory.getIPopupRequestProcessor().popupMessage(
                    Descriptions.ABOUT);
            break;
        case MenuConstants.MAIN_TEAM_PROBLEM_ROOM:
            MainObjectFactory.getMoveRequestProcessor().loadMainTeamProblemRoom();
            break;
        case MenuConstants.PENDING_TEAM_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadTeamPendingApprovalRoom();
            break;
        case MenuConstants.MAIN_LONG_PROBLEM_ROOM:
            MainObjectFactory.getMoveRequestProcessor().loadMainLongProblemRoom();
            break;
        case MenuConstants.PENDING_LONG_PROBLEMS:
            MainObjectFactory.getMoveRequestProcessor().loadLongPendingApprovalRoom();
            break;
        default:
            System.out.println("Unrecognized menu choice: " + choiceId);
        }
    }

    /**
     * Trys to load the help file URL.
     */
    private void loadHelp() {
        try {
            MainObjectFactory.getMainApplet().getLauncher()
                    .getAppletContext().showDocument(
                            new URL(Descriptions.HELP_CONTENTS_URL), "_blank");
        } catch (Exception ue) {
            System.out.println("Error loading help:");
            ue.printStackTrace();
        }
    }

    /**
     * If the user is an admin, gives them the admin menu
     */
    public void processAcceptedLogin(boolean isAdmin, boolean isWriter,
            boolean isTester) {
        resetMenus();
        model.notifyWatchers();
    }

    /** Does nothing, menu does not care. */
    public void processRefusedLogin(String reason) {
    }

}
