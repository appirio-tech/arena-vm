package com.topcoder.client.contestApplet.frames;

/*
* RoomInfoFrame.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.panels.ChatPanel;
import com.topcoder.client.contestApplet.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.panels.table.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.ChallengeTablePanel;
import com.topcoder.client.contestApplet.panels.table.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.UserTablePanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestApplet.widgets.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.RoomView;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 *
 * @author  Alex Roman
 * @version
 */

public final class RoomInfoFrame extends JFrame implements RoomView {

    private RoomModel model;
    private ContestApplet parentFrame = null;

    //private SourceViewer src = null;
    private AbstractSummaryTablePanel challengePanel = null;
    private UserTablePanel userPanel = null;
    private ChatPanel chatPanel = null;
    private boolean once = true;
    private JRadioButton jrb2 = null;


    private boolean enabled = true;
    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        userPanel.setPanelEnabled(on);
        chatPanel.setPanelEnabled(on);
        challengePanel.setPanelEnabled(on);
    }
    
    /**
     * Class constructor
     */
    public RoomInfoFrame(ContestApplet parent, RoomModel room) {
        super("TopCoder Competition Scoreboard");
        parentFrame = parent;
        this.model = room;
        //create();
    }

    public RoomModel getModel() {
        return model;
    }

    public boolean hasRoomModel() {
        return model != null;
    }

    public void showFrame(boolean enabled) {
        if (once) {
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
            once = false;
        }
        show();
        MoveFocus.moveFocus(challengePanel.getTable());
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        AbstractSummaryTablePanel cp;
        // create all the panels/panes
        //src = new SourceViewer(parentFrame);
        if (isLongRound()) {
            cp = new LongSummaryTablePanel(parentFrame, model, this, true);
        } else {
            cp = new ChallengeTablePanel(parentFrame, model, this, true);
        }
        

        UserTablePanel utp = new UserTablePanel(parentFrame);
        JPanel pp = createPrettyTogglePanel();
        ContestSponsorPanel csp = new ContestSponsorPanel(parentFrame, CommonData.getSponsorWatchRoomImageAddr(parentFrame.getSponsorName(), getModel()));
        ChatPanel chat = new ChatPanel(parentFrame);

        utp.setMinimumSize(new Dimension(150, 0));
        utp.setPreferredSize(new Dimension(150, 0));

        JPanel p = new JPanel(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        Common.insertInPanel(utp, p, gbc, 0, 0, 1, 1, 0.0, 0.1);
        gbc.insets = new Insets(0, 10, 0, 0);
        Common.insertInPanel(chat, p, gbc, 1, 0, 1, 1, 1.0, 0.1);

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, cp, p);
        
        this.userPanel = utp;
        sp.setDividerLocation(225);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        cp.setPreferredSize(new Dimension(675, 150));
        p.setPreferredSize(new Dimension(675, 275));
        chat.readonly(true);

        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 0, 15);
        Common.insertInPanel(csp, getContentPane(), gbc, 0, 0, 1, 1, 0.1, 0.0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 0, 15);
        Common.insertInPanel(pp, getContentPane(), gbc, 1, 0, 1, 1, 0.0, 0.0);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 15, 15, 15);
        Common.insertInPanel(sp, getContentPane(), gbc, 0, 1, 2, 1, 0.1, 0.1);

        // globalize needed variables
        challengePanel = cp;
        chatPanel = chat;
        pack();
    }

    private boolean isLongRound() {
        return model.getRoundModel() != null && model.getRoundModel().getRoundType().isLongRound();
    }


    /*
    ////////////////////////////////////////////////////////////////////////////////
    public void hide()
            ////////////////////////////////////////////////////////////////////////////////
    {
        super.hide();
        src.hide();
    }
    */


    ////////////////////////////////////////////////////////////////////////////////
    public boolean getPrettyToggle()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (jrb2.isSelected());
    }

    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

    private JPanel createPrettyTogglePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        if (isLongRound()) {
            return panel;
        }
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        //panel.setPreferredSize(new Dimension(200, 50));
        //panel.setBorder(BorderFactory.createLineBorder(Color.white));

        JLabel jl1 = new JLabel("Pretty : ", SwingConstants.RIGHT);
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
        
        final ButtonGroup group = new ButtonGroup();
        group.add(jrb1);
        group.add(jrb2);
        
        //this.jrb1 = jrb1;
        this.jrb2 = jrb2;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(jrb1);
        buttonPanel.add(jrb2);
        Common.insertInPanel(jl1, panel, gbc, 0, 0, 1, 1, 0.3, 0.1);
        Common.insertInPanel(buttonPanel, panel, gbc, 1, 0, 2, 1, 0.1, 0.1);
        
        JLabel label = new JLabel("View : ", SwingConstants.RIGHT);
        label.setToolTipText("View challenge/test status or point values.");
        label.setForeground(Color.white);
        resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(model.getRoundModel(), new ResultDisplayTypeSelectionPanel.Listener() {
            public void typeChanged(ResultDisplayType newType) {
                challengePanel.updateView(newType);
            }
        });
        Common.insertInPanel(label, panel, gbc, 0, 1, 1, 1, 0.3, 0.1);
        Common.insertInPanel(resultDisplayTypeSelectionPanel, panel, gbc, 1, 1, 2, 1, 0.1, 0.1);
        return (panel);
    }

    public void setModel(RoomModel model) {
        this.model = model;
        create();
        model.addChatView(chatPanel);
        model.addUserListView(userPanel);
        model.addUserListView(chatPanel);
        model.addChallengeView(challengePanel);
        userPanel.updateUserList(model.getUsers());
    }

    public void unsetModel() {
        model.removeChallengeView(challengePanel);
        model.removeUserListView(chatPanel);
        model.removeUserListView(userPanel);
        model.removeChatView(chatPanel);
        model.unsetWatchView();
        hide();
    }


    public void setName(String name) {
        super.setName("TopCoder Competition Scoreboard - " + name);
        repaint();
    }

    public void setStatus(String status) {
    }


    public String toString() {
        return "Room Info Frame - " + model;
    }
}
