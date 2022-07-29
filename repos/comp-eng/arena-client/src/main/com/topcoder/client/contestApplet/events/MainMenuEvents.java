package com.topcoder.client.contestApplet.events;

/**
 * MainMenuEvents.java
 *
 * Created on June 12, 2001, 3:06 PM
 */

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.editors.setup.EditorPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.AppletPreferencesDialog;
import com.topcoder.client.contestApplet.uilogic.frames.MessageDialog;
import com.topcoder.client.contestApplet.uilogic.frames.ClearProblemsDialog;
import com.topcoder.client.contestApplet.rooms.CoderRoomInterface;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.RoomView;

/**
 * This class handles all the main menu triggered events.
 *
 * @author Alex Roman
 * @version
 */
public final class MainMenuEvents {
    
    private ContestApplet ca = null;
    
    /**
     * Default constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public MainMenuEvents(ContestApplet ca)
    ////////////////////////////////////////////////////////////////////////////////
    {
        this.ca = ca;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    public void searchButtonEvent()
    ///////////////////////////////////////////////////////////////////////////////
    {
        String search = Common.input("Search",
                "Enter a username:",
                ca.getMainFrame());
        
        if (search != null) {
            ca.getRequester().requestSearch(search);
        }
    }
    
    public void aboutEvent() {
        StringBuffer sb = new StringBuffer();
        sb.append("              TopCoder\n");
        sb.append("        Competition Arena\n");
        sb.append("            Version ");
        sb.append(CommonData.CURRENT_VERSION);
        sb.append("\n\n");
        sb.append("      Updated: ");
        sb.append(CommonData.UPDATE_DATE);
        sb.append("\n\n");
        sb.append("     Java Version: ");
        sb.append(System.getProperty("java.version"));
        sb.append("\n\n");
        
        Common.showMessage("About", sb.toString(), ca.getMainFrame());
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void changeLogEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        String msg = "";
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ca.getClass().getResource("CHANGELOG.log").openStream()));
            String s = "";
            while ((s = br.readLine()) != null)
                msg = msg + s + "\n";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        MessageDialog md = new MessageDialog(ca, ca.getCurrentFrame(), "ChangeLog", msg, true, true);
        md.showDialog();
    }
        
    ////////////////////////////////////////////////////////////////////////////////
    public void contestManualEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_MAN));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void contestFAQEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_CMP_FAQ));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void contestPluginsEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_PLUGINS));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////
    public void activeUsersButtonEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.getRequester().requestActiveUsers();
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void importantMessagesButtonEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.getRequester().requestImportantMessages();
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void visitedPracticeEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.getRequester().requestVisitedPractice();
    }    
    
    ////////////////////////////////////////////////////////////////////////////////
    public void registerButtonEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_REG));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////
    public void logoffButtonEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        if (Common.confirm("Warning",
                "Are you sure you want to logoff?",
                ca.getMainFrame())) {
            if (ca.getRoomManager().getCurrentRoom().leave()) {
                ca.getModel().logoff();
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void chatToggleEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.getRequester().requestToggleChat();
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void chatScrollingEvent(ActionEvent e)
    ////////////////////////////////////////////////////////////////////////////////
    {
        LocalPreferences.getInstance().toggleDisableChatScrolling();
        ca.setDisableScrollingChat(!ca.isScrollingChatDisabled());
    }
    
    public void leaderTickerEvent(ActionEvent e) {
        LocalPreferences.getInstance().toggleLeaderTicker();
    }
    
    public void clearProblemsEvent() {
        RoomModel currentRoom = ca.getModel().getCurrentRoom();
        if (isCodingWindowOpened(currentRoom)) {
            Common.showMessage("Error", "You must close the coding window in order to clear the problems.", ca.getCurrentFrame());
            return;
        }
        if (Common.confirm("Clear Problems", "Are you sure you wish to clear the problems?", null)) {
            Long roomID = currentRoom.getRoomID();
            ca.getRequester().requestClearPractice(roomID.longValue());
        }
    }
    
    private boolean isCodingWindowOpened(RoomModel roomModel) {
        RoomView roomView = roomModel.getCurrentRoomView();
        return roomView != null && roomView instanceof CoderRoomInterface && ((CoderRoomInterface) roomView).isCodingWindowOpened();
    }
    
    public void clearSelectedProblemEvent() {
        RoomModel currentRoom = ca.getModel().getCurrentRoom();
        if (isCodingWindowOpened(currentRoom)) {
            Common.showMessage("Error", "You must close the coding window in order to clear a problem.", ca.getCurrentFrame());
            return;
        }
        ClearProblemsDialog pnl = new ClearProblemsDialog(ca.getMainFrame(), ca);
        Common.setLocationRelativeTo(ca.getMainFrame(), (Component) pnl.getFrame().getEventSource());
        pnl.show();
        /*
        if (Common.confirm("Clear Problems", "Are you sure you wish to clear the problems?", null)) {
            Long roomID = ca.getModel().getCurrentRoom().getRoomID();
            ca.getRequester().requestClearPractice(roomID.longValue());
        }  */
    }
    
    public void systemTestEvent() {
        RoomModel currentRoom = ca.getModel().getCurrentRoom();
        String handle = ca.getModel().getUserInfo().getHandle();
        Coder coder = currentRoom.getCoder(handle);
        CoderComponent[] components = coder.getComponents();
        if (components != null && components.length > 0) {
            int[] componentsId = new int[components.length];
            for (int i = 0; i < components.length; i++) {
                CoderComponent c = components[i];
                componentsId[i] = c.getComponent().getID().intValue();
            }
            ca.getRequester().requestPracticeSystemTest(currentRoom.getRoomID().longValue(), componentsId);
        } else {
            System.out.println("Unexpected state, CoderComponent[] is null or empty");
        }
    }
    
    public void statusWindowEvent(ActionEvent e) {
        ((CoderRoomInterface) ca.getRoomManager().getCurrentRoom()).challengeButtonEvent(e);
    }
    
    // POPS - 10/18/2001 - added event to respond to selecting editor options
    public void editorOptionsEvent() {
        EditorPreferences pref = new EditorPreferences(ca.getMainFrame());
        Common.setLocationRelativeTo(ca.getMainFrame(), pref);
        pref.show();
        
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // POPS - 2/22/2002 - added event to respond to setting up chat colors
    public void setupUserPreferences()
    ////////////////////////////////////////////////////////////////////////////////
    {
        // AdamSelene - working on this.
        //ChatColorPreferences pref = new ChatColorPreferences(ca.getMainFrame());
        AppletPreferencesDialog pref = new AppletPreferencesDialog(ca.getMainFrame());
        Common.setLocationRelativeTo(ca.getMainFrame(), (Component) pref.getFrame().getEventSource());
        pref.show();
        
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // POPS - 12/19/2001 - added event to respond to selecting disable enter/exit msgs
    public void disableEnterExitMsgsEvent()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.setDisableEnterExitMsgs(!ca.isEnterExitMsgsDisabled());
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // POPS - 12/28/2001 - added event to respond to selecting disable enhancedmode msgs
    public void disableAutoEnhancedChat()
    ////////////////////////////////////////////////////////////////////////////////
    {
        ca.setdisableAutoEnhancedChat(!ca.isAutoEnhancedChatDisabled());
    }
    
    public void disableChatHistory() {
        ca.setDisableChatHistory(!ca.isChatHistoryDisabled());
    }
    
    public void disableChatFindTabs() {
        ca.setDisableChatFindTabs(!ca.isChatFindTabsDisabled());
    }
    
    public void disableBroadcastPopup() {
        LocalPreferences.getInstance().toggleBroadcastPopup();
    }
    
    public void disableBroadcastBeep() {
        LocalPreferences.getInstance().toggleBroadcastBeep();
    }
    
    public void enableTimestamps() {
        LocalPreferences.getInstance().toggleEnableTimestamps();
    }
    
    public void enabledUnusedCodeCheck() {
        LocalPreferences.getInstance().toggleEnableUnusedCodeCheck();
    }
    
    
    public void teamMemberButtonEvent() {
        ca.showTeamManagerMember();
    }
    
    public void teamCaptainButtonEvent() {
        ca.showTeamManagerCaptain();
    }
}

