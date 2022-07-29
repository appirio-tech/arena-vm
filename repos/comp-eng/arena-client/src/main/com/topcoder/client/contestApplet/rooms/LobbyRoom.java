package com.topcoder.client.contestApplet.rooms;

/*
* LobbyRoom.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.util.*;
import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.uilogic.panels.*;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIComponent;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class LobbyRoom extends RoomModule {

    // panel variables
    private RoomPanel panel = null;
    private ChatPanel chatPanel = null;
    private UserTablePanel userPanel = null;
    //private JPanel workPanel = null;
    private RoomCompPanel lcp = null;
    private UIPage page = null;

    /**
     * Class constructor
     */
    LobbyRoom(ContestApplet parent) {
        super(parent, ContestConstants.LOBBY_ROOM);
        create();
    }

    /* Da Twink Daddy - 05/15/2002 - New constructor */
    /**
     * Creates a new LobbyRoom what acts as the given room type.
     *
     * @param     parent  The parent GUI and controller of the LobbyRoom
     * @param     type    The type of room to pretend to be.
     */
    LobbyRoom(ContestApplet parent, int type) {
        super(parent, type);
        create();
    }
   
    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    private void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        String title;
        switch (currentRoom) {
        case ContestConstants.LOBBY_ROOM:
            title = "Lobby";
            break;
        case ContestConstants.MODERATED_CHAT_ROOM:
            title = "Moderated Chat";
            break;
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.LOGIN_ROOM:
        case ContestConstants.CODER_ROOM:
        case ContestConstants.TEAM_CODER_ROOM:
        case ContestConstants.SPECTATOR_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
        case ContestConstants.PRACTICE_SPECTATOR_ROOM:
        case ContestConstants.WATCH_ROOM:
            title = "Unsupported room type " + currentRoom;
            System.err.println("Unsupported room type (" + currentRoom + ").");
            break;
        case ContestConstants.INVALID_ROOM:
            title = "Invalid Room";
            System.err.println("Invalid room type (" + currentRoom + ").");
            break;
        default:
            title = "Unknown room type " + currentRoom;
            //System.err.println("Unknown room type (" + currentRoom + ").");
            break;
        }

        page = parentFrame.getCurrentUIManager().getUIPage("lobby");
        lcp = new LobbyCompPanel(page);
        panel = new RoomPanel(title, parentFrame, createWorkPanel(), lcp, page);
        panel.showTimer();
    }


    ////////////////////////////////////////////////////////////////////////////////
    public void enter()
            ////////////////////////////////////////////////////////////////////////////////
    {
        chatPanel.enter();

        // If this is the first time a user has ever
        // logged in, show her the first time user panel
        boolean filter = parentFrame.getModel().getUserInfo().isAdmin() ||
                parentFrame.getModel().getUserInfo().isGuest();
        if (!filter && parentFrame.getModel().getUserInfo().isFirstTimeUser())
            displayFirstTimeUserFrame();
        else if (!filter && parentFrame.getModel().getUserInfo().getNumRatedEvents() == 0)
            displayNeverCompetedFrame();
    }
    
    public void setConnectionStatus(boolean on ) {
        panel.setStatusLabel(on);
        
        chatPanel.setPanelEnabled(on);
        userPanel.setPanelEnabled(on);
    }


    private static boolean once = true;
    private static final String FirstTimeUserEnabledKey = "com.topcoder.firstTimeUser.enabled";
    private static final String FirstTimeUserLocationKey = "com.topcoder.firstTimeUser.location";
    private static final String FirstTimeUserTitleKey = "com.topcoder.firstTimeUser.title";
    private static final String FirstTimeUserWidthKey = "com.topcoder.firstTimeUser.width";
    private static final String FirstTimeUserHeightKey = "com.topcoder.firstTimeUser.height";
    private static final String NeverCompetedEnabledKey = "com.topcoder.neverCompeted.enabled";
    private static final String NeverCompetedLocationKey = "com.topcoder.neverCompeted.location";
    private static final String NeverCompetedTitleKey = "com.topcoder.neverCompeted.title";
    private static final String NeverCompetedWidthKey = "com.topcoder.neverCompeted.width";
    private static final String NeverCompetedHeightKey = "com.topcoder.neverCompeted.height";

    private void displayFirstTimeUserFrame() {
        if (!once)
            return;
        once = false;
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getResourceAsStream("/firstTimeUser.properties"));
            String enabled = prop.getProperty(FirstTimeUserEnabledKey);
            if (enabled == null || !Boolean.valueOf(enabled).equals(Boolean.TRUE))
                return;
            displayFrame(
                    prop.getProperty(FirstTimeUserTitleKey, "First Time User"),
                    prop.getProperty(FirstTimeUserLocationKey),
                    Integer.parseInt(prop.getProperty(FirstTimeUserWidthKey, "" + Common.WIDTH)),
                    Integer.parseInt(prop.getProperty(FirstTimeUserHeightKey, "" + Common.HEIGHT))
            );
        } catch (Exception e) {
            System.err.println("Error loading first time user panel");
            e.printStackTrace();
        }
    }


    private void displayNeverCompetedFrame() {
        if (!once)
            return;
        once = false;
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getResourceAsStream("/neverCompeted.properties"));
            String enabled = prop.getProperty(NeverCompetedEnabledKey);
            if (enabled == null || !Boolean.valueOf(enabled).equals(Boolean.TRUE))
                return;
            displayFrame(
                    prop.getProperty(NeverCompetedTitleKey, "Competition"),
                    prop.getProperty(NeverCompetedLocationKey),
                    Integer.parseInt(prop.getProperty(NeverCompetedWidthKey, "" + Common.WIDTH)),
                    Integer.parseInt(prop.getProperty(NeverCompetedHeightKey, "" + Common.HEIGHT))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayFrame(String title, String location, int width, int height) throws Exception {
        HTMLPanel htmlPanel = new HTMLPanel(parentFrame);
        htmlPanel.load(location, parentFrame);
        htmlPanel.replaceVariables(new String[]{"$USERNAME"},
                                   new String[]{parentFrame.getModel().getCurrentUser()}
        );

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(htmlPanel.getPanel(), BorderLayout.CENTER);
        frame.setSize(width, height);
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), frame);
        frame.show();
        MoveFocus.moveFocus(frame);
    }


    public boolean leave() {
        chatPanel.leave();
        unsetModel();
        return (true);
    }

    public void resetFocus() {
        chatPanel.leave();
        chatPanel.enter();
    }

    /**
     * create the work panel
     */
    private UIComponent createWorkPanel() {
        // globalize needed variables
        chatPanel = new ChatPanel(parentFrame, page);
        userPanel = new UserTablePanel(parentFrame, page);

        return page.getComponent("work_panel_base");
    }

    /**
     * return the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public JPanel reload()
            ////////////////////////////////////////////////////////////////////////////////
    {
        panel.getWorkPanel().performAction("revalidate");
        panel.getWorkPanel().performAction("repaint");
        return ((JPanel) panel.getPanel().getEventSource());
    }

    /**
     * Clear out all room data
     */
    public void clear() {
        super.clear();

        userPanel.clear();
        chatPanel.clear();
    }

    public void setStatus(String msg) {
    }

    public void setName(String cn) {
    }

    protected void addViews() {    	    	
        roomModel.addUserListView(userPanel);
        roomModel.addUserListView(chatPanel);
        roomModel.addChatView(chatPanel);

        lcp.updateContestInfo(roomModel.getStatus());
        panel.getCompPanel().setContestName(roomModel.getName());
    }

    void clearViews() {
        lcp.updateContestInfo("");
        panel.getCompPanel().setContestName("");

        if (roomModel != null) {
            roomModel.removeChatView(chatPanel);
            roomModel.removeUserListView(chatPanel);
            roomModel.removeUserListView(userPanel);
        }
    }
}
