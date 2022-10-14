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
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.netCommon.contestantMessages.response.data.RoundStatsProblem;

public final class RoundStatsFrame extends JFrame {

    private static final Color BACKGROUND = Common.WPB_COLOR;
    private static final Color PANEL_BACKGROUND = Common.PB_COLOR;
    private static final GridBagConstraints GBC = Common.getDefaultConstraints();

    private final ContestApplet contestApplet;
    private final Coder coder;
    private final int roundId;
    private final String coderName;
    private final SourceViewer sourceViewer;

    private RoundStatsFrame(ContestApplet contestApplet, RoundStatsResponse roundStatsResponse, Coder coder, int roundId) {
        super("Round Stats");
        this.contestApplet = contestApplet;
        this.coder = coder;
        this.roundId = roundId;
        coderName = roundStatsResponse.getCoderName();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setBackground(BACKGROUND);
        contentPane.setLayout(new GridBagLayout());
        GBC.insets = beginInsets();
        insertInContentPane(createLabel("Elimination Round: " + roundStatsResponse.getRoundName()), 0);
        GBC.insets = middleInsets();
        insertInContentPane(createPanel(roundStatsResponse), 1);
        GBC.insets = endInsets();
        GBC.fill = GridBagConstraints.REMAINDER;
        insertInContentPane(createOkButton(), 2);
        pack();
        sourceViewer = new SourceViewer(contestApplet);
    }

    private JComponent createPanel(RoundStatsResponse roundStatsResponse) {
        JComponent panel = new JPanel();
        panel.setBorder(Common.getTitledBorder("Statistics for team member " + coderName + ":"));
        panel.setBackground(BACKGROUND);
        panel.setPreferredSize(new Dimension(600, 170));
        panel.setLayout(new BorderLayout());
        panel.add(createInsidePanel(roundStatsResponse));
        return panel;
    }

    private Component createInsidePanel(RoundStatsResponse roundStatsResponse) {
        JComponent panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GBC.anchor = GridBagConstraints.WEST;
        GBC.fill = GridBagConstraints.WEST;
        RoundStatsProblem[] problems = roundStatsResponse.getProblems();
        GBC.insets = beginInsets();
        insertInPanel(createLabel("Problem"), panel, 0, 0);
        insertInPanel(createLabel("Points"), panel, 1, 0);
        insertInPanel(createLabel("Status"), panel, 2, 0);
        insertInPanel(createLabel("Time to Submit"), panel, 3, 0);
        GBC.insets = middleInsets();
        for (int i = 0; i < problems.length; i++) {
            RoundStatsProblem problem = problems[i];
            boolean isLast = i == problems.length - 1;
            if (isLast) {
                GBC.insets = endInsets();
            }
            String className = problem.getClassName();
            double earnedPoints = problem.getEarnedPoints();
            double pointValue = problem.getPointValue();
            String statusString = problem.getStatusString();
            String timeToSubmit = problem.getTimeToSubmit();
            long componentId = problem.getComponentId();
            addToPanel(panel, i, className, earnedPoints, pointValue, statusString, timeToSubmit, componentId);
        }
        GBC.anchor = GridBagConstraints.NORTH;
        return panel;
    }

    private void addToPanel(JComponent panel, int y, String className, double earnedPoints, double pointValue, String statusString,
            String timeToSubmit, long componentId) {
        y++;
        insertInPanel(createProblemLabel(className), panel, 0, y);
        String earnedPointsStr;
        if (earnedPoints == 0) {
            earnedPointsStr = "0";
        } else {
            earnedPointsStr = "" + earnedPoints;
        }
        String points = earnedPointsStr + "/" + ((int) pointValue);
        insertInPanel(createProblemLabel(points), panel, 1, y);
        insertInPanel(createProblemLabel(statusString), panel, 2, y);
        insertInPanel(createProblemLabel(timeToSubmit), panel, 3, y);
        final JComponent viewCodeButton = createViewCodeButton(componentId);
        insertInPanel(viewCodeButton, panel, 4, y);
        boolean visible = !statusString.equals("Unopened");
        if (visible) {
            final CoderComponent component = coder.getComponent(new Long(componentId));
            component.addListener(new CoderComponent.Listener() {
                public void coderComponentEvent(CoderComponent coderComponent) {
                    if (coderComponent.hasSourceCode()) {
                        sourceViewer.clear();
                        sourceViewer.setCode(coderComponent.getSourceCode(), coderComponent.getSourceCodeLanguage());
                        Double points = coderComponent.getComponent().getPoints();
                        sourceViewer.setTitle(coderComponent.getCoder().getHandle() + "'s " + Common.formatNoFractions(points) +
                                "-point Problem");
                        String challengeHandle = coderComponent.getCoder().getHandle();
                        sourceViewer.setWriter(challengeHandle);
                        sourceViewer.show();
                    } else {
                        throw new IllegalStateException("Missing source code for coder: " + coderComponent.getCoder().getHandle());
                    }
                }
            });
            ProblemModel problem = component.getComponent().getProblem();
            problem.addListener(new ProblemModel.Listener() {
                public void updateProblemModel(ProblemModel problemModel) {
                    if (problemModel.hasProblemStatement()) {
                        if (sourceViewer == null) {
                            throw new IllegalStateException(
                                    "Source viewer not initialized!"
                            );
                        }
                        //sourceViewer.setTitle(problemModel.getName());
                        //sourceViewer.pack();  <-- Should never pack the source viewer - screws up the last saved position
                        sourceViewer.show();
                        sourceViewer.setProblem(problemModel);
                        //sourceViewer.setCode("", JavaLanguage.JAVA_LANGUAGE);
                        sourceViewer.setCoderComponent(component);
                        sourceViewer.refreshStatement();
                    } else {
                        throw new IllegalStateException("Missing statement for problem " + problemModel);
                    }
                }

                public void updateProblemModelReadOnly(ProblemModel problemModel) {
                }
            });
        }
        viewCodeButton.setVisible(visible);
    }

    private JComponent createViewCodeButton(final long componentId) {
        JButton button = new JButton("View Code");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (contestApplet != null) {
                    contestApplet.viewCodeRequest(coderName, componentId, roundId);
                }
            }
        });
        return button;
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

    private static JComponent createLabel(String text) {
        return createLabel(text, Common.PT_COLOR);
    }

    private static JComponent createLabel(String text, Color foreground) {
        JComponent label = new JLabel(text, JLabel.CENTER);
        label.setForeground(foreground);
        return label;
    }

    private static JComponent createProblemLabel(String text) {
        return createLabel(text, Color.white);
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

    public static void showFrame(ContestApplet contestApplet, RoundStatsResponse roundStatsResponse, Coder coder, int roundId) {
        JFrame frame = new RoundStatsFrame(contestApplet, roundStatsResponse, coder, roundId);
        if (contestApplet != null) {
            Common.setLocationRelativeTo(contestApplet.getCurrentFrame(), frame);
        }
        frame.show();
    }

    /*
    public static void main(String[] args) {
        RoundStatsProblem[] problems={
            new RoundStatsProblem("Chess", 190.54, 250, "Passed", "9 min 32 sec", 0),
            new RoundStatsProblem("Checkers", 0, 500, "Challenged", "16 min 05 sec", 1),
            new RoundStatsProblem("Reversi", 690.54, 1000, "Passed", "39 min 21 sec", 2),
        };
        RoundStatsResponse roundStatsResponse=new RoundStatsResponse(1, "WL1", "George", problems);
        showFrame(null, roundStatsResponse, null);
    }
    */

}
