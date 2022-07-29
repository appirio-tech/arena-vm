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
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contestantMessages.response.WLTeamsInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.data.WLTeamInfo;

public final class WLTeamsInfoFrame extends JFrame {

    private static final Color BACKGROUND = Common.WPB_COLOR;
    private static final Color PANEL_BACKGROUND = Common.PB_COLOR;
    private static final GridBagConstraints GBC = Common.getDefaultConstraints();

    private WLTeamsInfoFrame(WLTeamsInfoResponse wlTeamInfoResponse) {
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

    private JComponent createPanel(WLTeamsInfoResponse wlTeamInfoResponse) {
        JComponent panel = new JPanel();
        double prizeThreshold = wlTeamInfoResponse.getPrizeThreshold();
        panel.setBorder(Common.getTitledBorder("Teams Info (prize threshold = " + prizeThreshold + "):"));
        panel.setBackground(BACKGROUND);
        panel.setPreferredSize(new Dimension(250, 190));
        panel.setLayout(new BorderLayout());
        panel.add(createInsidePanel(wlTeamInfoResponse));
        return panel;
    }

    private Component createInsidePanel(WLTeamsInfoResponse wlTeamInfoResponse) {
        JComponent panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GBC.anchor = GridBagConstraints.WEST;
        GBC.fill = GridBagConstraints.WEST;
        WLTeamInfo[] teams = wlTeamInfoResponse.getTeams();
        Arrays.sort(teams);
        for (int i = 0; i < teams.length; i++) {
            WLTeamInfo team = teams[i];
            boolean isLast = i == teams.length - 1;
            if (i == 0) {
                GBC.insets = beginInsets();
            } else if (isLast) {
                GBC.insets = endInsets();
            } else if (i == 1) {
                GBC.insets = middleInsets();
            }
            double points = team.getPoints();
            addToPanel(panel, team.getName(), i, points);
        }
        GBC.anchor = GridBagConstraints.NORTH;
        return panel;
    }

    private void addToPanel(Container panel, String name, int y, double points) {
        VotingElement votingElement = new VotingElement(name, PANEL_BACKGROUND, Common.PT_COLOR);
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

    public static void showFrame(JFrame currentFrame, WLTeamsInfoResponse wlTeamInfoResponse) {
        JFrame frame = new WLTeamsInfoFrame(wlTeamInfoResponse);
        if (currentFrame != null) {
            Common.setLocationRelativeTo(currentFrame, frame);
        }
        frame.show();
    }

    /*
    public static void main(String[] args) {
        WLTeamInfo[] teams={
            new WLTeamInfo("Team A", 363),
            new WLTeamInfo("Team B", 0),
            new WLTeamInfo("Team C", 6230),
            new WLTeamInfo("Team D", 88),
            new WLTeamInfo("Team E", 1000),
        };
        WLTeamsInfoResponse wlTeamInfoResponse=new WLTeamsInfoResponse(teams, 2222);
        showFrame(null, wlTeamInfoResponse);
    }
    */

}
