package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.text.html.HTMLDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.ProblemModel.Listener;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.render.ProblemRenderer;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;

public class StatementViewer implements FrameLogic {
    private ContestApplet contestApplet;
    private UIComponent frame;
    private UIPage page;
    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.statementViewer.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.statementViewer.size";
    private UIComponent problemPane;
    private final LocalPreferences pref = LocalPreferences.getInstance();
    private boolean enabled = true;
    private UIComponent problemScroll;
    private UIComponent registerButton = null;
    private ProblemModel problem;
    private Listener problemListener;
    private PhaseListener phaseListener;

    public void setPanelEnabled(boolean on) {
        enabled = on;
        registerButton.setProperty("Enabled", Boolean.valueOf(enabled && isRegistrationOpen()));
    }

    private boolean isRegistrationOpen() {
        return false;
    }

    public UIComponent getFrame() {
        return frame;
    }

    public StatementViewer(ContestApplet ca) {
        this.contestApplet = ca;
        page = ca.getCurrentUIManager().getUIPage("statement_viewer", true);
        frame = page.getComponent("root_frame");
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
        create();
        frame.addEventListener("Window", new UIWindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeWindow();
                }
            });
    }

    private void create() {
        problemPane = page.getComponent("problem_statement_pane");
        problemScroll = page.getComponent("problem_statement_scroll_pane");
        registerButton = page.getComponent("register_button");

        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.CHALPROBFONT) + ";}");
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFONTSIZE) + "pt;}");
        
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.CHALPROBFIXEDFONT) + ";}");
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE) + "pt;}");
        this.problemPane.setProperty("Foreground", pref.getColor(LocalPreferences.CHALPROBFORE));
        this.problemPane.setProperty("Background", pref.getColor(LocalPreferences.CHALPROBBACK));

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
        frame.setProperty("Location", frameLocation);

        // Restore the frame size (adjusted for the constraints of the window)
        Dimension frameSize = pref.getSize(FRAMESIZE);
        if (frameSize == null) frameSize = (Dimension) frame.getProperty("Size");
        frameSize = Common.adjustWindowSize(frameLocation, frameSize);
        frame.setProperty("Size", frameSize);

        registerButton.addEventListener("Action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registerButtonEvent();
                }
            });
    }

    public void clear() {
        problemPane.setProperty("Text", "");
        ((JScrollBar) problemScroll.getProperty("VerticalScrollBar")).setValue(0);
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
        frame.setProperty("Title", problem.getName() + " statement "+ (problem.hasProblemStatement() ? "" : "(loading...)"));
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
        registerButton.setProperty("Enabled", Boolean.valueOf(phase >= ContestConstants.REGISTRATION_PHASE && phase <= ContestConstants.CODING_PHASE));
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
        problemPane.setProperty("Text", html);
        problemPane.setProperty("CaretPosition", new Integer(0));
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

    private void registerButtonEvent() {
        this.contestApplet.getRequester().requestRegisterEventInfo(problem.getRound().getRoundID().longValue());
    }

    private void closeWindow() {
        removeListeners();
        clear();
        if(!enabled)
            return;
        
        pref.setLocation(FRAMELOCATION, (Point) frame.getProperty("Location"));
        pref.setSize(FRAMESIZE, (Dimension) frame.getProperty("Size"));
        // Try to save the sizes - catch all errors (we probably don't have authority to save)
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void setVisible(boolean on) {
        frame.setProperty("Visible", Boolean.valueOf(on));
    }
}
