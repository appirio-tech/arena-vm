package com.topcoder.client.contestApplet;

/**
 * ContestApplet.java
 *
 * Created on July 6, 2000, 8:43 PM
 */

import java.applet.AppletContext;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.*;
import com.topcoder.netCommon.contestantMessages.response.*;
import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.editors.*;
import com.topcoder.client.contestApplet.uilogic.frames.*;
import com.topcoder.client.contestApplet.rooms.*;
import com.topcoder.client.contestApplet.panels.main.*;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestApplet.uilogic.views.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestant.message.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.ui.*;

// Obselete frames
import com.topcoder.client.contestApplet.frames.VotingFrame;
import com.topcoder.client.contestApplet.frames.VotingResultsFrame;
import com.topcoder.client.contestApplet.frames.RoundStatsFrame;
import com.topcoder.client.contestApplet.frames.BadgeIdDialog;
import com.topcoder.client.contestApplet.frames.WLMyTeamInfoFrame;
import com.topcoder.client.contestApplet.frames.WLTeamsInfoFrame;
import com.topcoder.client.contestApplet.frames.TeamManagerFrame;

// We do not convert main frame at present
import com.topcoder.client.contestApplet.frames.MainFrame;

/**
 * This class is responsible for initializing the TopCoder
 * Contest Applet/Application. It can be launched inside a
 * webpage, by a button in a web page, or as a java application.
 *
 * @author  Alex Roman
 * @version
 */

public final class ContestApplet extends JApplet implements ContestantView {
    // The horse that brung us
    private JApplet launchApplet = null;

    // The model
    private Contestant model;

    // Used for displaying messages while waiting for a response
    // (e.g., "Fetching coder info...")
    private MessageFrame interFrame = null;


    // Essentially a view manager
    // handles swapping out rooms
    // with the server during moves.
    private RoomManager roomManager = null;

    // The one and only, main frame.
    // Where rooms live.
    private MainFrame mainFrame = null;
    private JFrame currentFrame = null;

    private boolean disableChatScrolling = false;
    private boolean disableLeaderTicker = false;

    // POPS - 12/19/2001 - added boolean for disable enter/exit msgs
    private boolean disableEnterExitMsgs = false;
    private boolean disableAutoEnhancedChat = false;
    private boolean disableChatHistory = false;
    private boolean disableChatFindTabs = false;
    private boolean allowSSL = false;
    
    public static final String DISABLEENTEREXITMSGSPROPERTY = "com.topcoder.jmaContestApplet.ContestApplet.disableEnterExitMsgs";
    public static final String DISABLEAUTOENHANCEDCHAT = "com.topcoder.jmaContestApplet.ContestApplet.disableAutoEnhancedChat";
    public static final String DISABLECHATHISTORY = "com.topcoder.jmaContestApplet.ContestApplet.disableChatHistory";
    public static final String DISABLECHATFINDTABS = "com.topcoder.jmaContestApplet.ContestApplet.disableChatFindTabs";
    
    private String companyName;
    private String sponsorName;
    private boolean poweredByView = false;

    // The main menu
    private MainMenuPanel menuPanel = null;

    // Watch rooms (roomIndex to RoomModule)
    private HashMap roomHash = new HashMap();

    // "Who's registered"
    private RegistrantsTableFrame rtf = null;
    private RegistrantsTableFrame hsrtf = null; // high school
    private RegistrantsTableFrame mmrtf = null; // marathon
    
    private ImportantMessageSummaryFrame imf = null;
    private PracticeSystestResultsFrame psrf = null;

    // Who's logged in
    private ActiveUsersTableFrame autf = null;

    private TeamManagerFrame tmfm = null;
    private TeamManagerFrame tmfc = null;

    private ActiveRoundsMenu activeRoundsMenu;
    
    // Visited Practice Rooms
    private VisitedPracticeFrame vpf = null;

    private UIManager currentUIManager = null;

    private ViewerLogic codingViewingFrame = null;

    private Object viewerLock = new Object();

    public ActiveRoundsMenu getActiveRoundsMenu() {
        return activeRoundsMenu;
    }

    public UIManager getCurrentUIManager() {
        return currentUIManager;
    }


    // Override this to do testing
    protected Contestant createModel() {
        return new com.topcoder.client.contestant.impl.ContestantImpl(allowSSL);
    }


    /**
     * ------------------------------------------------------------
     * Class Constructors
     * ------------------------------------------------------------
     */
    public ContestApplet(String host, int port, String tunnel, String companyName, String destinationHost, boolean poweredByView, String sponsorName)
    {

        // POPS - 11/27/2001 - changed to set a security manager
        try {
            System.setSecurityManager(new TCSecurityManager());
        } catch (java.security.AccessControlException e) {
            System.out.println("Cannot create the security manager - plugins will not work");
        }

        // POPS - 12/19/2001 - restore properties
        LocalPreferences pref = LocalPreferences.getInstance();
        if (pref != null) {
            String temp = pref.getProperty(DISABLEENTEREXITMSGSPROPERTY);
            disableEnterExitMsgs = temp == null ? false : temp.equals("true");

            temp = pref.getProperty(DISABLEAUTOENHANCEDCHAT);
            disableAutoEnhancedChat = temp == null ? false : temp.equals("true");
            
            temp = pref.getProperty(DISABLECHATHISTORY);
            disableChatHistory = (temp == null) ? false : temp.equals("true");
            
            temp = pref.getProperty(DISABLECHATFINDTABS);
            disableChatFindTabs = (temp == null) ? false : temp.equals("true");

            // By default, we enable SSL
            temp = pref.getProperty(LocalPreferences.CONNECTION_SSL);
            allowSSL = (temp == null) ? allowSSL : temp.equals("true");
        }

        //UI manager
        currentUIManager = null;
        String themeName = pref.getProperty(LocalPreferences.UI_THEME, "Default");
        UIManager[] managers = pref.getAllUIManagers();
        
        // try to look for the theme.
        for (int i=0;i<managers.length;++i) {
            if (managers[i].getName().equals(themeName)) {
                currentUIManager = managers[i];
                break;
            }
        }

        if (currentUIManager == null) {
            // if the manager cannot be found, use default theme.
            currentUIManager = managers[0];
        }

        currentUIManager.create();

        // set the default widget properties
        //ContestUIDefaults.set();

        // initialize crucial objects
        this.companyName = companyName;
        this.sponsorName = sponsorName;
        this.poweredByView = poweredByView;
        if (companyName == null || companyName.trim().length() == 0) this.companyName = "TopCoder";
        if (sponsorName == null || sponsorName.trim().length() == 0) this.sponsorName = companyName;
        // POPS - 9/12/2002
        if (companyName.equalsIgnoreCase(ContestConstants.COMPANY_SUN)) this.companyName = ContestConstants.COMPANY_SUN;

        // initialize crucial objects
        
        model = createModel();
        roomManager = new RoomManager(this, this.companyName);
        activeRoundsMenu = new ActiveRoundsMenu("Active Contests", 120, 14, 'a', this);
        mainFrame = new MainFrame(this);
        currentFrame = mainFrame;
        interFrame = new MessageFrame("", mainFrame, this);
        menuPanel = mainFrame.getMenuPanel();
        rtf = new RegistrantsTableFrame(this,RegistrantsTableFrame.OTHER);
        hsrtf = new RegistrantsTableFrame(this,RegistrantsTableFrame.HIGH_SCHOOL);
        mmrtf = new RegistrantsTableFrame(this,RegistrantsTableFrame.MARATHON);
        imf = new ImportantMessageSummaryFrame(this);
        psrf = new PracticeSystestResultsFrame(this);
        vpf = new VisitedPracticeFrame(this);
        autf = new ActiveUsersTableFrame(this);
        tmfm = new TeamManagerFrame(this, TeamManagerFrame.MEMBER);
        tmfc = new TeamManagerFrame(this, TeamManagerFrame.CAPTAIN);

        ConnectionType.registerAuthenticator(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AuthenticatorDialog.getUsername(), AuthenticatorDialog.getPassword());
                }
            });

        //GT 02/12/2003
        if (!destinationHost.equals("")) {
            System.out.println("This is my destination:" + destinationHost);
            model.setGoThroughProxy(true);
        }
        model.init(host, port, tunnel,this,autf,rtf,hsrtf,mmrtf,
            tmfm.getTeamListView(), tmfm.getAvailableListView(),
            tmfm.getMemberListView(), menuPanel,roomManager,new EventService() {
                public void invokeLater(Runnable runnable) {
                    EventQueue.invokeLater(runnable);
                }
            }, destinationHost);


        model.getRoundViewManager().addListener(activeRoundsMenu);

        //
        // Listen for new broadcasts, optionally popping up the dialog
        //
        model.getBroadcastManager().addBroadcastListener(mainBroadcastListener, false);
        roomManager.loadInitRoom();

              
        
        // this will start the ticker
//        setDisableLeaderTicker(false);
    }

    public ContestApplet(String host, int port, String tunnel, String roomVersion, JApplet la, String destinationHost, boolean poweredByView, String sponsorName)
    {
        this(host, port, tunnel,roomVersion, destinationHost, poweredByView, sponsorName);
        this.launchApplet = la;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public AppletContext getAppletContext()
            ////////////////////////////////////////////////////////////////////////////////
    {
        AppletContext ac = null;

        if (launchApplet != null) {
            ac = launchApplet.getAppletContext();
        }

        return (ac);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public MainFrame getMainFrame()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (mainFrame);
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public JFrame getImportantMessagesFrame()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (JFrame) imf.getFrame().getEventSource();
    }
    
    public JFrame getVisitedPracticeFrame() {
        return (JFrame) vpf.getFrame().getEventSource();
    }

    public void closeOtherCodingViewingFrame(ViewerLogic frame) {
        synchronized(viewerLock) {
            if (codingViewingFrame != null && codingViewingFrame != frame) {
                codingViewingFrame.closeWindow();
                codingViewingFrame.dispose();
            } 
            //we have the pending source code view not closed
            if(SourceViewer.PROBLEM_STATE.size()>0) {                
                for(Iterator it=SourceViewer.PROBLEM_STATE.iterator();it.hasNext();)
                {
                    String value = (String)it.next();
                    String[] problemInfos = value.split("_");
                    long problemID = Long.parseLong(problemInfos[0]);
                    String writer = problemInfos[1];
                    getRequester().requestCloseComponent(problemID, writer);
                    it.remove();
                }
            }

            codingViewingFrame = frame;
        }
    }

// --Recycle Bin START (5/8/02 2:09 PM):
//  ////////////////////////////////////////////////////////////////////////////////
//  public void hideInterFrame()
//  ////////////////////////////////////////////////////////////////////////////////
//  {
//    interFrame.hideMessage();
//  }
// --Recycle Bin STOP (5/8/02 2:09 PM)

    ////////////////////////////////////////////////////////////////////////////////
    public void mainFrameEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if(!connected)
            return;
        
        if (!roomManager.leave()) {
            return;
        }

        mainFrame.hide();
        model.logoff();
    }


    public void loggingOff() {
        leave();
        roomManager.loadRoom(ContestConstants.LOGIN_ROOM, IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void leave()
            ////////////////////////////////////////////////////////////////////////////////
    {
        mainFrame.leave();

        for (Enumeration e = (new Vector(roomHash.values())).elements(); e.hasMoreElements();) {
            RoomInfoFrame rif = (RoomInfoFrame) e.nextElement();
            rif.hide();
            rif.dispose();
        }
    }


    // ------------------------------------------------------------
    // Set/Get local Session Information
    // ------------------------------------------------------------

    public void setCurrentFrame(JFrame f) {
        this.currentFrame = f;
    }


    public JFrame getCurrentFrame() {
        return (this.currentFrame);
    }

    public void setDisableScrollingChat(boolean s) {
        disableChatScrolling = s;
    }

    public boolean isScrollingChatDisabled() {
        return (disableChatScrolling);
    }

//    public void setDisableLeaderTicker(boolean s) {
//        disableLeaderTicker = s;
//        if (s) {
//            mainFrame.getStatusPanel().getFaderPanel().setTickerEnabled(false);
//            mainFrame.getStatusPanel().getFaderPanel().clear();
//        }
//        else {
//            mainFrame.getStatusPanel().getFaderPanel().setTickerEnabled(true);
//        }
//    }

    public boolean isLeaderTickerDisabled() {
        return (disableLeaderTicker);
    }

    // POPS - 12/19/2001 - added methods to toggle disable enter/exit switch
    public boolean isEnterExitMsgsDisabled() {
        return (disableEnterExitMsgs);
    }

    public void setDisableEnterExitMsgs(boolean s) {
        // Attempt to save the preference to local preferences - if an error occurs, ignore it (probably no access exception)
        LocalPreferences pref = LocalPreferences.getInstance();
        pref.setProperty(DISABLEENTEREXITMSGSPROPERTY, s ? "true" : "false");
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        disableEnterExitMsgs = s;
    }

    // POPS - 12/28/2001 - added methods to toggle disable enhancedchat switch
    public boolean isAutoEnhancedChatDisabled() {
        return (disableAutoEnhancedChat);
    }

    public void setdisableAutoEnhancedChat(boolean s) {
        // Attempt to save the preference to local preferences - if an error occurs, ignore it (probably no access exception)
        LocalPreferences pref = LocalPreferences.getInstance();
        pref.setProperty(DISABLEAUTOENHANCEDCHAT, s ? "true" : "false");
        try {
            pref.savePreferences();
        } catch (Throwable t) {
        }

        disableAutoEnhancedChat = s;
    }
    
    
    public boolean isChatFindTabsDisabled() {
    	return disableChatFindTabs;
    }
    
    public void setDisableChatFindTabs(boolean disabled) {
    	LocalPreferences pref = LocalPreferences.getInstance();
        pref.setProperty(DISABLECHATFINDTABS, disabled ? "true" : "false");
        disableChatFindTabs = disabled;
        try {
            pref.savePreferences();
        } catch (IOException e) {
        }
    }
    
    
    public boolean isChatHistoryDisabled() {
        return disableChatHistory;
    }
    
    public void setDisableChatHistory(boolean disabled) {
        LocalPreferences pref = LocalPreferences.getInstance();
        pref.setProperty(DISABLECHATHISTORY, disabled ? "true" : "false");
        try {
            pref.savePreferences();
        } catch (IOException e) {
        }
        disableChatHistory = disabled;
    }


    /////////////////////////////////////////////////
    //
    // Listen for new broadcasts, optionally popping up the dialog
    //
    private BroadcastListener mainBroadcastListener = new BroadcastListener() {
        public void refreshBroadcasts() {
        }

        public void readBroadcast(AdminBroadcast bc) {
        }

        public void newBroadcast(AdminBroadcast bc) {
            String val = LocalPreferences.getInstance().getProperty(LocalPreferences.DISABLEBROADCASTPOPUP);
            String val2 = LocalPreferences.getInstance().getProperty(LocalPreferences.DISABLEBROADCASTBEEP);
            /*if (val == null) {
            val = "" + Common.confirm("Disable Broadcast Popup",
            "Would you like to disable the broadcast popup window?\nYou can change your setting later in the options menu."
            , getMainFrame()
            );
            LocalPreferences.getInstance().setProperty(LocalPreferences.DISABLEBROADCASTPOPUP, val);
            try {
            LocalPreferences.getInstance().savePreferences();
            }
            catch (Throwable t) {
            t.printStackTrace();
            }
            }*/
            if (val == null || !val.equals("true")) {
                new BroadcastDialog(ContestApplet.this, bc).show();
            }

            if (val2 == null || !val2.equals("true")) {
                Toolkit.getDefaultToolkit().beep();
                Toolkit.getDefaultToolkit().beep();
                Toolkit.getDefaultToolkit().beep();
            }
        }
    };

    public MessageFrame getInterFrame() {
        return interFrame;
    }

    public boolean isChatEnabled() {
        return menuPanel.isChatEnabled();
    }
    
    private void showCodePopup() {
        // make sure to save any open code for user before closing the connection.
        RoomModule cr = roomManager.getCurrentRoom();

        if (cr instanceof com.topcoder.client.contestApplet.rooms.CoderRoom) {
            if (((CoderRoom) cr).getCodingFrame().isShowing()) {
                String source = ((CoderRoom) cr).getCodingFrame().getCode();
                if (!source.equals("")) {
                    MessageDialog md = new MessageDialog(this, mainFrame,
                            "Please copy your working code into a local editor",
                            source);
                    md.show();
                }
            }
        }
    }
    
    public void reconnectFailedEvent() {
        showCodePopup();
        disconnectPopUp();
        connected = false;
    }

    public void closingConnectionEvent() {
        //showCodePopup();
    }
    
    private void disconnectPopUp() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    Common.showMessage("Client Connection Error",
                            "The connection to the server has been lost. Logging off.",
                            mainFrame);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // slow down evenve to minimize focus problems
        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }

        // reload the login room
        leave();
        roomManager.loadRoom(ContestConstants.LOGIN_ROOM, IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
    }
    
    private boolean connected = true;
    
    public void setConnectionStatus(boolean on) {
        connected = on;
        RoomModule cr = roomManager.getCurrentRoom();

        cr.setConnectionStatus(on);
        mainFrame.setMenuEnabled(on);
        RoomListFrame.getInstance(this).setPanelEnabled(on);
        BroadcastSummaryFrame.getInstance(this).setPanelEnabled(on);
        if(autf != null) {
            autf.setPanelEnabled(on);
        }
        if(rtf != null) {
            rtf.setPanelEnabled(on);
        }
        if(hsrtf != null) {
            hsrtf.setPanelEnabled(on);
        }
        
        //watch rooms
        RoomModel[] rooms = model.getRooms();
        for(int i = 0; i < rooms.length; i++) {
            if(rooms[i].getWatchView() != null && rooms[i].getWatchView() instanceof RoomInfoFrame) {
                ((RoomInfoFrame)rooms[i].getWatchView()).setPanelEnabled(on);
            }
        }
        
        if(this.activeRoundsMenu != null) {
            activeRoundsMenu.setPanelEnabled(on);
        }
    }

    public void lostConnectionEvent() {
        //disconnectPopUp();
        
        //gray out buttons here
        setConnectionStatus(false);
        
        getModel().startReconnectAttempt();
    }

    public Requester getRequester() {
        return model.getRequester();
    }

    public Contestant getModel() {
        return model;
    }


    ////////////////////////////////////////////////////////////////////////////////
    public void requestCoderInfo(String coder, int userType)
            ////////////////////////////////////////////////////////////////////////////////
    {
        getInterFrame().showMessage("Fetching coder info...",
                getCurrentFrame(),
                ContestConstants.CODER_INFO);
        model.getRequester().requestCoderInfo(coder, userType);
    }

    public void requestCoderHistory(String handle, long roomID, int userType) {
        getInterFrame().showMessage("Fetching coder history...",
                getCurrentFrame(),
                ContestConstants.CODER_HISTORY);
        model.getRequester().requestCoderHistory(handle, roomID, userType);
    }
    
    
    public void requestSubmissionHistory(String handle, long roomID, int userType, boolean example) {
        getInterFrame().showMessage("Fetching submission history...",
                getCurrentFrame(),
                ContestConstants.CODER_HISTORY);
        model.getRequester().requestSubmissionHistory(handle, roomID, userType, example);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public void popup(final int type, final int type2, final String title,
            final String msg, final ArrayList al, final Object o)
            ////////////////////////////////////////////////////////////////////////////////
    {
        interFrame.hideMessage();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (type2 == ContestConstants.TEXT_AREA) {
                    if (type == ContestConstants.ROOM_MOVE) {
                        MessageDialog md = new MessageDialog(ContestApplet.this, getCurrentFrame(), title, msg, true, true);
                        md.setButtonText((String) al.get(0));
                        md.setButton2Text((String) al.get(1));
                        boolean retVal = md.showDialog();

                        if (retVal) {
                            roomManager.loadRoom(((Integer) ((ArrayList) o).get(0)).intValue(), ((Integer) ((ArrayList) o).get(1)).intValue(), IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
                        }
                        model.getRequester().requestPopupGeneric(type, (retVal ? 0 : 1), null);
                    } else if (type == ContestConstants.CONTEST_REGISTRATION) {
                        MessageDialog md = new MessageDialog(ContestApplet.this, getCurrentFrame(), title, msg, true, true);
                        md.setButtonText((String) al.get(0));
                        md.setButton2Text((String) al.get(1));
                        boolean retVal = md.showDialog();
                        Long roundID = (Long) o;
                        if (retVal) {
                            model.getRequester().requestRegister(roundID.longValue(), new ArrayList());
                        }
                    } else if (type == ContestConstants.CONTEST_REGISTRATION_SURVEY) {
                        SurveyDialog sd = new SurveyDialog(ContestApplet.this, getMainFrame(), title, msg, (String) al.get(3), (ArrayList) al.get(2));
                        sd.setButtonText((String) al.get(0));
                        sd.setButton2Text((String) al.get(1));
                        ArrayList results = sd.showDialog();
                        boolean retVal = ((Boolean) results.remove(0)).booleanValue();
                        Long roundID = (Long) o;
                        if (retVal) {
                            model.getRequester().requestRegister(roundID.longValue(), results);
                        }
                    } else if (type == ContestConstants.SUBMIT_RESULTS) {
                        //load custom message dialog   
                        //SubmitResultsDialog md = new SubmitResultsDialog(getCurrentFrame(), title, msg);
                        //boolean retVal = md.showDialog();
                        Long roundID = (Long) al.get(0);
                        boolean bSystest = ((Boolean) al.get(1)).booleanValue();
                        
                        Common.showMessage(title, msg, getCurrentFrame());                     
                        
                        if(!(companyName.toLowerCase().startsWith(ContestConstants.COMPANY_SUN.toLowerCase())))
                        {
                            bSystest = false;
                        }
                        if(companyName.toLowerCase().equals("sunonsitefinals"))
                        {
                            bSystest = false;
                        }
                        
                        if(bSystest)
                        {
                            getInterFrame().showMessage("System Testing...", getCurrentFrame(), ContestConstants.SUBMIT_PROBLEM);
                            model.getRequester().requestSunAutoCompile(roundID.intValue());
                        }

                    }
                } else if (type2 == ContestConstants.LABEL) {
                    boolean retVal = Common.confirm(title,
                            msg,
                            getCurrentFrame());
                    if (retVal && type == ContestConstants.ROOM_MOVE) {
                        roomManager.loadRoom(((Integer) ((ArrayList) o).get(0)).intValue(), ((Integer) ((ArrayList) o).get(1)).intValue(), IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
                        return;
                    }
                    ArrayList al = new ArrayList();
                    if (o instanceof Integer)//for the multiple submit confirmation, there is a componentID
                        al.add(o);
                    model.getRequester().requestPopupGeneric(type, (retVal ? 0 : 1), al);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void popup(final int type, final String title, final String msg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        interFrame.hideMessage();

        // total hack......swing....
        //Thread t = new Thread(new Runnable() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (type == ContestConstants.TEXT_AREA) {
                    MessageDialog md = new MessageDialog(ContestApplet.this, getCurrentFrame(), title, msg);
                    md.show();
                } else if (type == ContestConstants.LABEL) {
                    if (msg.length() > 300) {//this is a hack so that stack overflow exceptions display propertly
                        MessageDialog md = new MessageDialog(ContestApplet.this, getCurrentFrame(), title, msg);
                        md.show();
                    } else {
                        Common.showMessage(title, msg, getCurrentFrame());
                    }
                } else if (type == ContestConstants.WRAPPING_TEXT_AREA) {
                    MessageDialog md = new MessageDialog(ContestApplet.this, getCurrentFrame(), title, msg, false, true);
                    md.show();
                }
            }
        });
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public String getCompanyName() {
        return companyName;
    }
    
    public String getSponsorName() {
        return sponsorName;
    }

    public boolean getPoweredByView() {
        return poweredByView;
    }

    public void showTeamManagerMember() {
        tmfm.setVisible(true);
    }

    public void showTeamManagerCaptain() {
        tmfc.setVisible(true);
    }

    public void vote(VoteResponse voteResponse) {
        VotingFrame.showFrame(this, voteResponse);
    }

    public void sendVoteBack(int roundId, String selectedName) {
        getRequester().requestVote(roundId, selectedName);
    }

    public void voteResults(VoteResultsResponse voteResultsResponse) {
        VotingResultsFrame.showFrame(this, voteResultsResponse);
    }

    public void disposeVotingFrame() {
        VotingFrame.disposeVotingFrame();
    }

    public void disposeTieBreakVotingFrame() {
        VotingFrame.disposeTieBreakVotingFrame();
    }

    public void sendRoundStatsRequest(int roundId, String coderName) {
        getRequester().requestRoundStats(roundId, coderName);
    }

    public void roundStatsResponse(RoundStatsResponse roundStatsResponse) {
        int roundId = roundStatsResponse.getRoundId();
        String handle = roundStatsResponse.getCoderName();
        Coder coderToFind = findCoder(roundId, handle);
        RoundStatsFrame.showFrame(this, roundStatsResponse, coderToFind, roundId);
    }

    public Coder findCoder(int roundId, String handle) {
        Coder coderToFind = null;
        RoomModel room = findRoom(roundId, handle);
        if (room != null) {
            coderToFind = room.getCoder(handle);
        }
        if (coderToFind == null) {
            throw new IllegalStateException();
        }
        return coderToFind;
    }

    public RoomModel findRoom(int roundId, String handle) {
        RoomModel result = null;
        Contestant contestant = getModel();
        RoundModel[] activeRounds = contestant.getActiveRounds();
        for (int i = 0; i < activeRounds.length; i++) {
            RoundModel activeRound = activeRounds[i];
            if (roundId == activeRound.getRoundID().intValue()) {
                RoomModel[] coderRooms = activeRound.getCoderRooms();
                for (int j = 0; j < coderRooms.length; j++) {
                    RoomModel coderRoom = coderRooms[j];
                    if (coderRoom.getCoder(handle) != null) {
                        result = coderRoom;
                        break;
                    }
                }
                break;
            }
        }
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    public void viewCodeRequest(String coderName, long componentId, int roundId) {
        RoomModel room = findRoom(roundId, coderName);
        long roomId = room.getRoomID().longValue();
        getRequester().requestChallengeComponent(componentId, false, roomId, coderName);
    }

    public void noBadgeId(NoBadgeIdResponse noBadgeIdResponse) {
        BadgeIdDialog.showDialog(this, noBadgeIdResponse);
    }

    public void loginWithBadgeId(String handle, String password, String badgeId) throws LoginException {
        model.loginWithBadgeId(handle, password, badgeId);
        if (model.isLoggedIn()) {
            getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                    IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
        }
    }

    public void wlMyTeamInfoResponse(WLMyTeamInfoResponse wlTeamInfoResponse) {
        WLMyTeamInfoFrame.showFrame(getCurrentFrame(), wlTeamInfoResponse);
    }

    public void wlTeamsInfoResponse(WLTeamsInfoResponse wlTeamsInfoResponse) {
        WLTeamsInfoFrame.showFrame(getCurrentFrame(), wlTeamsInfoResponse);
    }
    
    public void showImportantMessages() {
        for(int i = 0; i < messages.size(); i++) {
            ImportantMessageResponse resp = (ImportantMessageResponse)messages.get(i);
            
            ImportantMessageDialog md = new ImportantMessageDialog(this, resp.getText()); 
            md.showDialog();
            
            //need to send acknowledgement
            getRequester().requestReadMessage(resp.getId());
        }
        
        messages.clear();
    }
    
    public void importantMessage(ImportantMessageResponse response) {
        messages.add(response);
    }
    
    private ArrayList messages = new ArrayList();

    private StatementViewer problemStatementViewer;

    public void importantMessageSummry(GetImportantMessagesResponse response) {
        imf.update(response);
        imf.showFrame();
    }
    
    public void visitedPracticeList(CreateVisitedPracticeResponse response) {
        vpf.update(response);
        vpf.showFrame();
    }

    public void startPracticeSystest(PracticeSystemTestResponse response) {
        psrf.reset(response);
        psrf.showFrame();
    }

    public void practiceSystestResult(PracticeSystemTestResultResponse response) {
        psrf.update(response);
    }
    
    public void showSubmissionHistory(SubmissionHistoryResponse response) {
        interFrame.hideMessage();
        SubmissionHistoryFrame frame = new SubmissionHistoryFrame(this, response);
        frame.setVisible(true);
    }
    
    
    public void showLongTestResults(LongTestResultsResponse response) {
        interFrame.hideMessage();
        LongTestResultsFrame frame = new LongTestResultsFrame(this, response);
        frame.setVisible(true);
    }


    public void showProblemStatement(ProblemModel problem) {
        if (problemStatementViewer == null) {
            problemStatementViewer = new StatementViewer(this);
        }
        problemStatementViewer.setProblemStatement(problem);
        problemStatementViewer.setVisible(true);
    }

    public void showCoderHistory(CoderHistoryResponse response) {
        interFrame.hideMessage();
        CoderHistoryFrame frame = new CoderHistoryFrame(this, response);
        frame.showFrame();
    }
    public void loadPlugins() {
        // Startup any plugins if needed
        final PluginManager pluginManager = PluginManager.getInstance();
        EditorPlugin[] plugins = pluginManager.getEditorPlugins();

        // Loop though them all
        for(int x=plugins.length-1;x>=0;x--) {
            // If the plugins should be created eagerly...
            if(plugins[x].getEager()) {
                // Spawn a thread (for each) to create them
                final EditorPlugin temp = plugins[x];
                try {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                          try {
                              // Create the editor
                              DynamicEditor editor = pluginManager.getEditor(temp);
                              
                              // Set the model - deprecated
                              //editor.setContestApplet(ContestApplet.this);
                              
                              // Dispose of the editor (probably will cache it then)
                              pluginManager.disposeEditor(editor);
                          } catch (Exception e) {
                              System.err.println("Error with: " + temp.getName());
                              e.printStackTrace();
                          }
                        }
                    });
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
