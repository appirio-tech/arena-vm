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

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contestantMessages.response.WLMyTeamInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;

public final class WLMyTeamInfoFrame extends JFrame {

    private static final Color BACKGROUND = Common.WPB_COLOR;
    private static final Color PANEL_BACKGROUND = Common.PB_COLOR;
    private static final GridBagConstraints GBC = Common.getDefaultConstraints();

    private WLMyTeamInfoFrame(WLMyTeamInfoResponse wlTeamInfoResponse) {
        super("Team Info");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setBackground(BACKGROUND);
        contentPane.setLayout(new GridBagLayout());
        GBC.insets = beginInsets();
        insertInContentPane(createPanel(wlTeamInfoResponse), 1);
        GBC.insets = endInsets();
        GBC.fill = GridBagConstraints.REMAINDER;
        insertInContentPane(createOkButton(), 2);
        pack();
    }

    private JComponent createPanel(WLMyTeamInfoResponse wlTeamInfoResponse) {
        JComponent panel = new JPanel();
        panel.setBorder(Common.getTitledBorder("My Team Info:"));
        panel.setBackground(BACKGROUND);
        panel.setPreferredSize(new Dimension(250, 190));
        panel.setLayout(new BorderLayout());
        panel.add(createInsidePanel(wlTeamInfoResponse));
        return panel;
    }

    private Component createInsidePanel(WLMyTeamInfoResponse wlTeamInfoResponse) {
        JComponent panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GBC.anchor = GridBagConstraints.WEST;
        GBC.fill = GridBagConstraints.WEST;
        VoteResultsCoder[] coders = wlTeamInfoResponse.getCoders();
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
            double points = coder.getPoints();
            addToPanel(panel, coder.getHandle(), coderColor, i, points);
        }
        GBC.anchor = GridBagConstraints.NORTH;
        return panel;
    }

    private void addToPanel(Container panel, String name, Color coderColor, int y, double points) {
        VotingElement votingElement = new VotingElement(name, PANEL_BACKGROUND, coderColor);
        insertInPanel(votingElement.getCoderName(), panel, 0, y, 1);
        insertInPanel(createLabel("" + points), panel, 1, y, 1);
    }

    private static JComponent createLabel(String text) {
        JComponent label = new JLabel(text, JLabel.CENTER);
        label.setForeground(Common.PT_COLOR);
        return label;
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

    private static Insets beginInsets() {
        return newInsets(15, 5);
    }

    private static Insets middleInsets() {
        return newInsets(5, 5);
    }

    private static Insets endInsets() {
        return newInsets(5, 15);
    }

    private static Insets newInsets(int top, int bottom) {
        return new Insets(top, 15, bottom, 15);
    }

    public static void showFrame(JFrame currentFrame, WLMyTeamInfoResponse wlTeamInfoResponse) {
        JFrame frame = new WLMyTeamInfoFrame(wlTeamInfoResponse);
        if (currentFrame != null) {
            Common.setLocationRelativeTo(currentFrame, frame);
        }
        frame.show();
    }

    /*
    public static void main(String[] args) {
        VoteResultsCoder[] coders={
            new VoteResultsCoder("reid", 3205, 513.33),
            new VoteResultsCoder("dvickrey", 2198, 238.00),
            new VoteResultsCoder("alanm", 1499, 0),
            new VoteResultsCoder("bmetz", 1199, 363.3),
            new VoteResultsCoder("Mongus", 899, 111.30),
        };
        WLMyTeamInfoResponse wlTeamInfoResponse=new WLMyTeamInfoResponse(coders);
        showFrame(null, wlTeamInfoResponse);
    }
    */

}
