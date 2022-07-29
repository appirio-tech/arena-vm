package com.topcoder.client.contestApplet.frames;

/*
* ChallengeFrame.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.UserAssignmentPanel;
import com.topcoder.client.contestApplet.panels.coding.CodingTimerPanel;
import com.topcoder.client.contestApplet.panels.room.TimerPanel;
import com.topcoder.client.contestApplet.panels.table.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.ChallengeTablePanel;
import com.topcoder.client.contestApplet.panels.table.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.widgets.MouseLessTextArea;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestApplet.widgets.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 *
 * @author  Alex Roman
 * @version
 */

public class ChallengeFrame extends JFrame {
	
    public JTextField message;
    private ContestApplet parentFrame = null;

    // declared global for handling/referencing
    private AbstractSummaryTablePanel challengePanel = null;
    private CodingTimerPanel timerPanel = null;
    private boolean once = true;

    private JRadioButton jrb2 = null;
    private UserAssignmentPanel userAssignmentPanel = null;
    private RoomModel room;
    private boolean open = false;

    private boolean enabled = true;
    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        challengePanel.setPanelEnabled(on);
    }

    /**
     * Class constructor
     */
    public ChallengeFrame(ContestApplet parent, RoomModel room) {
        super("TopCoder Competition Arena - Competition Details");
        parentFrame = parent;
        this.room = room;
        create();
    }


    public void showFrame(boolean enabled) {
        if (once) {
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
            once = false;
        }
        show();
        if (!open)
            parentFrame.getRequester().requestOpenSummary(room.getRoomID().longValue());
        open = true;
        MoveFocus.moveFocus(challengePanel.getTable());
    }

    /**
     * Create the room
     */
    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        MouseLessTextArea  mesg;
        // create all the panels/panes
        if (room.getRoundModel().getRoundProperties().hasChallengePhase()) {
            mesg = new MouseLessTextArea("During the challenge phase, you can right click on " +
                    "submitted problems to view a user's source code, or " +
                    "to challenge the validity of the user's problem.");
        } else {
            mesg = new MouseLessTextArea("After the coding phase, you can right click on " +
                    "submitted problems to view a user's source code.");
        }
        JPanel mp = Common.createMessagePanel("Instructions", mesg, 0, 100, Common.BG_COLOR);

        //
        // Changed 6/27/2002 by schveiguy (schveiguy@yahoo.com)
        // Create the source viewer with a challenge button.
        //
//        src = new SourceViewer(parentFrame,true);

        AbstractSummaryTablePanel cp;
        if (room.getRoundModel() != null && room.getRoundModel().getRoundType().isLongRound()) {
            cp = new LongSummaryTablePanel(parentFrame, room, this, true);
        } else {
            cp = new ChallengeTablePanel(parentFrame, room, this, false);
        }

        //
        // Added 6/25/2002 by schveiguy (schveiguy@yahoo.com)
        // This tells the source viewer to use the panel to submit challenges.
        // Since the Source viewer doesn't need to implement ChallengeView, and
        // I'm pretty sure that if it did, incorrect things would happen, it is
        // easier for the source viewer to use the panel to submit challenges.
        //
//        src.setPanel(cp);

        CodingTimerPanel tp = new CodingTimerPanel(parentFrame);
        JPanel pp = createPrettyTogglePanel();

        this.timerPanel = tp;

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        cp.setPreferredSize(new Dimension(675, 310));

        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(mp, getContentPane(), gbc, 0, 0, 1, 3, 1.0, 0.0);

        gbc.insets = new Insets(25, 5, 5, 15);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(tp, getContentPane(), gbc, 1, 0, 1, 1, 0.0, 0.0);

        gbc.insets = new Insets(0, 5, 0, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(pp, getContentPane(), gbc, 1, 1, 1, 1, 0.0, 0.0);

        if (parentFrame.getModel().getUserInfo().isCaptain() &&
                (parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_CODER_ROOM ||
                parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_PRACTICE_CODER_ROOM ||
                parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_ADMIN_ROOM)) {

            userAssignmentPanel = new UserAssignmentPanel(parentFrame, room);

            gbc.insets = new Insets(5, 15, 15, 15);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.BOTH;
            Common.insertInPanel(userAssignmentPanel, getContentPane(), gbc, 0, 3, 2, 1, 0.0, 1.0);
        }
        
        if(parentFrame.getCompanyName().equals("SunOnsite") && !room.isPracticeRoom())
        {
            JPanel vrp = createViewResultsPanel();
            gbc.insets = new Insets(0, 5, 0, 15);
            gbc.fill = GridBagConstraints.BOTH;
            Common.insertInPanel(vrp, getContentPane(), gbc, 1, 2, 1,1 , 0.0, 0.0);
            
            
        }

        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(cp, getContentPane(), gbc, 0, 4, 2, 1, 0.1, 0.1);

        // globalize needed variables
        challengePanel = cp;

        pack();
    }

    /**
     * Clear out all room data
     */
// --Recycle Bin START (5/8/02 2:09 PM):
//  ////////////////////////////////////////////////////////////////////////////////
//  public void clear()
//  ////////////////////////////////////////////////////////////////////////////////
//  {
//    challengePanel.clear();
//  }
// --Recycle Bin STOP (5/8/02 2:09 PM)

    public void hide() {
        super.hide();
        challengePanel.closeSourceViewer();
        if (open && enabled)
            parentFrame.getRequester().requestCloseSummary(room.getRoomID().longValue());
        open = false;
    }

//    ////////////////////////////////////////////////////////////////////////////////
//    public void clearPracticer(Integer index)
//            ////////////////////////////////////////////////////////////////////////////////
//    {
//        challengePanel.clearPracticer(index);
//    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean getPrettyToggle()
    ////////////////////////////////////////////////////////////////////////////////
    {
        return (jrb2.isSelected());
    }
    
    public ResultDisplayType getViewType() {
        return resultDisplayTypeSelectionPanel.getSelectedType();
    }
    
    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    private JPanel createPrettyTogglePanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        if (room.getRoundModel().getRoundType().isLongRound()) {
            return panel;
        }
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel jl1 = new JLabel("Pretty : ", SwingConstants.LEFT);
        JRadioButton jrb1 = new JRadioButton("Off", true);
        JRadioButton jrb2 = new JRadioButton("On", false);
        
       
        jl1.setToolTipText("Reformat source code for readability.");
        jl1.setForeground(Color.white);
        jrb1.setBackground(Common.WPB_COLOR);
        jrb2.setBackground(Common.WPB_COLOR);
        jrb1.setForeground(Color.white);
        jrb2.setForeground(Color.white);
        jrb1.setOpaque(false);
        jrb2.setOpaque(false);
        jrb1.setActionCommand("Standard");
        jrb2.setActionCommand("VI");
        
        final ButtonGroup group_pretty = new ButtonGroup();
        group_pretty.add(jrb1);
        group_pretty.add(jrb2);

        this.jrb2 = jrb2;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(jrb1);
        buttonPanel.add(jrb2);

        Common.insertInPanel(jl1, panel, gbc, 0, 0, 1, 1, 0.3, 0.1);
        Common.insertInPanel(buttonPanel, panel, gbc, 1, 0, 2, 1, 0.1, 0.1);
        //Common.insertInPanel(jrb2, panel, gbc, 2, 0, 1, 1, 0.1, 0.1);
        
        JLabel label = new JLabel("View : ", SwingConstants.LEFT);
        label.setToolTipText("View challenge/test status or point values.");
        label.setForeground(Color.white);
        resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(room.getRoundModel(), new ResultDisplayTypeSelectionPanel.Listener() {
            public void typeChanged(ResultDisplayType newType) {
                challengePanel.updateView(newType);
            }
        });
        Common.insertInPanel(label, panel, gbc, 0, 1, 1, 1, 0.3, 0.1);
        Common.insertInPanel(resultDisplayTypeSelectionPanel, panel, gbc, 1, 1, 2, 1, 0.1, 0.1);
        return (panel);
    }

   

    public TimerPanel getTimerPanel() {
        return (timerPanel);
    }


    ////////////////////////////////////////////////////////////////////////////////
    private JPanel createViewResultsPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();

        //JLabel jl1 = new JLabel("Pretty : ", SwingConstants.LEFT);
        //JRadioButton jrb1 = new JRadioButton("Off", true);
        //JRadioButton jrb2 = new JRadioButton("On", false);
        JButton btn = new JButton("System Test Results");
        
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //request results here
                parentFrame.getRequester().requestSystestResults(room.getRoundModel().getRoundID().intValue());
                
            }
        });

        panel.setOpaque(false);
        //btn.setBackground(Common.LIGHT_GREY);
        //btn.setForeground(Color.white);
        //btn.setOpaque(false);

        Common.insertInPanel(btn, panel, gbc, 0, 0, 1, 1, 1, 1);

        return (panel);
    }

    
    // ------------------------------------------------------------
    // Event Handling
    // ------------------------------------------------------------

    public AbstractSummaryTablePanel getChallengePanel() {
        return (this.challengePanel);
    }


    public boolean hasAssignmentPanel() {
        return userAssignmentPanel != null;
    }

    public UserAssignmentPanel getAssignmentPanel() {
        return userAssignmentPanel;
    }
}
