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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;

public final class VotingResultsFrame extends JFrame {

    private static final Color BACKGROUND = Common.WPB_COLOR;
    private static final Color PANEL_BACKGROUND = Common.PB_COLOR;
    private static final GridBagConstraints GBC = Common.getDefaultConstraints();

    private VotingResultsFrame(VoteResultsResponse voteResultsResponse) {
        super("Voting Results");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setBackground(BACKGROUND);
        contentPane.setLayout(new GridBagLayout());
        GBC.insets = beginInsets();
        insertInContentPane(createLabel("Elimination Round: " + voteResultsResponse.getRoundName()), 0);
        GBC.insets = middleInsets();
        insertInContentPane(createPanel(voteResultsResponse), 1);
        GBC.insets = endInsets();
        GBC.fill = GridBagConstraints.REMAINDER;
        insertInContentPane(createOkButton(), 2);
        pack();
    }

    private JComponent createPanel(VoteResultsResponse voteResultsResponse) {
        JComponent panel = new JPanel();
        panel.setBorder(Common.getTitledBorder("Results Of The Vote:"));
        panel.setBackground(BACKGROUND);
        panel.setPreferredSize(new Dimension(250, 190));
        panel.setLayout(new BorderLayout());
        panel.add(createInsidePanel(voteResultsResponse));
        return panel;
    }

    private Component createInsidePanel(VoteResultsResponse voteResultsResponse) {
        JComponent panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GBC.anchor = GridBagConstraints.WEST;
        GBC.fill = GridBagConstraints.WEST;
        VoteResultsCoder[] coders = voteResultsResponse.getCoders();
        for (int i = 0; i < coders.length; i++) {
            VoteResultsCoder coder = coders[i];
            int rating = coder.getRating();
            boolean isLast = i == coders.length - 1;
            if (i == 0) {
                GBC.insets = beginInsets();
            } else if (isLast) {
                GBC.insets = endInsets();
            } else if (i == 1) {
                GBC.insets = middleInsets();
            }
            Color coderColor = Common.getRankColor(rating);
            String numVotes;
            if (coder.isTieBreakVictim()) {
                numVotes = (coder.getVotes() - 1) + "+1";
            } else {
                numVotes = "" + coder.getVotes();
            }
            addToPanel(panel, coder.getHandle(), coderColor, i, numVotes);
        }
        GBC.anchor = GridBagConstraints.NORTH;
        return panel;
    }

    private void addToPanel(Container panel, String name, Color coderColor, int y, String numVotes) {
        VotingElement votingElement = new VotingElement(name, PANEL_BACKGROUND, coderColor);
        insertInPanel(votingElement.getCoderName(), panel, 0, y, 1);
        insertInPanel(createLabel(numVotes), panel, 1, y, 1);
    }

    private JComponent createOkButton() {
        JButton button = new JButton("Ok");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        return button;
    }

    private static Insets middleInsets() {
        return newInsets(5, 5);
    }

    private static Insets endInsets() {
        return newInsets(5, 15);
    }

    private void insertInContentPane(JComponent component, int y) {
        insertInPanel(component, getContentPane(), y);
    }

    private static void insertInPanel(JComponent component, Container container, int y) {
        insertInPanel(component, container, 0, y);
    }

    private static void insertInPanel(JComponent component, Container container, int x, int y) {
        insertInPanel(component, container, x, y, 1);
    }

    private static void insertInPanel(JComponent component, Container container, int x, int y, double weightx) {
        Common.insertInPanel(component, container, GBC, x, y, 1, 1, weightx, 0);
    }

    private static JComponent createLabel(String text) {
        JComponent label = new JLabel(text, JLabel.CENTER);
        label.setForeground(Common.PT_COLOR);
        return label;
    }

    private static Insets beginInsets() {
        return newInsets(15, 5);
    }

    private static Insets newInsets(int top, int bottom) {
        return new Insets(top, 15, bottom, 15);
    }

    public static void showFrame(ContestApplet contestApplet, VoteResultsResponse voteResultsResponse) {
        JFrame frame = new VotingResultsFrame(voteResultsResponse);
        if (contestApplet != null) {
            Common.setLocationRelativeTo(contestApplet.getCurrentFrame(), frame);
        }
        frame.show();
    }

    /*
    public static void main(String[] args) {
        VoteResultsCoder[] coders={
            new VoteResultsCoder("reid", 3205, 0, false),
            new VoteResultsCoder("dvickrey", 2198, 1, false),
            new VoteResultsCoder("alanm", 1499, 2, false),
            new VoteResultsCoder("bmetz", 1199, 3, false),
            new VoteResultsCoder("Mongus", 899, 4, true),
        };
        VoteResultsResponse voteResultsResponse=new VoteResultsResponse("WL", coders);
        showFrame(null, voteResultsResponse);
    }
    */

}
