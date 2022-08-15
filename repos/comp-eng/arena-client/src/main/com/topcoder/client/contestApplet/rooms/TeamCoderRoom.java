package com.topcoder.client.contestApplet.rooms;

//TODO
//uncomment line 620, 330, 331

/*
 * TeamCoderRoom.java
 *
 * Created on November 11, 2002, 12:11 PM
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.frames.ChallengeFrame;
import com.topcoder.client.contestApplet.frames.CodingFrame;
import com.topcoder.client.contestApplet.frames.TeamProblemFrame;
import com.topcoder.client.contestApplet.panels.ChatTabbedPane;
import com.topcoder.client.contestApplet.panels.TeamProblemPanel;
import com.topcoder.client.contestApplet.panels.TimeLine;
import com.topcoder.client.contestApplet.panels.room.RoomPanel;
import com.topcoder.client.contestApplet.panels.room.TimerPanel;
import com.topcoder.client.contestApplet.panels.room.WorkPanel;
import com.topcoder.client.contestApplet.panels.room.comp.CompPanel;
import com.topcoder.client.contestApplet.panels.room.comp.CompetitionCompPanel;
import com.topcoder.client.contestApplet.panels.table.TeamContestantTablePanel;
import com.topcoder.client.contestApplet.panels.table.UserTablePanel;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.CodingView;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoundProblemsListener;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.problem.DataType;

/**
 *
 * @author Tim Bulat
 * @version
 */
public final class TeamCoderRoom extends RoomModule implements CodingView, PhaseListener, CoderRoomInterface, RoundProblemsListener {

    // challenge round info
    protected JButton challengeButton = null;

    // session variables
    //private ArrayList problemInfo = null;
    private JComboBox componentList = new JComboBox();
    private JComboBox problemList = new JComboBox();

    // Chat Panel variables
    private RoomPanel panel = null;
    private CompPanel compPanel = null;
    private ChatTabbedPane chatPanel = null;
    private UserTablePanel userPanel = null;
    private TeamContestantTablePanel contestantPanel = null;
    private CodingFrame codingRoom = null;
    private TeamProblemFrame teamProblemRoom = null;
    private ChallengeFrame statusFrame = null;
    private TimeLine timeLine = null;
    private TeamProblemPanel problemPanel = null;
    private ArrayList tempArgs = null;
    private int buttonStatus = 0;
    private CompetitionCompPanel ccp = null;

    private boolean openingProblem = false;
    private CoderComponent currentCoderComponent;

    private static final String SELECTONEPROBLEM = "Problems";
    private static final String SELECTONECOMPONENT = "Components";
    public static final String SPECTATOR_ROOM = "Spectator Room";
    private static final String DEFAULT_NAME = "Team Coder Room";

    private CoderComponent.Listener myCoderComponentListener = new CoderComponent.Listener() {
        public void coderComponentEvent(CoderComponent coderComponent) {
            if (coderComponent.hasSourceCode()) {
                codingRoom.updateComponentSource(coderComponent.getSourceCode(), coderComponent.getSourceCodeLanguage());
            }
            coderComponent.removeListener(this);  // No more updates.
        }

    };

    private ProblemModel.Listener myProblemModelListener = new ProblemModel.Listener() {
        public void updateProblemModel(ProblemModel problemModel) {
            if (openingProblem) {
                openingProblem = false;
                parentFrame.getInterFrame().hideMessage();

                //find the right component model
                for (int i = 0; i < problemModel.getComponents().length; i++) {
                    if (problemModel.getComponents()[i].getID().equals(currentCoderComponent.getComponent().getID())) {
                        updateCodingWindow(problemModel.getComponents()[i]);
                        break;
                    }
                }
            }
        }

        public void updateProblemModelReadOnly(ProblemModel problemModel) {
            if (openingProblem) {
                openingProblem = false;
                parentFrame.getInterFrame().hideMessage();
                updateTeamProblemWindow(problemModel);
            }
        }
    };

    // ------------------------------------------------------------
    // Class constructor
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public TeamCoderRoom(ContestApplet parent) {
        super(parent, ContestConstants.TEAM_CODER_ROOM);
        create("", true);
    }

    private String getDefaultName() {
        return DEFAULT_NAME;
    }
    
    public void setConnectionStatus(boolean on ) {
        panel.setStatusLabel(on);
        //TODO: BUTTONS
    }

    /***
     *	Create the room.
     *
     *	@param title Name of room.  if null, use the default name;
     */
    private void create(String title, boolean showProblem) {
        //globalize any needed variables
        if (title == null) title = getDefaultName();
        panel = new RoomPanel(title, parentFrame, createWorkPanel(showProblem), new CompetitionCompPanel());
        this.compPanel = panel.getCompPanel();
        ccp = (CompetitionCompPanel) compPanel.getContestPanel();
        panel.showTimer();
    }
    
    public void setArgs(ArrayList al) {
        tempArgs = al;
    }

    private JPanel createWorkPanel(boolean showProblem) {
        JPanel wpb = new JPanel(new GridBagLayout());
        WorkPanel wp = new WorkPanel(parentFrame);
        TimeLine tp = new TimeLine(parentFrame);
        TeamProblemPanel pp = new TeamProblemPanel(parentFrame, showProblem);
        this.problemList = pp.getProblemList();
        this.componentList = pp.getComponentList();

        problemList.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                if (value instanceof ProblemModel) {
                    ProblemModel problem = (ProblemModel) value;
                    value = problem.getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        componentList.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                if (value instanceof ProblemComponentModel) {
                    ProblemComponentModel problem = (ProblemComponentModel) value;
                    value = problem.getClassName() + " - " + Common.formatNoFractions(problem.getPoints().doubleValue());
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        if (!showProblem) {
            this.problemList.setVisible(false);
            this.componentList.setVisible(false);
        }

        GridBagConstraints gbc = Common.getDefaultConstraints();
        wpb.setBackground(Common.WPB_COLOR);

        // create all the panels/panes
        UserTablePanel sp = new UserTablePanel(parentFrame);
        ChatTabbedPane ca = new ChatTabbedPane(parentFrame);
        ca.addChat("Room Chat", ContestConstants.GLOBAL_CHAT_SCOPE);
        ca.addChat("Team Chat", ContestConstants.TEAM_CHAT_SCOPE);
        TeamContestantTablePanel cp = new TeamContestantTablePanel(parentFrame);

        // register all events
        problemList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                problemsListEvent();
            }
        });

        componentList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                componentsListEvent();
            }
        });

        createNewCodingRoom();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 15, 5, 10);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 10, 2, 15);
        Common.insertInPanel(tp, wp, gbc, 1, 0, 1, 1, 1.0, 0.0);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 10, 5, 15);
        Common.insertInPanel(pp, wp, gbc, 1, 1, 1, 1, 1.0, 0.0);

        gbc.insets = new Insets(5, 10, 15, 15);
        Common.insertInPanel(ca, wp, gbc, 1, 2, 1, 1, 1.0, 1.0);

        JPanel p = new JPanel(new GridBagLayout());
        p.setMinimumSize(new Dimension(170, 0));
        p.setPreferredSize(new Dimension(170, 0));
        gbc.insets = new Insets(5, 15, 5, 10);
        Common.insertInPanel(cp, p, gbc, 0, 0, 1, 1, 0.1, 1.0);
        gbc.insets = new Insets(5, 15, 15, 10);
        Common.insertInPanel(sp, p, gbc, 0, 1, 1, 1, 0.1, 0.8);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.VERTICAL;
        Common.insertInPanel(p, wp, gbc, 0, 0, 1, 3, 0.0, 0.1);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(wp, wpb, gbc, 1, 1, 1, 1, 1.0, 1.0);

        // globalize needed variables
        chatPanel = ca;
        userPanel = sp;
        contestantPanel = cp;
        timeLine = tp;
        problemPanel = pp;

        return (wpb);
    }

    // ------------------------------------------------------------
    // Return a reloaded room
    // ------------------------------------------------------------
    public JPanel reload() {
        panel.getWorkPanel().revalidate();
        panel.getWorkPanel().repaint();
        return (panel);
    }

    // ------------------------------------------------------------
    // Clear out all room data
    // ------------------------------------------------------------
    public void clear() {
        super.clear();
        problemList.setEnabled(true);
        problemList.removeAllItems();
        componentList.setEnabled(true);
        componentList.removeAllItems();
        userPanel.clear();
        contestantPanel.clear();
        chatPanel.clear();
    }

    public void enter() {
        chatPanel.enter();
    }

    // ------------------------------------------------------------
    // Take care of any business before leaving
    // ------------------------------------------------------------
    public boolean leave() {
        if (codingRoom != null) {
            closeCodingWindow();
            codingRoom.hide();
        }
        if (teamProblemRoom != null) {
            closeTeamProblemWindow();
            teamProblemRoom.hide();
        }
        if (statusFrame != null) {
            statusFrame.hide();
        }
        chatPanel.leave();
        unsetModel();
        return (true);
    }


    public void resetFocus() {
        chatPanel.leave();
        chatPanel.enter();
    }

    public CodingFrame getCodingFrame() {
        return (codingRoom);
    }

    public synchronized void phaseEvent(int phase, RoundModel roundModel) {
        setTimerPhase(phase, roundModel);
        timeLine.setPhase(phase);
        problemPanel.setPhase(phase);
        switch (phase) {
        case ContestConstants.CODING_PHASE:
            // coding phase starting
            problemList.setEnabled(true);
            componentList.setEnabled(true);
            break;
        case ContestConstants.INTERMISSION_PHASE:
            // coding phase ending, intermission phase begins
            codingRoom.setButtons(false, false, false, false, false, true, false, false);
            codingRoom.enableText(false);
            // fix for Coding room document listener trigger bug.
            codingRoom.setButtons(false, false, false, false, false, true, false, false);
            break;
        case ContestConstants.CHALLENGE_PHASE:
            // intermission phase ends, challenge phase begins
            //challengeFrame.showFrame(true);
            //challengeButton.setEnabled(true);
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
            // challenge phase ends, system test phase begins
            //cd.stopRunning();
//          updateTimer(0, 0, 0);
            break;
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
            /* Do nothing */
            break;
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.REGISTRATION_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
        case ContestConstants.MODERATED_CHATTING_PHASE:
//          updateTimer(timerInfo);
            break;
        default:
            throw new IllegalArgumentException("Unknown phase (" + phase + ").");
        }
    }

    private void setTimerPhase(int phase, RoundModel roundModel) {
        int mode = -1;
        String title = null;
        switch (phase) {
        case ContestConstants.INACTIVE_PHASE:
            title = "TOPCODER TIME";
            mode = (TimerPanel.CLOCK_MODE);
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
            title = "PENDING SYSTESTS";
            mode = (TimerPanel.CLOCK_MODE);
            break;
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            title = "CONTEST COMPLETE";
            mode = (TimerPanel.CLOCK_MODE);
            break;
        case ContestConstants.REGISTRATION_PHASE:
            title = "REGISTRATION";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.ALMOST_CONTEST_PHASE:
            title = "STARTS IN";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.STARTS_IN_PHASE:
            title = "STARTS IN";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.CODING_PHASE:
            title = "CODING";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.INTERMISSION_PHASE:
            title = "INTERMISSION";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.CHALLENGE_PHASE:
            title = "CHALLENGE";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.SYSTEM_TESTING_PHASE:
            title = "SYSTEM TESTING";
            mode = (TimerPanel.SYSTEST_MODE);
            break;
        default:
            throw new IllegalArgumentException("Bad phase type: " + phase);
        }
        getTimerPanel().setTitle(title);
        getTimerPanel().setMode(mode);
        statusFrame.getTimerPanel().setTitle(title);
        statusFrame.getTimerPanel().setMode(mode);
        TimerPanel codingRoomTimerPanel = codingRoom.getTimerPanel();
        codingRoomTimerPanel.setTitle(title);
        TimerPanel teamProblemRoomTimerPanel = null;
        if (teamProblemRoom != null) {
            teamProblemRoomTimerPanel = teamProblemRoom.getTimerPanel();
            teamProblemRoomTimerPanel.setTitle(title);
        }
        boolean roundModelInitialized = codingRoomTimerPanel.isRoundModelInitialized();
        if (roundModel != null && !roundModelInitialized) {
            codingRoomTimerPanel.setRoundModel(roundModel);
            if (teamProblemRoomTimerPanel != null) {
                teamProblemRoomTimerPanel.setRoundModel(roundModel);
            }
        }
        codingRoomTimerPanel.setMode(mode);
    }

    public void showStatusWindow() {
        statusFrame.showFrame(true);
    }

    public void timeOutEvent(int requestType) {
        switch (requestType) {
        case ContestConstants.SAVE:
            codingRoom.setButtons(true, true, true, true, true, false, true, false);
            break;
        case ContestConstants.COMPILE:
            codingRoom.setButtons(true, true, true, true, true, false, true, false);
            break;
        case ContestConstants.TEST:
            codingRoom.setButtons(true, true, true, false, false, true, true, true);
            break;
        }
    }

    public void roundProblemsEvent() {
        RoundModel roundModel = roomModel.getRoundModel();
        createProblems(roundModel.getAssignedComponents(roomModel.getDivisionID()),
                roundModel.getProblems(roomModel.getDivisionID()));
    }

    private void createProblems(ProblemComponentModel[] assignedComponents, ProblemModel[] problems) {

        // Clear the current list
        problemList.removeAllItems();
        componentList.removeAllItems();

        // Add the select one
        problemList.addItem(SELECTONEPROBLEM);
        componentList.addItem(SELECTONECOMPONENT);

        // Add each of the names to the problem list
        for (int i = 0; i < problems.length; i++) {
            problemList.addItem(problems[i]);
        }

        // Add name and point value of each component to the component list
        for (int i = 0; i < assignedComponents.length; i++) {
            componentList.addItem(assignedComponents[i]);
        }
    }


    protected void addViews() {
        if (!roomModel.hasRoundModel()) {
            throw new IllegalStateException("No round associated with: " + roomModel);
        }

        RoundModel roundModel = roomModel.getRoundModel();
        roundModel.addPhaseListener(this);
        roundModel.addRoundProblemsListener(this);

        if (!roundModel.hasProblems(roomModel.getDivisionID())) {
            throw new IllegalStateException("Missing problem labels for round: " + roundModel);
        }

        createProblems(roundModel.getAssignedComponents(roomModel.getDivisionID()),
                roundModel.getProblems(roomModel.getDivisionID()));

        createNewStatusRoom();
        getTimerPanel().setRoundModel(roundModel);
        codingRoom.getTimerPanel().setRoundModel(roundModel);
        statusFrame.getTimerPanel().setRoundModel(roundModel);

        phaseEvent(roundModel.getPhase().intValue(), null);

        roomModel.addChatView(chatPanel);
        roomModel.addChallengeView(statusFrame.getChallengePanel());
        if (statusFrame.getAssignmentPanel() != null) {
            roomModel.addChallengeView(statusFrame.getAssignmentPanel());
            roomModel.addAssignmentView(statusFrame.getAssignmentPanel());
        }
        roomModel.addUserListView(userPanel);
        roomModel.addUserListView(chatPanel);
        roomModel.addCodingView(this);

        Coder[] coders = roomModel.getCoders();
        UserListItem[] coderUserListItems = new UserListItem[coders.length];
        for (int i = 0; i < coders.length; i++) {
            Coder coder = coders[i];
            coderUserListItems[i] = new UserListItem(coder.getHandle(), coder.getRating().intValue(), coder.getUserType());
        }
        contestantPanel.updateUserList(coderUserListItems);
        ccp.updateContestInfo(roomModel.getStatus());
        compPanel.setContestName(roomModel.getName());
    }

    protected void clearViews() {
        ccp.updateContestInfo("");
        compPanel.setContestName("");
        contestantPanel.clear();
        if (roomModel != null) {
            roomModel.removeCodingView(this);
            roomModel.removeUserListView(chatPanel);
            roomModel.removeUserListView(userPanel);
            if (statusFrame != null && statusFrame.getAssignmentPanel() != null) {
                roomModel.removeChallengeView(statusFrame.getAssignmentPanel());
                roomModel.removeAssignmentView(statusFrame.getAssignmentPanel());
            }
            roomModel.removeChallengeView(statusFrame.getChallengePanel());
            roomModel.removeChatView(chatPanel);
            if (roomModel.hasRoundModel()) {
                getTimerPanel().unsetRoundModel();
                roomModel.getRoundModel().removePhaseListener(this);
                roomModel.getRoundModel().removeRoundProblemsListener(this);
            } else {
                throw new IllegalStateException("No round associated with: " + roomModel);
            }
        }
    }

    public void updateCompileError(ArrayList compile) {
        // POPS - 11/15/01 - Moved logic into the codingFrame
        codingRoom.updateCompile(((Boolean) compile.get(0)).booleanValue(), (String) compile.get(1));
    }


    public void setTestInfo(DataType[] params, int componentID) {
        ArrayList info = null;

        codingRoom.testComponentID = componentID;
        codingRoom.setCR(this);
        
        ProblemComponentModel probComponent = codingRoom.getComponentModel();
        /*if(probComponent.getTestCases().length == 0)
            Common.showArgInput(null, params, tempArgs, codingRoom, false, probComponent);
        else
            Common.showArgInput("Select an example from the drop-down list, or create your own custom arguments to test with.", params, tempArgs, codingRoom, false, probComponent);
        */
        /*
        if (tempArgs == null) {
            Common.showArgInput(params, codingRoom, false);
        } else {
            Common.showArgInput(params, tempArgs, codingRoom, false);
        }*/
    }

    private void test(ArrayList info, int componentID) {
        parentFrame.getInterFrame().showMessage("Testing...", codingRoom, ContestConstants.TEST);
        parentFrame.getRequester().requestTest((ArrayList) info.get(1), componentID);
    }


    // ------------------------------------------------------------
    // Event Handling
    // ------------------------------------------------------------

//    public void setCoderProblem(Problem problemInfo) {
//        parentFrame.getInterFrame().hideMessage();
//        updateCodingWindow(problemInfo);
//    }


    private void updateCodingWindow(ProblemComponentModel componentModel) {
        createNewCodingRoom();
        componentList.setEnabled(false);
        codingRoom.clear();

        codingRoom.updateComponentInfo(componentModel);
        codingRoom.showFrame(true);
    }

    public void updateTeamProblemWindow(ProblemModel problemModel) {
        createNewTeamProblemRoom();
        problemList.setEnabled(false);
        teamProblemRoom.clear();
        teamProblemRoom.updateProblemInfo(problemModel);
        teamProblemRoom.showFrame(true);
    }


    /**
     * Temporary fix to the focus problem. If focus is lost in the window, normally
     * the user would have to log out the browser and reload the applet. Now the
     * user just has to reload the problem, and a new coding window will get created.
     */
    private void createNewCodingRoom() {
        if (codingRoom != null) {
            codingRoom.hide();
            codingRoom.dispose();
            codingRoom = null;
        }
        codingRoom = new CodingFrame(parentFrame);
        codingRoom.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeCodingWindow();
            }
        });
        codingRoom.create();
        if (roomModel != null) {
            codingRoom.getTimerPanel().setRoundModel(roomModel.getRoundModel());
            setTimerPhase(roomModel.getRoundModel().getPhase().intValue(), null);
        }
    }

    /**
     * Temporary fix to the focus problem. If focus is lost in the window, normally
     * the user would have to log out the browser and reload the applet. Now the
     * user just has to reload status room to get a new one.
     */
    public void createNewTeamProblemRoom() {
        if (teamProblemRoom != null) {
            teamProblemRoom.hide();
            teamProblemRoom.dispose();
            teamProblemRoom = null;
        }

        teamProblemRoom = new TeamProblemFrame(parentFrame);
        teamProblemRoom.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeTeamProblemWindow();
            }
        });
        teamProblemRoom.create();
        if (roomModel != null) {
            teamProblemRoom.getTimerPanel().setRoundModel(roomModel.getRoundModel());
            setTimerPhase(roomModel.getRoundModel().getPhase().intValue(), null);
        }
    }

    /**
     * Temporary fix to the focus problem. If focus is lost in the window, normally
     * the user would have to log out the browser and reload the applet. Now the
     * user just has to reload status room to get a new one.
     */
    private void createNewStatusRoom() {
        if (statusFrame != null) {
            statusFrame.hide();
            statusFrame.dispose();
            statusFrame = null;
        }
        statusFrame = new ChallengeFrame(parentFrame, roomModel);
        //challengeFrame.addFocusListener(new fl());
    }


    // ------------------------------------------------------------
    // Event Handling
    // ------------------------------------------------------------

    private void problemsListEvent() {

        int index = problemList.getSelectedIndex();
        if (index < 0) return;

        tempArgs = null;

        Object item = problemList.getItemAt(index);
        // Did they select the "Select one" choice - if so - return;
        if (item.equals(SELECTONEPROBLEM)) {
            return;
        }

        String myHandle = parentFrame.getModel().getCurrentUser();
        if (!roomModel.isAssigned(myHandle)) {
            Common.showMessage("Not assigned", "You are not assigned to this room.", parentFrame.getCurrentFrame());
            return;
        }

        openingProblem = true;
        ProblemModel problem = (ProblemModel) item;
        problem.addListener(myProblemModelListener);
        parentFrame.getRequester().requestOpenProblemForReading(roomModel.getRoundModel().getRoundID().longValue(), problem.getProblemID().longValue());
    }

    private void componentsListEvent() {
        int index = componentList.getSelectedIndex();
        if (index < 0) return;

        tempArgs = null;

        Object item = componentList.getItemAt(index);
        // Did they select the "Select one" choice - if so - return;
        if (item.equals(SELECTONECOMPONENT)) {
            return;
        }

        String myHandle = parentFrame.getModel().getCurrentUser();
        if (!roomModel.isAssigned(myHandle)) {
            Common.showMessage("Not assigned", "You are not assigned to this room.", parentFrame.getCurrentFrame());
            return;
        }

        ProblemComponentModel component = (ProblemComponentModel) item;

        myHandle = parentFrame.getModel().getCurrentTeam();
        this.currentCoderComponent = roomModel.getCoder(myHandle).getComponent(component.getID());
        currentCoderComponent.addListener(myCoderComponentListener);   // do this one first.
        component.getProblem().addListener(myProblemModelListener);
        openingProblem = true;
        parentFrame.getRequester().requestOpenComponentForCoding(component.getID().longValue());
    }

    public void closeCodingWindow() {
        openingProblem = false;
        componentList.setEnabled(true);

        if (!codingRoom.isSaved()) {
            codingRoom.setButtons(false, false, false, false, false, false, false, false);
        }

        if (currentCoderComponent != null) {
            currentCoderComponent.removeListener(myCoderComponentListener);  // Just in case
            currentCoderComponent.getComponent().getProblem().removeListener(myProblemModelListener);
            currentCoderComponent = null;
        }

        resetFocus();
    }

    public boolean isCodingWindowOpened() {
        return currentCoderComponent != null;
    }

    private void closeTeamProblemWindow() {
        openingProblem = false;
        problemList.setEnabled(true);
        resetFocus();
    }

    public void challengeButtonEvent(ActionEvent e) {
        statusFrame.showFrame(true);
    }

    private TimerPanel getTimerPanel() {
        return panel.getTimerPanel();
    }

    public void updateSystestProgress(int testsDone, int totalSystests, RoundModel roundMode) {
        // TODO - ugly
        if (!ContestConstants.isPracticeRoomType(roomModel.getType().intValue())) {
            getTimerPanel().updateSystestProgress(testsDone, totalSystests);
        }
    }

    public void enableRound(RoundModel round) {
    }

//    public void setComponentSource(Integer lanuageID, String code){
//        codingRoom.updateComponentSource(code, lanuageID);
//    }
}
