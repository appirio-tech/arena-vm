/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.rooms;

/*
* CoderRoom.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.frames.ChallengeFrame;
import com.topcoder.client.contestApplet.uilogic.frames.CodingFrame;
import com.topcoder.client.contestApplet.uilogic.panels.ChatPanel;
import com.topcoder.client.contestApplet.uilogic.panels.CoderContestantTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.CompPanel;
import com.topcoder.client.contestApplet.uilogic.panels.CompetitionCompPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ProblemPanel;
import com.topcoder.client.contestApplet.uilogic.panels.RoomPanel;
import com.topcoder.client.contestApplet.uilogic.panels.TimeLine;
import com.topcoder.client.contestApplet.uilogic.panels.TimerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.UserTablePanel;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.CodingView;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.problem.DataType;

/**
 * <p>
 * Changes in version 1.1 (Fix issue 162)
 * <ol>
 *      <li>Update {@link #phaseEvent(int phase, RoundModel roundModel)} method.</li>
 * </ol>
 * </p>
 * @author  Alex Roman
 * @version 1.1
 */
public class CoderRoom extends RoomModule implements CodingView, PhaseListener, CoderRoomInterface {

    // challenge round info
    //protected JButton challengeButton = null;

    // session variables
    //private ArrayList problemInfo = null;
    private UIComponent problemSelector;
    //private boolean noCheck = false;

    // Chat Panel variables
    private RoomPanel panel = null;
    private CompPanel compPanel = null;
    private ChatPanel chatPanel = null;
    private UserTablePanel userPanel = null;
    private CoderContestantTablePanel contestantPanel = null;
    private CodingFrame codingRoom = null;
    private ChallengeFrame statusFrame = null;
    protected UIPage page;
    private TimeLine timeLine = null;
    private ProblemPanel problemPanel = null;
    private ArrayList tempArgs = null;
    private CompetitionCompPanel ccp = null;

    private boolean openingProblem = false;
    private boolean problemListStatus = true;
    private boolean enabled = true;
    private boolean timeLineWithChallengePhase;

    
    private static final String SELECTONE = "Select one";
    public static final String SPECTATOR_ROOM = "Spectator Room";
    private static final String DEFAULT_NAME = "Coder Room";
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
                updateCodingWindow(problemModel);
            }
        }

        public void updateProblemModelReadOnly(ProblemModel problemModel) {
        }
    };
    private CoderComponent currentCoderComponent;

    // ------------------------------------------------------------
    // Class constructor
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public CoderRoom(ContestApplet parent) {
        this(parent, "coder");
    }

    protected CoderRoom(ContestApplet parent, String pageName) {
        super(parent, ContestConstants.CODER_ROOM);
        create("", pageName);
    }

//    public CoderRoom(ContestApplet parent, int room) {
//        super(parent, room);
//    }

    private String getDefaultName() {
        return DEFAULT_NAME;
    }
    
    public void setArgs(ArrayList al) {
        tempArgs = al;
    }
    
    
    public void setConnectionStatus(boolean on ) {
        enabled = on;
        
        panel.setStatusLabel(on);
        //TODO: BUTTONS
        chatPanel.setPanelEnabled(on);
        userPanel.setPanelEnabled(on);
        contestantPanel.setPanelEnabled(on);
        problemPanel.setPanelEnabled(on);
        statusFrame.setPanelEnabled(on);
        
        if(!on)
            problemListStatus = ((Boolean) problemSelector.getProperty("Enabled")).booleanValue();
        
        if(problemListStatus == true && on == true) {
            problemSelector.setProperty("Enabled", Boolean.TRUE);
        }
        
        if(problemListStatus == true && on == false) {
            problemSelector.setProperty("Enabled", Boolean.FALSE);
        }
        
        if(codingRoom != null) {
            codingRoom.setPanelEnabled(on);
        }
    }

    /***
     *	Create the room.
     *
     *	@param title Name of room.  if null, use the default name;
     */
    private void create(String title, String pageName) {
        //globalize any needed variables
        if (title == null) title = getDefaultName();
        page = parentFrame.getCurrentUIManager().getUIPage(pageName);
        ccp = new CompetitionCompPanel(page);
        panel = new RoomPanel(title, parentFrame, createWorkPanel(), ccp, page);
        this.compPanel = panel.getCompPanel();
        panel.showTimer();
    }

    private UIComponent createWorkPanel() {
        TimeLine tp = newTimeLine();
        this.problemSelector = buildProblemSelector(new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                problemsListEvent();
            }
        });

        ProblemPanel pp = new ProblemPanel(parentFrame, page, this, problemSelector);
        
        // create all the panels/panes
        UserTablePanel sp = new UserTablePanel(parentFrame, page);
        ChatPanel ca = new ChatPanel(parentFrame, ContestConstants.GLOBAL_CHAT_SCOPE, page);
        CoderContestantTablePanel cp = new CoderContestantTablePanel(parentFrame, page);

        createNewCodingRoom();

        // globalize needed variables
        chatPanel = ca;
        userPanel = sp;
        contestantPanel = cp;
        timeLine = tp;
        problemPanel = pp;

        return page.getComponent("work_panel_base");
    }
    

    protected TimeLine newTimeLine() {
        timeLineWithChallengePhase = true;
        return new TimeLine(parentFrame, page);
    }
    
    protected void updateTimeLine() {
        boolean hasChallengePhase = roomModel.getRoundModel().getRoundProperties().hasChallengePhase();
        boolean isPracticeRound = roomModel.getRoundModel().getRoundType().isPracticeRound();
        if (!hasChallengePhase && timeLineWithChallengePhase) {
            timeLineWithChallengePhase = false;
            timeLine.updateIcons("phase_empty_nc_image", "phase_coding_nc_image", "phase_intermission_nc_image", "phase_challenge_image", "phase_system_nc_image", "phase_practice_image", parentFrame);
        } else if (hasChallengePhase && !timeLineWithChallengePhase) {
            timeLine.updateIcons("phase_empty_image", "phase_coding_image", "phase_intermission_image", "phase_challenge_image", "phase_system_image", "phase_practice_nc_image", parentFrame);
            timeLineWithChallengePhase = true;
        }
    }
    
    public RoomModel getRoomModel() {
        return roomModel;
    }


    // ------------------------------------------------------------
    // Return a reloaded room
    // ------------------------------------------------------------
    public JPanel reload() {
        panel.getWorkPanel().performAction("revalidate");
        panel.getWorkPanel().performAction("repaint");
        return ((JPanel) panel.getPanel().getEventSource());
    }

    // ------------------------------------------------------------
    // Clear out all room data
    // ------------------------------------------------------------
    public void clear() {
        super.clear();
        problemSelector.setProperty("Enabled", Boolean.TRUE);
        clearProblemSelector();
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
        closeCodingWindow();
        codingRoom.hide();
        if (statusFrame != null) statusFrame.hide();
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
    /**
     * Prepare the phase event.
     *
     * @param phase the phase type.
     * @param roundModel the round model.
     */
    public synchronized void phaseEvent(int phase, RoundModel roundModel) {
        setTimerPhase(phase, roundModel);
        boolean isPracticeRound = codingRoom.getRoomModel().isPracticeRoom();
        timeLine.setPhase(phase, isPracticeRound);
        problemPanel.setPhase(phase);
        switch (phase) {
        case ContestConstants.CODING_PHASE:
            // coding phase starting
            problemSelector.setProperty("Enabled", Boolean.TRUE);
            break;
        case ContestConstants.INTERMISSION_PHASE:
            // coding phase ending, intermission phase begins
            codingRoom.setButtons(false, false, false, false, false, true, false, false);
            codingRoom.enableText(false);
            // fix for Coding room document listener trigger bug.
            codingRoom.setButtons(false, false, false, false, false, true, false, false);
//          updateTimer(timerInfo);
            break;
        case ContestConstants.CHALLENGE_PHASE:
            // intermission phase ends, challenge phase begins
            //challengeFrame.showFrame(true);
            //challengeButton.setEnabled(true);
//          updateTimer(timerInfo);
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
            // challenge phase ends, system test phase begins
            //cd.stopRunning();
//          updateTimer(0, 0, 0);
            break;
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.VOTING_PHASE:
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
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
        case ContestConstants.VOTING_PHASE:
            title = "VOTING";
            mode = (TimerPanel.COUNTDOWN_MODE);
            break;
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            title = "TIE BREAKING VOTE";
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
        boolean roundModelInitialized = codingRoomTimerPanel.isRoundModelInitialized();
        if (roundModel != null && !roundModelInitialized) {
            codingRoomTimerPanel.setRoundModel(roundModel);
        }
        codingRoomTimerPanel.setMode(mode);
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


    protected void addViews() {
        if (!roomModel.hasRoundModel()) {
            throw new IllegalStateException("No round associated with: " + roomModel);
        }

        RoundModel roundModel = roomModel.getRoundModel();
        roundModel.addPhaseListener(this);

        if (!roundModel.hasProblems(roomModel.getDivisionID())) {
            throw new IllegalStateException("Missing problem labels for round: " + roundModel);
        }

        createProblems(roundModel.getAssignedComponents(roomModel.getDivisionID()));

        createNewStatusRoom();
        getTimerPanel().setRoundModel(roundModel);
        codingRoom.getTimerPanel().setRoundModel(roundModel);
        codingRoom.setRoomModel(roomModel);
        updateTimeLine();
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
            try
            {
                roomModel.removeChallengeView(statusFrame.getChallengePanel());
                roomModel.removeChatView(chatPanel);
            }
            catch (Exception e)
            {
                
            }
            if (roomModel.hasRoundModel()) {
                getTimerPanel().unsetRoundModel();
                roomModel.getRoundModel().removePhaseListener(this);
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
        //
        // Modified 3/13/2003 by schveiguy -- This gives the coder
        // instructions on how to use the new example testing dialog.  It also
        // passes the ProblemComponentModel to the argument dialog so it can
        // query the example cases.  If the problem component has no test
        // cases, then there is no point to pass it into the arg input, and no
        // reason to have instructions.  This is the case for older problems
        // in the practice rooms.
        //
        
        codingRoom.testComponentID = componentID;
        
        ProblemComponentModel probComponent = codingRoom.getComponentModel();
        if(probComponent.getTestCases().length == 0)
            Common.showArgInput(parentFrame, null, params, tempArgs, codingRoom, false, probComponent, codingRoom.getLanguage());
        else
            // ORIGINALARGS IS NOT WHAT WE WANT
            // IT JUST SHOWS CURRENT ARGS
            // FIND OUT WHERE IT COMES FROM
            Common.showArgInput(parentFrame, "Select an example from the drop-down list, or create your own custom arguments to test with.", params, tempArgs, codingRoom, false, probComponent, codingRoom.getLanguage());
        
    }
    

    // ------------------------------------------------------------
    // Event Handling
    // ------------------------------------------------------------

//    public void setCoderProblem(Problem problemInfo) {
//        parentFrame.getInterFrame().hideMessage();
//        updateCodingWindow(problemInfo);
//    }


    private void updateCodingWindow(ProblemModel problemModel) {
        problemSelector.setProperty("Enabled", Boolean.FALSE);
        codingRoom.clear();

        codingRoom.updateComponentInfo(problemModel.getComponents()[0]);
//        ProblemLabel pl = (ProblemLabel)(((ComboBoxItemWrapper)problemSelector.getSelectedItem()).getObject());
        codingRoom.setCR(this);
        codingRoom.showFrame(true);
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
            codingRoom.setCR(null);
            codingRoom = null;
        }
        codingRoom = newCodingRoom();
        codingRoom.setRoomModel(roomModel);
        //codingRoom.addWindowListener(new wl("windowClosing", "closeCodingWindow", this));
        codingRoom.getFrame().addEventListener("Window", new UIWindowAdapter() {
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

    protected CodingFrame newCodingRoom() {
        return new CodingFrame(parentFrame);
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

        Object item = getSelectedProblemComponent();
        if (item == null) return;

        String myHandle = parentFrame.getModel().getCurrentUser();
        if (!roomModel.isAssigned(myHandle)) {
            Common.showMessage("Not assigned", "You are not assigned to this room.", parentFrame.getCurrentFrame());
            return;
        }

        ProblemComponentModel component = (ProblemComponentModel) item;

        this.currentCoderComponent = roomModel.getCoder(myHandle).getComponent(component.getID());
        currentCoderComponent.addListener(myCoderComponentListener);   // do this one first.
        component.getProblem().addListener(myProblemModelListener);
        openingProblem = true;
        createNewCodingRoom();
        parentFrame.getRequester().requestOpenComponentForCoding(component.getID().longValue());
    }

    public void closeCodingWindow() {
        openingProblem = false;
        if(enabled) {
            problemSelector.setProperty("Enabled", Boolean.TRUE);
            problemSelectorReset();
        }
        else
            problemListStatus = true;

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

    protected UIComponent buildProblemSelector(UIActionListener listener) {
        UIComponent pl = page.getComponent("problem_list");
        pl.setProperty("Renderer", getProblemListRenderer(pl));
        pl.addEventListener("action", listener);
        return pl;
    }
    
    
    private ListCellRenderer getProblemListRenderer(UIComponent component) {
        final ListCellRenderer renderer = (ListCellRenderer) component.getProperty("Renderer");
        return new ListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                if (value instanceof ProblemComponentModel) {
                    RoundModel roundModel = roomModel.getRoundModel();
                    ComponentNameBuilder nameBuilder = roundModel.getRoundType().getComponentNameBuilder();
                    ProblemComponentModel component = (ProblemComponentModel) value;
                    value = nameBuilder.shortNameForComponent(component.getClassName(), component.getPoints().doubleValue(), roundModel.getRoundProperties());
                }
                return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        };
    }

    protected void problemSelectorReset() {
        UIComponent problemList = (UIComponent) problemSelector;
        problemList.setProperty("SelectedItem", SELECTONE);
    }
    
    protected void clearProblemSelector() {
        UIComponent problemList = (UIComponent) problemSelector;
        problemList.performAction("removeAllItems");
    }

    
    void createProblems(ProblemComponentModel[] components) {
        updateProblemSelector(components);
        
    }
    
    protected void updateProblemSelector(ProblemComponentModel[] components) {
        UIComponent problemList = (UIComponent) problemSelector;
        // Clear the current list
        problemList.performAction("removeAllItems");

        // Add the select one
        problemList.performAction("addItem", new Object[] {SELECTONE});
        // Add each of the point values to the problem list
        for (int x = 0; x < components.length; x++) {
            problemList.performAction("addItem", new Object[] {components[x]});
        }
    }
    
    protected Object getSelectedProblemComponent() {
        UIComponent problemList = (UIComponent) problemSelector;
        int index = ((Integer) problemList.getProperty("SelectedIndex")).intValue();
        if (index < 0) return null;

        tempArgs = null;

        // Did they select the "Select one" choice - if so - return;
        Object item = problemList.performAction("getItemAt", new Object[] {new Integer(index)});
        
        if (item.equals(SELECTONE)) return null;
        
        return item;
    }

    public void challengeButtonEvent(ActionEvent e) {
        RoundModel roundModel = roomModel.getRoundModel();
        if (roundModel.canDisplaySummary()) {
            statusFrame.showFrame(true);
        } else {
            parentFrame.popup(ContestConstants.LABEL, "Error", "Summary not available until the end of the round.");
        }
    }

    private TimerPanel getTimerPanel() {
        return panel.getTimerPanel();
    }

    public void updateSystestProgress(int testsDone, int totalSystests, RoundModel round) {
        // TODO - ugly
        if (!ContestConstants.isPracticeRoomType(roomModel.getType().intValue())) {
            getTimerPanel().updateSystestProgress(testsDone, totalSystests);
            statusFrame.getTimerPanel().updateSystestProgress(testsDone, totalSystests);
            codingRoom.getTimerPanel().updateSystestProgress(testsDone, totalSystests);
        }
    }

    public void enableRound(RoundModel round) {
    }

//    public void setComponentSource(Integer lanuageID, String code){
//        codingRoom.updateComponentSource(code, lanuageID);
//    }

    protected UIComponent getProblemSelector() {
        return problemSelector;
    }
}
