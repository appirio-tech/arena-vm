package com.topcoder.client.contestApplet.frames;

/*
* TeamCodingFrame.java
*
* Created on November 15, 2002, 12:29 AM
*/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.panels.*;
import com.topcoder.client.contestApplet.panels.room.*;
import com.topcoder.client.contestApplet.panels.coding.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.editors.*;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestant.*;
import com.topcoder.client.render.ProblemRenderer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.problem.*;
import com.topcoder.shared.language.*;

/**
 *
 * @author Tim Bulat
 */
public final class TeamProblemFrame extends JFrame {

    // main window
    private ContestApplet parentFrame = null;

    // panels
    private CodingTimerPanel timerPanel = null;
    private TeamProblemInfoPanel problemPanel = null;

    // SourceCode panel variables
    private JEditorPane problemPane = new JEditorPane("text/html", "");
    private JScrollPane problemScroll = null;

    private JButton assignButton;

    // POPS 12/22/2001 - added keys to store sizes in local preferences
    private LocalPreferences pref = LocalPreferences.getInstance();
    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.codingframe.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.codingframe.size";

    private ProblemModel problem;
    private ProblemComponentModel component;

    public TeamProblemFrame(ContestApplet parent) {
        super("TopCoder Competition Arena - Team Problem");

        parentFrame = parent;

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeTeamProblemWindow();
            }
        });
        problemPane.addHyperlinkListener(new HyperLinkLoader(parent.getAppletContext()));
    }

    public void clear() {
        problemPane.setText("");
        problemScroll.getVerticalScrollBar().setValue(0);
    }

    public void showFrame(boolean enabled) {
        show();
    }

    public TimerPanel getTimerPanel() {
        return (timerPanel);
    }

    public void updateProblemInfo(ProblemModel info) {
        problem = info;
        updateAll();
    }


    //------------------------------------------------------------------------------
    // Create the room
    //------------------------------------------------------------------------------

    public void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        CodingTimerPanel tp = new CodingTimerPanel(parentFrame);
        this.timerPanel = tp;
        TeamProblemInfoPanel pp = new TeamProblemInfoPanel(parentFrame);
        this.problemPanel = pp;

        // create all the panels/panes
        JPanel ps = Common.createMessagePanel("Problem Statement", problemPane, 0, 0, Common.BG_COLOR);

        // set the preferred font on the problem statement, and set the color.
        this.problemPane.setFont(new Font(pref.getFont(LocalPreferences.PROBLEMFONT),
                Font.PLAIN, pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE)));
        this.problemPane.setForeground(pref.getColor(LocalPreferences.PROBLEMFORE));
        this.problemPane.setBackground(pref.getColor(LocalPreferences.PROBLEMBACK));

        // Get the scroll area
        this.problemScroll = (JScrollPane) ps.getComponent(0);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 5, 5);
        Common.insertInPanel(pp, getContentPane(), gbc, 0, 0, 2, 1, 0.0, 0.0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 5, 5, 15);
        Common.insertInPanel(tp, getContentPane(), gbc, 2, 0, 1, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 15, 5, 15);
        Common.insertInPanel(new ContestSponsorPanel(parentFrame,
                CommonData.getSponsorCodingFrameImageAddr(parentFrame.getSponsorName(), null)), getContentPane(), gbc, 0, 1, 1, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 15, 15, 15);
        Common.insertInPanel(ps, getContentPane(), gbc, 0, 2, 3, 1, 0.1, 0.1);

        pack();

        // Get the frame location
        Point frameLocation = pref.getLocation(FRAMELOCATION);
        if (frameLocation == null) {
            JFrame currFrame = parentFrame.getCurrentFrame();
            if (currFrame == null) {
                frameLocation = new Point(0, 0);
            } else {
                frameLocation = currFrame.getLocation();
            }
        }

        // Adjust the frame location (to be possible) and set the location
        frameLocation = Common.adjustWindowLocation(frameLocation);
        setLocation(frameLocation);

        // Restore the frame size (adjusted for the constraints of the window)
        Dimension frameSize = pref.getSize(FRAMESIZE);
        if (frameSize == null) frameSize = new Dimension(760, 360);
        setSize(Common.adjustWindowSize(frameLocation, frameSize));
    }

    private void updateAll() {
        ProblemRenderer problemRenderer = new ProblemRenderer(problem.getProblem());
        problemRenderer.setForegroundColor(pref.getColor(LocalPreferences.PROBLEMFORE));
        problemRenderer.setBackgroundColor(pref.getColor(LocalPreferences.PROBLEMBACK));
        String html = "";
        try {
            html = problemRenderer.toHTML(JavaLanguage.JAVA_LANGUAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String name = problem.getName();
        String methodName = "";
        DataType[] parms = new DataType[0];
        DataType rcType = new DataType("int");

        problemPane.setText(html);
        problemPane.setCaretPosition(0);
        problemPanel.updateProblemInfo(problem, JavaLanguage.JAVA_LANGUAGE.getId());
    }

    private void closeTeamProblemWindow() {
        pref.setLocation(FRAMELOCATION, this.getLocation());
        pref.setSize(FRAMESIZE, getSize());

        // Try to save the sizes - catch all errors (we probably don't have authority to save)
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
