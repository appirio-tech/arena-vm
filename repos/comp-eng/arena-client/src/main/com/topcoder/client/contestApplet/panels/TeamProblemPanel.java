package com.topcoder.client.contestApplet.panels;

//import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.rooms.*;
//import com.topcoder.client.contestApplet.listener.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.netCommon.contest.ContestConstants;

public final class TeamProblemPanel extends JPanel {

    private ContestApplet ca = null;
    private JComboBox problemList = null;
    private JComboBox componentList = null;
    private JLabel phaseStatus = null;
    private JLabel phaseDesc = null;
    private boolean isCoderRoom = true;

    ////////////////////////////////////////////////////////////////////////////////
    public TeamProblemPanel(ContestApplet ca, boolean flag)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout());

        this.ca = ca;
        this.isCoderRoom = flag;

        setOpaque(false);
        setBorder(new RoundBorder(Common.PB_COLOR, 5, true));
        setMinimumSize(new Dimension(0, 60));
        setPreferredSize(new Dimension(0, 60));

        create();
        setPhase(ContestConstants.INACTIVE_PHASE);
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

        JComboBox c1 = Common.createComboBox();
        JComboBox c2 = Common.createComboBox();

        JButton summary = Common.getImageButton("g_summary_but.gif", ca);
        summary.setMnemonic('s');

        summary.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusWindowEvent(e);
            }
        });
        back.setBackground(Common.BG_COLOR);

        // insert the problem list
        gbc.insets = new Insets(4, 5, -1, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        Common.insertInPanel(c1, back, gbc, 1, 0, 1, 1, 0.0, 0.1);

        //insert the component list
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(-1, 5, 4, 5);
        Common.insertInPanel(c2, back, gbc, 1, 1, 1, 1, 0.0, 0.1);

        gbc.insets = new Insets(0, 10, -1, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
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

        // globalize variables;
        problemList = c1;
        componentList = c2;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setPhase(int index)
            ////////////////////////////////////////////////////////////////////////////////
    {
        switch (index) {
        case ContestConstants.INACTIVE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 0. COMPETITION INACTIVE");
            phaseDesc.setText("The contest is currently not running.");
            break;
        case ContestConstants.REGISTRATION_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 0. REGISTRATION PHASE");
            phaseDesc.setText("Select event registration from the main menu.");
            break;
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 0. STARTS IN");
            phaseDesc.setText("The competition will start at the end of the countdown.");
            break;
        case ContestConstants.CODING_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 1. CODING PHASE");
            if (isCoderRoom) {
                phaseDesc.setText("Select a problem from the problem list.");
            } else {
                phaseDesc.setText("Please select the status window from the tools menu.");
            }
            break;
        case ContestConstants.INTERMISSION_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 2. INTERMISSION");
            phaseDesc.setText("Please wait for the challenge phase to start.");
            break;
        case ContestConstants.CHALLENGE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 3. CHALLENGE PHASE");
            phaseDesc.setText("Please select the status window from the tools menu.");
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            phaseStatus.setText("COMPETITION STATUS : 4. SYSTEM TESTING PHASE");
            phaseDesc.setText("Please wait for the system test phase to end.");
            break;
        case ContestConstants.MODERATED_CHATTING_PHASE:
            phaseStatus.setText("MODERATED CHAT STATUS : 1. CHATTING");
            phaseDesc.setText("Ask questions with /moderator.");
            break;
        default:
            System.err.println("Unknown phase (" + index + ").");
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public JComboBox getProblemList()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (problemList);
    }

    public JComboBox getComponentList() {
        return componentList;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void statusWindowEvent(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ((CoderRoomInterface) ca.getRoomManager().getCurrentRoom()).challengeButtonEvent(e);
    }
}
