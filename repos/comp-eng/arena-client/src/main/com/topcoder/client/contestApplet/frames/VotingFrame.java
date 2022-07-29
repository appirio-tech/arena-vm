package com.topcoder.client.contestApplet.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;

public final class VotingFrame extends JFrame {

    private static final Color BACKGROUND = Common.WPB_COLOR;
    private static final Color PANEL_BACKGROUND = Common.PB_COLOR;
    private static final GridBagConstraints GBC = Common.getDefaultConstraints();

    private static VotingFrame votingFrame;
    private static JFrame tieBreakVotingFrame;

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ContestApplet contestApplet;
    private final int roundId;

    private String selectedName;

    private VotingFrame(ContestApplet contestApplet, VoteResponse voteResponse) {
        super(voteResponse.getTitle());
        this.contestApplet = contestApplet;
        roundId = voteResponse.getRoundId();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setBackground(BACKGROUND);
        contentPane.setLayout(new GridBagLayout());
        GBC.insets = beginInsets();
        insertInContentPane(createLabel("Elimination Round: " + voteResponse.getRoundName()), 0);
        GBC.insets = middleInsets();
        insertInContentPane(createVotingPanel(voteResponse), 1);
        insertInContentPane(createLabel("* Denotes The Round's Winner - Cannot be voted out"), 2);
        GBC.insets = endInsets();
        GBC.fill = GridBagConstraints.REMAINDER;
        insertInContentPane(createVoteButton(), 3);
        pack();
    }

    private static Insets endInsets() {
        return newInsets(5, 15);
    }

    private static Insets beginInsets() {
        return newInsets(15, 5);
    }

    private static Insets middleInsets() {
        return newInsets(5, 5);
    }

    private static Insets newInsets(int top, int bottom) {
        return new Insets(top, 15, bottom, 15);
    }

    private static void insertInPanel(JComponent component, Container container, int y) {
        insertInPanel(component, container, 0, y, 1);
    }

    private static void insertInPanel(JComponent component, Container container, int x, int y, double weightx) {
        Common.insertInPanel(component, container, GBC, x, y, 1, 1, weightx, 0);
    }

    private void insertInContentPane(JComponent component, int y) {
        insertInPanel(component, getContentPane(), y);
    }

    private static JComponent createLabel(String text) {
        JComponent label = new JLabel(text, JLabel.CENTER);
        label.setForeground(Common.PT_COLOR);
        return label;
    }

    private JComponent createVotingPanel(VoteResponse voteResponse) {
        JComponent panel = new JPanel();
        panel.setBorder(Common.getTitledBorder("Choose Which Team Member You Would Like to Vote Out:"));
        panel.setBackground(BACKGROUND);
        panel.setPreferredSize(new Dimension(355, 260));
        panel.setLayout(new BorderLayout());
        panel.add(createInsideVotingPanel(voteResponse));
        return panel;
    }

    private Component createInsideVotingPanel(VoteResponse voteResponse) {
        JComponent panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GBC.anchor = GridBagConstraints.WEST;
        GBC.fill = GridBagConstraints.WEST;
        UserInfo[] coders = voteResponse.getCoders();
        for (int i = 0; i < coders.length; i++) {
            UserInfo coder = coders[i];
            int rating = coder.getRating();
            boolean isLeader = i == 0;
            boolean isLast = i == coders.length - 1;
            if (i == 0) {
                GBC.insets = beginInsets();
            } else if (isLast) {
                GBC.insets = endInsets();
            } else if (i == 1) {
                GBC.insets = middleInsets();
            }
            Color coderColor = Common.getRankColor(rating);
            addToVotingPanel(panel, coder.getHandle(), coderColor, i, isLeader, isLast, voteResponse.getMaxList());
        }
        GBC.anchor = GridBagConstraints.NORTH;
        return panel;
    }

    private void addToVotingPanel(Container panel, final String name, Color coderColor, int y, boolean isLeader, boolean isLast,
            ArrayList maxList) {
        VotingElement votingElement = new VotingElement(name, PANEL_BACKGROUND, coderColor);
        if (isLeader) {
            insertInPanel(votingElement.getStar(), panel, 0, y, 0);
        } else if (maxList.isEmpty() || maxList.contains(new Integer(y))) {
            AbstractButton radioButton = votingElement.getRadioButton();
            radioButton.setActionCommand(name);
            if (isLast) {
                radioButton.setSelected(true);
            }
            buttonGroup.add(radioButton);
            insertInPanel(radioButton, panel, 1, y, 0);
        }
        insertInPanel(votingElement.getCoderName(), panel, 2, y, 1);
        JButton roundStatsButton = votingElement.getRoundStats();
        roundStatsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRoundStatsRequest(name);
            }
        }
        );
        insertInPanel(roundStatsButton, panel, 3, y, 1);
    }

    private void sendRoundStatsRequest(String name) {
        if (contestApplet != null) {
            contestApplet.sendRoundStatsRequest(roundId, name);
        }
    }

    public static void showFrame(ContestApplet contestApplet, VoteResponse voteResponse) {
        VotingFrame frame = new VotingFrame(contestApplet, voteResponse);
        if (contestApplet != null) {
            Common.setLocationRelativeTo(contestApplet.getCurrentFrame(), frame);
        }
        frame.show();
        byte type = voteResponse.getType();
        switch (type) {
        case VoteResponse.VOTING:
            votingFrame = frame;
            break;
        case VoteResponse.TIE_BREAK_VOTING:
            tieBreakVotingFrame = frame;
            break;
        default:
            throw new IllegalArgumentException("unknown type: " + type);
        }
    }

    private JComponent createVoteButton() {
        JButton button = new JButton("Submit Vote");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonModel selection = buttonGroup.getSelection();
                selectedName = selection.getActionCommand();
                assertNotNull(selectedName);
                VoteConfirmDialog.showDialog(votingFrame, selectedName);
            }
        });
        return button;
    }

    void send() {
        dispose();
        if (contestApplet != null) {
            assertNotNull(selectedName);
            contestApplet.sendVoteBack(roundId, selectedName);
        }
    }

    private static void assertNotNull(Object object) {
        assertTrue(object != null);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new RuntimeException();
        }
    }

    private static void disposeFrame(JFrame frame) {
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void disposeVotingFrame() {
        disposeFrame(votingFrame);
    }

    public static void disposeTieBreakVotingFrame() {
        disposeFrame(tieBreakVotingFrame);
    }

    /*
    public static void main(String[] args) {
        UserInfo[] coders={
            new UserInfo("reid", 3205),
            new UserInfo("dvickrey", 2198),
            new UserInfo("alanm", 1499),
            new UserInfo("bmetz", 1199),
            new UserInfo("Mongus", 899),
        };
        VoteResponse voteResponse=new VoteResponse(-1, "WL1", coders);
        showFrame(null, voteResponse);
    }
    */

}
