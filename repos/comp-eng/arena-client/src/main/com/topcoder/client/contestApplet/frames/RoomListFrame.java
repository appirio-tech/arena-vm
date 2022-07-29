package com.topcoder.client.contestApplet.frames;

/*
* RoomListFrame.java
*
* Created on July 10, 2000, 4:08 PM
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.panels.coding.CodingTimerPanel;
import com.topcoder.client.contestApplet.panels.room.TimerPanel;
import com.topcoder.client.contestApplet.panels.table.RoomInfoTablePanel;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoundView;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundProperties;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class RoomListFrame extends JFrame implements PhaseListener, RoundView {

    private ContestApplet parentFrame = null;
    private RoomInfoTablePanel roomInfoPanel = null;
    private TimerPanel timerPanel = null;

    private JLabel roundsLabel = new JLabel("Rounds: ");
    private JComboBox rounds = new JComboBox();
    private static RoomListFrame singleton;
    
    private Long selectedRoundID = null;
    private boolean enabled = true;
    private Set roundsToShow = Collections.synchronizedSet(new LinkedHashSet());
    
    public Long getSelectedRoundID() {
    	return selectedRoundID;
    }
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            rounds.setEnabled(true);
        } else {
            rounds.setEnabled(false);
        }
        if(roomInfoPanel != null)
            roomInfoPanel.setEnabled(on);
    }

    /**
     * Class constructor
     */
    public RoomListFrame(ContestApplet parentFrame) {
        super("Competition Room Details");
        this.parentFrame = parentFrame;

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        create();

        // set the placement
        Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
    }

    public static RoomListFrame getInstance(ContestApplet ca) {
        if (singleton == null)
            singleton = new RoomListFrame(ca);
        return singleton;
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();
        JLabel asterisk = new JLabel();

        // create all the panels/panes
        RoomInfoTablePanel ritp = new RoomInfoTablePanel(parentFrame);
        CodingTimerPanel ctp = new CodingTimerPanel(parentFrame);

        // set misc properties
        ritp.setMinimumSize(new Dimension(275, 350));
        ritp.setPreferredSize(new Dimension(275, 350));
        asterisk.setForeground(Common.STATUS_COLOR);
        asterisk.setText("NOTE: * represents a close race (50 points)");

        rounds.setMinimumSize(new Dimension(200, 20));
        rounds.setPreferredSize(new Dimension(250, 20));
        rounds.setRenderer(new DefaultListCellRenderer() {
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
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });
        rounds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RoundModel roundModel = (RoundModel) rounds.getSelectedItem();
                if (roundModel != null) {
                    roomInfoPanel.setRound(roundModel);
                    selectedRoundID = roundModel.getRoundID();
                    setRoomPhase(roundModel);
                } else {
                	selectedRoundID = null;
                }
            }
        });
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Common.WPB_COLOR);
        roundsLabel.setForeground(Common.STATUS_COLOR);
        rounds.setBackground(Common.WPB_COLOR);
        rounds.setForeground(Common.STATUS_COLOR);

        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(roundsLabel, panel, gbc, 0, 0, 1, 1, 0, 0);

        gbc.insets = new Insets(0,10,0,0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(rounds, panel, gbc, 1, 0, 1, 1, 1, 1);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(new ContestSponsorPanel(parentFrame, CommonData.getSponsorScoreBoardImageAddr(parentFrame.getSponsorName(), null)), getContentPane(), gbc, 0, 0, 1, 1, 0.0, 0.0);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(ctp, getContentPane(), gbc, 1, 0, 1, 1, 0, 0);
        
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(ritp, getContentPane(), gbc, 0, 1, 2, 1, 0.1, 1.0);

        gbc.insets = new Insets(5, 20, 15, 20);
        Common.insertInPanel(panel, getContentPane(), gbc, 0, 2, 2, 1, 0.1, 1.0);
        Common.insertInPanel(asterisk, getContentPane(), gbc, 0, 3, 2, 1, 0.1, 0.1);

        this.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            	show();
            }
        });
        
        parentFrame.getModel().getRoundViewManager().addListener(this);
        bindToActiveRounds();
        
        // globalize variables
        this.roomInfoPanel = ritp;
        this.timerPanel = ctp;
        pack();
    }
    
    private void setRoomPhase(RoundModel roundModel) {
        setTimerPanelRoundModel(roundModel);
        phaseEvent(roundModel.getPhase().intValue(), roundModel);
    }

    public void reset() {
        roomInfoPanel.clear();
    }

    public void hide() {
        super.hide();
        reset();
        if(enabled) {
            parentFrame.getRequester().requestCloseLeaderBoard();
        }
    }
    
    public void show() {
    	reloadRounds();
        super.show();
    }
    
    public void reloadRounds() {
    	Long savedSelectedRoundID;
    	if (selectedRoundID == null) {
    		savedSelectedRoundID = null;
    	} else {
    		savedSelectedRoundID = new Long(selectedRoundID.longValue());
    	}
    	
        reset();
        rounds.removeAllItems();
        
        if (roundsToShow.size() > 0) {
            RoundModel[] roundsTemp = (RoundModel[]) roundsToShow.toArray(new RoundModel[roundsToShow.size()]);
            RoundModel round = roundsTemp[0];
            rounds.addItem(round);
            roomInfoPanel.setRound(round);
            rounds.setSelectedIndex(0);
            setRoomPhase(round);
            
            for (int i = 1; i < roundsTemp.length; i++) {
                round = roundsTemp[i];
                rounds.addItem(round);
                if ((round.getRoundID()).equals(savedSelectedRoundID)) {
                	roomInfoPanel.setRound(round);
                	rounds.setSelectedIndex(i);
                	setRoomPhase(round);
                }
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
