package com.topcoder.client.contestApplet.panels;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.rooms.CoderRoom;
import com.topcoder.client.contestApplet.widgets.RoundBorder;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ContestConstants;

public final class ProblemPanel extends JPanel {

    private ContestApplet ca = null;
    private JComponent problemSelector = null;
    private JLabel phaseStatus = null;
    private JLabel phaseDesc = null;
    private JButton summary = null;
    private CoderRoom coderRoom;

    ////////////////////////////////////////////////////////////////////////////////
    public ProblemPanel(ContestApplet ca, CoderRoom coderRoom, JComponent problemSelector)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout());
        this.ca = ca;
        this.coderRoom = coderRoom;
        
        setOpaque(false);
        setBorder(new RoundBorder(Common.PB_COLOR, 5, true));
        setMinimumSize(new Dimension(0, 60));
        setPreferredSize(new Dimension(0, 60));
        
        this.problemSelector = problemSelector;
        create();
        setPhase(ContestConstants.INACTIVE_PHASE);
    }
    
    private boolean enabled = true;
    public void setPanelEnabled(boolean on) {
        enabled = on;
        refreshSummaryButton();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        JPanel back = new JPanel(new GridBagLayout());

        phaseStatus = new JLabel("");
        phaseDesc = new JLabel("");

        phaseStatus.setFont(new Font("SansSerif", Font.PLAIN, 10));
        phaseStatus.setForeground(Common.THF_COLOR);
        phaseDesc.setFont(new Font("SansSerif", Font.PLAIN, 10));
        phaseDesc.setForeground(Color.white);

        JLabel seperator = new JLabel(Common.getImage("dotted_line.gif", ca));
        JLabel seperator2 = new JLabel(Common.getImage("dotted_line.gif", ca));

        JComponent c1 = problemSelector;

//    JButton summary = Common.getImageButton("g_summary_but.gif", ca);

        // AdamSelene - 5/27/02 - merge - above line commented, below from ccpi merge.
        summary = Common.getImageButton("g_summary_but.gif", ca);
        summary.setMnemonic('s');
        // end merge changes

        //summary.addActionListener(new al("actionPerformed", "statusWindowEvent", this));
        summary.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(enabled) {
                    statusWindowEvent(e);
                }
            }
        });
        back.setBackground(Common.BG_COLOR);

        // insert the problem statement text area
        gbc.insets = new Insets(0, 0, -1, 10);
        gbc.insets = new Insets(0, 0, -1, 0);

        // insert the problem list
        gbc.insets = new Insets(0, 5, -1, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        Common.insertInPanel(c1, back, gbc, 1, 0, 1, 2, 0.0, 0.1);

        gbc.insets = new Insets(0, 10, -1, 5);
        Common.insertInPanel(seperator, back, gbc, 2, 0, 1, 2, 0.0, 0.1);
        Common.insertInPanel(summary, back, gbc, 3, 0, 1, 2, 0.0, 0.1);
        Common.insertInPanel(seperator2, back, gbc, 4, 0, 1, 2, 0.0, 0.1);

        // insert the problem statement text area
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 0, 0);
        Common.insertInPanel(phaseStatus, back, gbc, 5, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(0, 10, 10, 0);
        Common.insertInPanel(phaseDesc, back, gbc, 5, 1, 1, 1, 1.0, 0.1);

        // insert the panel in the root titled panel
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        Common.insertInPanel(back, this, gbc, 0, 0, 1, 1, 0.1, 0.1);

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setPhase(int index)
            ////////////////////////////////////////////////////////////////////////////////
    {
        switch (index) {
        case ContestConstants.INACTIVE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : COMPETITION INACTIVE");
            phaseDesc.setText("The contest is currently not running.");
            break;
        case ContestConstants.REGISTRATION_PHASE:
            phaseStatus.setText("COMPETITION STATUS : REGISTRATION PHASE");
            phaseDesc.setText("Select event registration from the main menu.");
            break;
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
            phaseStatus.setText("COMPETITION STATUS : STARTS IN");
            phaseDesc.setText("The competition will start at the end of the countdown.");
            break;
        case ContestConstants.CODING_PHASE:
            phaseStatus.setText("COMPETITION STATUS : CODING PHASE");
            phaseDesc.setText("Select a problem from the problem list.");
            break;
        case ContestConstants.INTERMISSION_PHASE:
            phaseStatus.setText("COMPETITION STATUS : INTERMISSION");
            phaseDesc.setText("Please wait for the challenge phase to start.");
            break;
        case ContestConstants.CHALLENGE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : CHALLENGE PHASE");
            phaseDesc.setText("Please select the status window from the tools menu.");
            break;
        case ContestConstants.VOTING_PHASE:
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : SYSTEM TESTING PHASE");
            phaseDesc.setText("Please wait for the system test phase to end.");
            break;
        case ContestConstants.MODERATED_CHATTING_PHASE:
            phaseStatus.setText("MODERATED CHAT STATUS : CHATTING");
            phaseDesc.setText("Ask questions with /moderator.");
            break;
        default:
            System.err.println("Unknown phase (" + index + ").");
            break;
        }
        refreshSummaryButton();
    }

    private void refreshSummaryButton() {
        boolean summaryEnabled = enabled && coderRoom.getRoomModel() != null && coderRoom.getRoomModel().getRoundModel().canDisplaySummary();
        summary.setEnabled(summaryEnabled);
        if(summaryEnabled) {
            summary.setIcon(Common.getImage("g_summary_but.gif", ca));
        } else {
            summary.setIcon(Common.getImage("no_g_summary_but.gif", ca));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void statusWindowEvent(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        coderRoom.challengeButtonEvent(e);
    }
}
