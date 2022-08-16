/*
 * StatementViewer
 * 
 * Created 08/13/2007
 */
package com.topcoder.client.contestApplet.frames;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.ProblemModel.Listener;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.render.ProblemRenderer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: StatementViewer.java 72032 2008-07-30 06:28:49Z qliu $
 */
public final class StatementViewer extends JFrame  {

    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.statementViewer.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.statementViewer.size";
    private final JEditorPane problemPane = new JEditorPane("text/html", "");
    private final LocalPreferences pref = LocalPreferences.getInstance();

    private final ContestApplet contestApplet;
    private boolean enabled = true;
    private JScrollPane problemScroll;
    private JButton registerButton = null;
    private ProblemModel problem;
    private Listener problemListener;
    private PhaseListener phaseListener;
    

    public void setPanelEnabled(boolean on) {
        enabled = on;
        registerButton.setEnabled(enabled && isRegistrationOpen());
    }

    private boolean isRegistrationOpen() {
        return false;
    }



    public StatementViewer(ContestApplet ca) {
        super("Statement Viewer");
        this.contestApplet = ca;
        this.problemListener = new Listener() {
            public void updateProblemModelReadOnly(ProblemModel problemModel) {
            }
        
            public void updateProblemModel(ProblemModel problemModel) {
                refreshView();
            }
        };
        this.phaseListener = new PhaseListener(){
            public void updateSystestProgress(int completed, int total, RoundModel roundModel) {
            }
            public void phaseEvent(int phase, RoundModel roundModel) {
                updateRegisterButton();
            }
            public void enableRound(RoundModel round) {
            }
        
        };
        getContentPane().setBackground(Common.BG_COLOR);
        create();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });
    }


    public void clear() {
        problemPane.setText("");
        problemScroll.getVerticalScrollBar().setValue(0);
    }

    public void setProblemStatement(ProblemModel problem) {
        if (this.problem != problem) { 
            clear();
            removeListeners();
            this.problem = problem;
            updateTitle();
            addListeners();
        }
        updateView();
        updateRegisterButton();
    }

    private void updateTitle() {
        setTitle(problem.getName() + " statement "+ (problem.hasProblemStatement() ? "" : "(loading...)"));
    }

    private void addListeners() {
        this.problem.addListener(problemListener);
        this.problem.getRound().addPhaseListener(phaseListener);
    }

    private void removeListeners() {
        if (this.problem != null) {
            this.problem.getRound().removePhaseListener(phaseListener);
            this.problem.removeListener(problemListener);
            this.problem = null;
        }
    }
    
    private void updateRegisterButton() {
        int phase = this.problem.getRound().getPhase().intValue();
        //This only works for Long Contest rounds now.
        registerButton.setEnabled(phase >= ContestConstants.REGISTRATION_PHASE && phase <= ContestConstants.CODING_PHASE);
    }

    private void updateView() {
        if (!problem.hasProblemStatement()) {
            contestApplet.getRequester().requestOpenProblemForReading(problem.getRound().getRoundID().longValue(), problem.getProblemID().longValue());
            return;
        }
        refreshView();
    }
    
    private void refreshView() {
        if (!problem.hasProblemStatement()) {
            throw new IllegalStateException("No server object for problem");
        }
        updateTitle();
        ProblemRenderer problemRenderer = new ProblemRenderer(problem.getProblem());
        problemRenderer.setForegroundColor(pref.getColor(LocalPreferences.PROBLEMFORE));
        problemRenderer.setBackgroundColor(pref.getColor(LocalPreferences.PROBLEMBACK));
        String html = "";
        try {
            html = problemRenderer.toHTML(getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        problemPane.setText(html);
        problemPane.setCaretPosition(0);
    }
    
    private Language getLanguage() {
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        Map prefs = contestApplet.getModel().getUserInfo().getPreferences();
        if (prefs.containsKey(lang)) {
            if(((Integer)prefs.get(lang)).intValue() == 0) {
                return JavaLanguage.JAVA_LANGUAGE;
            }
            return BaseLanguage.getLanguage(((Integer) prefs.get(lang)).intValue());
        } else {
            return JavaLanguage.JAVA_LANGUAGE;
        }
    }

    private void create() {
        JPanel problemStatementPanel = Common.createMessagePanel("Problem Statement", problemPane, 0, 0, Common.BG_COLOR);
        this.problemScroll = (JScrollPane) problemStatementPanel.getComponent(0);

        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.CHALPROBFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFONTSIZE) + "pt;}");
        
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.CHALPROBFIXEDFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE) + "pt;}");
        this.problemPane.setForeground(pref.getColor(LocalPreferences.CHALPROBFORE));
        this.problemPane.setBackground(pref.getColor(LocalPreferences.CHALPROBBACK));
        
        problemPane.setEditable(false);

        getContentPane().setBackground(Common.WPB_COLOR);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(problemStatementPanel, BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

        // Get the frame location
        Point frameLocation = pref.getLocation(FRAMELOCATION);
        if (frameLocation == null) {
            JFrame currFrame = contestApplet.getCurrentFrame();
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
        frameSize = Common.adjustWindowSize(frameLocation, frameSize);
        setSize(frameSize);
    }

    private JPanel createButtonPanel() {
        registerButton = new JButton("Register");
        registerButton.setForeground(Common.FG_COLOR);
        registerButton.setBackground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.setToolTipText("Register on this contest");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerButtonEvent();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);
        return buttonPanel;
    }

    private void registerButtonEvent() {
        this.contestApplet.getRequester().requestRegisterEventInfo(problem.getRound().getRoundID().longValue());
    }

    private void closeWindow() {
        removeListeners();
        clear();
        if(!enabled)
            return;
        
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
