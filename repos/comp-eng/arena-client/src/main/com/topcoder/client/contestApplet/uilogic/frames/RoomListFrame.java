package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.uilogic.panels.CodingTimerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.uilogic.panels.RoomInfoTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.TimerPanel;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoundView;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundProperties;

public class RoomListFrame implements FrameLogic, PhaseListener, RoundView {
    private UIComponent frame;
    private UIPage page;
    private ContestApplet parentFrame = null;
    private RoomInfoTablePanel roomInfoPanel = null;
    private TimerPanel timerPanel = null;

    private UIComponent rounds;
    private static RoomListFrame singleton;
    private ContestSponsorPanel sponsorPanel;
    
    private Long selectedRoundID = null;
    private boolean enabled = true;
    private Set roundsToShow = Collections.synchronizedSet(new LinkedHashSet());
    
    public Long getSelectedRoundID() {
        return selectedRoundID;
    }
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            rounds.setProperty("Enabled", Boolean.TRUE);
        } else {
            rounds.setProperty("Enabled", Boolean.FALSE);
        }
        if(roomInfoPanel != null)
            roomInfoPanel.setEnabled(on);
    }

    public UIComponent getFrame() {
        return frame;
    }

    public RoomListFrame(ContestApplet parentFrame) {
        this.parentFrame = parentFrame;
        page = parentFrame.getCurrentUIManager().getUIPage("room_list_frame", true);
        frame = page.getComponent("root_frame");

        create();

        // set the placement
        Common.setLocationRelativeTo(parentFrame.getMainFrame(), (Component) frame.getEventSource());
    }

    public static RoomListFrame getInstance(ContestApplet ca) {
        if (singleton == null)
            singleton = new RoomListFrame(ca);
        return singleton;
    }
    
    private void create() {
        rounds = page.getComponent("rounds");

        // create all the panels/panes
        RoomInfoTablePanel ritp = new RoomInfoTablePanel(parentFrame, page);
        CodingTimerPanel ctp = new CodingTimerPanel(parentFrame, page);
        sponsorPanel = new ContestSponsorPanel(page.getComponent("sponsor_logo"), CommonData.getSponsorScoreBoardImageAddr(parentFrame.getSponsorName(), null));

        // set misc properties
        final ListCellRenderer roundsRenderer = (ListCellRenderer) rounds.getProperty("Renderer");
        rounds.setProperty("Renderer", new ListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof RoundModel) {
                    RoundModel roundModel = (RoundModel) value;
                    value = roundModel.getDisplayName();
                }
                return roundsRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });

        rounds.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                RoundModel roundModel = (RoundModel) rounds.getProperty("SelectedItem");
                if (roundModel != null) {
                    roomInfoPanel.setRound(roundModel);
                    selectedRoundID = roundModel.getRoundID();
                    setRoomPhase(roundModel);
                } else {
                    selectedRoundID = null;
                }
            }
        });

        frame.addEventListener("window", new UIWindowAdapter() {
            public void windowActivated(WindowEvent e) {
                show();
            }
        });
        
        parentFrame.getModel().getRoundViewManager().addListener(this);
        bindToActiveRounds();
        
        // globalize variables
        this.roomInfoPanel = ritp;
        this.timerPanel = ctp;
        frame.performAction("pack");
    }
    
    private void setRoomPhase(RoundModel roundModel) {
        updateSponsors(roundModel);
        setTimerPanelRoundModel(roundModel);
        phaseEvent(roundModel.getPhase().intValue(), roundModel);
    }

    private void updateSponsors(RoundModel roundModel) {
        sponsorPanel.updateURL(CommonData.getSponsorScoreBoardImageAddr(parentFrame.getSponsorName(), roundModel));
        
    }

    public void reset() {
        roomInfoPanel.clear();
    }

    public void hide() {
        frame.performAction("hide");
        reset();
        if(enabled) {
            parentFrame.getRequester().requestCloseLeaderBoard();
        }
    }
    
    public void show() {
        reloadRounds();
        frame.performAction("show");
    }
    
    public void reloadRounds() {
        Long savedSelectedRoundID;
        if (selectedRoundID == null) {
            savedSelectedRoundID = null;
        } else {
            savedSelectedRoundID = new Long(selectedRoundID.longValue());
        }
        
        reset();
        rounds.performAction("removeAllItems");
        if (roundsToShow.size() > 0) {
            boolean set = false;
            RoundModel[] roundsTemp = (RoundModel[]) roundsToShow.toArray(new RoundModel[roundsToShow.size()]);
            RoundModel round = roundsTemp[0];
            for (int i = 0; i < roundsTemp.length; i++) {
                round = roundsTemp[i];
                rounds.performAction("addItem", new Object[] {round});
                if (round.getRoundID().equals(savedSelectedRoundID)) {
                    roomInfoPanel.setRound(round);
                    rounds.setProperty("SelectedIndex", new Integer(i));
                    setRoomPhase(round);
                    set = true;
                }
            }
            if (!set) {
                round = roundsTemp[0];
                roomInfoPanel.setRound(round);
                rounds.setProperty("SelectedIndex", new Integer(0));
                setRoomPhase(round);
            }
        }
        //pack();
    }
    
    private TimerPanel getTimerPanel() {
        return timerPanel;
    }
    
    public void setTimerPanelRoundModel(RoundModel roundModel) {
        getTimerPanel().setRoundModel(roundModel);
    }
    
    public synchronized void phaseEvent(int phase, RoundModel roundModel) {
        if (roundModel != null) {
            if (roundModel.canDisplaySummary() != roundsToShow.contains(roundModel)) {
                if (roundModel.canDisplaySummary()) {
                    roundsToShow.add(roundModel);
                } else {
                    roundsToShow.remove(roundModel);
                }
                reloadRounds();
            } 
        }
        if (roundModel == null || roundModel.getRoundID().equals(selectedRoundID)) {
            setTimerPhase(phase, roundModel);
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
    }
    
    public void updateSystestProgress(int testsDone, int totalSystests, RoundModel roundModel) {
        if (roundModel.getRoundID().equals(selectedRoundID)) {
            getTimerPanel().updateSystestProgress(testsDone, totalSystests);
        }
    }
    
    public void enableRound(RoundModel round) {
    }

    public void clearRoundList() {
    }

    public void updateActiveRoundList(Contestant model) {
        RoundModel[] rounds = model.getActiveRounds();
        if (rounds.length == 0) {
            phaseEvent(ContestConstants.INACTIVE_PHASE, null);
        } else {
            bindToActiveRounds();
            reloadRounds();
        }
    }
    
    /**
     * Listens for phase changes in every active round that may be shown in the leaderboard.
     * Collects rounds that can be displayed in the roundsToShow list
     */
    private void bindToActiveRounds() {
        RoundModel[] activeRounds = parentFrame.getModel().getActiveRounds();
        if (activeRounds != null && activeRounds.length > 0) {
            LinkedHashSet bindedRoundList = new LinkedHashSet(10);
            RoundModel roundToSet = null;
            for (int i=0; i<activeRounds.length; i++) {
                RoundModel round = activeRounds[i];
                RoundProperties roundProperties = round.getRoundProperties();
                if (roundProperties.usesScore() && roundProperties.getShowScoresOfOtherCoders().booleanValue()) {
                    if (!round.containsPhaseListener(this)) {
                        round.addPhaseListener(this);
                    }
                    if (round.canDisplaySummary()) {
                        bindedRoundList.add(round);
                        if (round.getRoundID().equals(selectedRoundID)) {
                            roundToSet = round;
                        }
                    }
                }
            }
            roundsToShow = Collections.synchronizedSet(bindedRoundList);
            if (roundsToShow.size() > 0) {
                if (roundToSet == null) {
                    roundToSet = (RoundModel) roundsToShow.iterator().next();
                }
                internalSetSelectedRound(roundToSet);
            }
        }
    }

    private void internalSetSelectedRound(RoundModel round) {
        getTimerPanel().setRoundModel(round);
        phaseEvent(round.getPhase().intValue(), round);
    }
}
