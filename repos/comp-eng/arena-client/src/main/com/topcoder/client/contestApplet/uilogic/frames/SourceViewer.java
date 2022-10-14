package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.html.HTMLDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.editors.Standard.StandardEditorPanel;
import com.topcoder.client.contestApplet.uilogic.views.ChallengeViewLogic;
import com.topcoder.client.contestApplet.uilogic.views.ViewerLogic;
import com.topcoder.client.contestApplet.uilogic.views.SourceViewerListener;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.render.ProblemRenderer;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.ConfigManagerException;
import com.topcoder.util.syntaxhighlighter.ConfigurationException;
import com.topcoder.util.syntaxhighlighter.HtmlOutput;
import com.topcoder.util.syntaxhighlighter.SyntaxHighlighter;
import com.topcoder.util.syntaxhighlighter.TextStyle;

public class SourceViewer implements Observer, FrameLogic, ViewerLogic {
    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.sourceViewer.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.sourceViewer.size";
    private static final String DIVIDERLOC = "com.topcoder.jmaContestApplet.frames.sourceViewer.dividerloc";
    private static final int TAB_COUNT = 20;

    private final UIComponent problemPane;
    private final UIComponent code;
    private final UIComponent status;
    private final UIComponent frame;
    private final ContestApplet contestApplet;
    private String writer;
    private CoderComponent coderComponent;
    private SyntaxHighlighter highlighter;

    private boolean enabled = true;
    private boolean showChallengeSucceeded = true;

    private UIComponent splitToggleButton;
    private UIComponent problemScroll;
    private UIComponent challengeButton = null;
    private UIComponent findButton = null;
    private SourceFindDialog findDialog;
    private SourceViewerListener challengePanel = null;

    private final boolean challengeable;
    private ProblemModel problem;
    private UIComponent splitPane;
    private UIPage page;
    
    private String oldText;
    private Language oldLanguage;
    private String oldHtml;

    /**
     * <p>
     * this is the problem view state table
     * all the pending problem view will be recorded here
     * and it will send <code>CloseProblemRequest</code>
     * </p>
     */
    public static final Set PROBLEM_STATE = new HashSet();
    
    private final LocalPreferences pref = LocalPreferences.getInstance();

    public SourceViewer(ContestApplet ca) {
        this(ca, false);
    }

    public SourceViewer(ContestApplet ca, boolean chlge) {
        this(ca, chlge, false);
    }

    public SourceViewer(ContestApplet ca, boolean chlge, boolean allowCopy) {
        ca.closeOtherCodingViewingFrame(this);

        this.contestApplet = ca;
        this.page = ca.getCurrentUIManager().getUIPage("source_viewer", true);
        frame = page.getComponent("root_frame");
        if (allowCopy) {
            code = page.getComponent("copy_code_pane");
            page.getComponent("code_pane").setProperty("Visible", Boolean.FALSE);
        } else {
            code = page.getComponent("code_pane");
            page.getComponent("copy_code_pane").setProperty("Visible", Boolean.FALSE);
        }
        problemPane = page.getComponent("problem_pane");
        status = page.getComponent("challenge_status");
        Component mouseless = (Component) status.getEventSource();
        MouseListener[] listeners = mouseless.getMouseListeners();
        for (int i=0;i<listeners.length;++i) {
            mouseless.removeMouseListener(listeners[i]);
        }
        //        setSize(575, 350);
        challengeable = chlge;

        create();

        // set the placement
        //        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //        setLocation(screenSize.width/2 - getSize().width/2,
        //                screenSize.height/2 - getSize().height/2);
        
        // POPS - 12/22/2001 - added new windowclosing listener to save the sizes to the local preferences
        //this.addWindowListener(new wl("windowClosing", "closeCodingWindow", this));
        frame.addEventListener("window", new UIWindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeWindow();
                }
            });
        splitToggleButton = page.getComponent("problem_split_toggle_button");
        splitToggleButton.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    splitToggle();
                }
            });
    }

    private void splitToggle() {
        if (((Integer) splitPane.getProperty("Orientation")).intValue() == JSplitPane.VERTICAL_SPLIT) {
            splitPane.setProperty("Orientation", new Integer(JSplitPane.HORIZONTAL_SPLIT));
            splitToggleButton.setProperty("Enabled", Boolean.FALSE);
        } else {
            splitPane.setProperty("Orientation", new Integer(JSplitPane.VERTICAL_SPLIT));
            splitToggleButton.setProperty("Enabled", Boolean.TRUE);
        }
    }

    public void dispose() {
        frame.performAction("dispose");
    }

    public void hide() {
        frame.performAction("hide");
    }

    public void show() {
        frame.performAction("show");
    }

    public UIComponent getFrame() {
        return frame;
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            if(challengeable) {
                challengeButton.setProperty("Enabled", Boolean.TRUE);
            }
        } else {
            if(challengeable) {
                challengeButton.setProperty("Enabled", Boolean.FALSE);
            }
        }
    }

    public void setChallengeable(boolean newval) {
        if (challengeButton != null) {
            //
            // disable/enable the button if it is already created
            //
            challengeButton.setProperty("Enabled", Boolean.valueOf(newval));

            //
            // TODO: if this problem was challengeable and is now NOT challengeable, it
            // would be nice to popup a dialog which tells the coder viewing the
            // source to stop looking at it
            //
        }
    }

    public void setTitle(String title) {
        frame.setProperty("Title", title);
    }

    public void clear() {
        // Do NOT clear if the source viewer is visible
        // If it's visible - the clear call is usually an update
        // to the source which will be handled in the setCode
        // below.  Without this - you'd get a refresh that
        // forces the caret positions back up to the top
        // when the source hasn't changed...
        if(((Boolean) frame.getProperty("Visible")).booleanValue()) return;

        problemPane.setProperty("Text", "");
        ((JScrollBar) problemScroll.getProperty("VerticalScrollBar")).setValue(0);
        code.setProperty("Text", "");
        code.performAction("revalidate");
        oldText = null;
        oldLanguage = null;
        oldHtml = null;
    }

    public void setProblem(ProblemModel problem) {
        //        System.out.println("SourceViewer, setting problem: " + problem);
        this.problem = problem;
    }

    public void setCode(String text, Language language) {
        String codeText = "";
        try {
            ConfigManager configManager = pref.getConfigManager();
            if (!configManager.existsNamespace(SyntaxHighlighter.DEFAULT_NAMESPACE)) {
                URL url = StandardEditorPanel.class.getResource("/syntaxhighlighter/config.xml");
                configManager.add(url);
            }
            
            highlighter = new SyntaxHighlighter();
            String fontName = pref.getFont(LocalPreferences.CHALSRCFONT);
            int fontStyle = 0;
            int fontSize = pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE);
            for (int i=0; i<highlighter.getLanguages().length; i++) {
                TextStyle[] styles = highlighter.getLanguages()[i].getStyles();
                for (int j=0; j<styles.length; j++) {
                    if (styles[j].getName().equals("KEYWORD_STYLE")) {
                        styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXKEYWORDS));
                        fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, "0"));
                    } else if (styles[j].getName().equals("BLOCK_STYLE")) {
                        styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXCOMMENTS));
                        fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, "0"));
                    } else if (styles[j].getName().equals("LITERAL_STYLE")) {
                        styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXLITERALS));
                        fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, "0"));
                    } else if (styles[j].getName().equals("DEFAULT_STYLE")) {
                        styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXDEFAULT));
                        fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, "0"));
                    }
                    styles[j].setBGColor(pref.getColor(LocalPreferences.CHALSRCBACK));
                    styles[j].setFont(new Font(fontName, fontStyle, fontSize));
                    
                }
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ConfigManagerException e) {
            e.printStackTrace();
        }

        try {
        
            if (pref.isViewerSyntaxHighlight() && text != null && !text.trim().equals("")) {
                HtmlOutput htmlOutput = new HtmlOutput(pref.getTabSize());
                try {
                    highlighter.highlightText(text, language.getName(), htmlOutput);
                    codeText = htmlOutput.getText();
                } catch (Exception e) {
                    codeText = HtmlOutput.span(HtmlOutput.convertStringToHtml(text),
                                               new Font(pref.getFont(LocalPreferences.CHALSRCFONT),
                                                        0, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)),
                                               pref.getColor(LocalPreferences.CHALSRCFORE),
                                               pref.getColor(LocalPreferences.CHALSRCBACK));
                }
            } else {
                codeText = HtmlOutput.span(HtmlOutput.convertStringToHtml(text),
                                           new Font(pref.getFont(LocalPreferences.CHALSRCFONT),
                                                    0, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)),
                                           pref.getColor(LocalPreferences.CHALSRCFORE),
                                           pref.getColor(LocalPreferences.CHALSRCBACK));
            }

        } catch(StackOverflowError ex) {
            //stupid syntax highlighter not being a proper state machine or parser
            codeText = HtmlOutput.span(HtmlOutput.convertStringToHtml(text),
                                       new Font(pref.getFont(LocalPreferences.CHALSRCFONT),
                                                0, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)),
                                       pref.getColor(LocalPreferences.CHALSRCFORE),
                                       pref.getColor(LocalPreferences.CHALSRCBACK));
        }
        
        // Only set the text if it changed
        if (oldText == null || !oldText.equals(codeText) || oldLanguage == null || !oldLanguage.equals(language)) {
                //for java 1.4, needs a valid HTML doc
                //codeText = "<html><body text=\"#ffffff\">sadf<font color='blue'>test</font><font style=\"color: blue\">asdf</font></body></html>";
            oldText = codeText;
            oldLanguage = language;
            //for java 1.4, needs a valid HTML doc
            //codeText = "<html><body text=\"#ffffff\">sadf<font color='blue'>test</font><font style=\"color: blue\">asdf</font></body></html>";
            code.setProperty("Text", codeText);
            code.setProperty("CaretPosition", new Integer(0));
            code.performAction("revalidate");
        }
        
        refreshStatement();
        
        findDialog.setText(text);
        findDialog.setSyntaxHighlight(pref.isViewerSyntaxHighlight());
    }

        
        
    public void refreshStatement() {
        Language language = oldLanguage == null ? JavaLanguage.JAVA_LANGUAGE : oldLanguage;
        ProblemRenderer problemRenderer = new ProblemRenderer(problem.getProblem());
        problemRenderer.setForegroundColor(pref.getColor(LocalPreferences.PROBLEMFORE));
        problemRenderer.setBackgroundColor(pref.getColor(LocalPreferences.PROBLEMBACK));
        String html = "";
        try {
            html = problemRenderer.toHTML(language);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (oldHtml == null || !oldHtml.equals(html)) {
            problemPane.setProperty("Text", html);
            problemPane.setProperty("CaretPosition", new Integer(0));
            oldHtml = html;
        }
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setCoderComponent(CoderComponent coderComponent) {
        this.coderComponent = coderComponent;
        setChallengeable(false);
        
        if (coderComponent.getStatus().intValue() == ContestConstants.CHALLENGE_SUCCEEDED) {
            status.setProperty("Text", "This problem has been successfully challenged.");
        } else {
            status.setProperty("Text", "");
        }
    }

    public void setPanel(SourceViewerListener c) {
        challengePanel = c;
    }

    private void create() {
        ((JScrollBar) page.getComponent("source_scroll_pane").getProperty("verticalscrollbar")).setUnitIncrement(13);
        problemScroll = page.getComponent("problem_description_scroll_pane");
        if (!challengeable) {
            page.getComponent("button_panel").setProperty("Visible", Boolean.FALSE);
        } else {
            createButtonPanel();
        }

        splitPane = page.getComponent("split_pane");
        pref.addSaveObserver(this);

        ((HTMLDocument) problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.CHALPROBFONT) + ";}");
        ((HTMLDocument) problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFONTSIZE) + "pt;}");
        
        ((HTMLDocument) problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.CHALPROBFIXEDFONT) + ";}");
        ((HTMLDocument) problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE) + "pt;}");

        code.setProperty("Background", pref.getColor(LocalPreferences.CHALSRCBACK));
        code.setProperty("Foreground", pref.getColor(LocalPreferences.CHALSRCFORE));
        code.setProperty("DisabledTextColor", pref.getColor(LocalPreferences.CHALSRCFORE));

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

        Dimension frameSize = pref.getSize(FRAMESIZE);
        if (frameSize == null) frameSize = new Dimension(760, 360);
        frameSize = Common.adjustWindowSize(frameLocation, frameSize);
        frame.setProperty("Size", frameSize);

        // Restore the divider location
        String dividerLocation = pref.getProperty(DIVIDERLOC);
        if (dividerLocation == null) dividerLocation = "80";
        splitPane.setProperty("DividerLocation", Integer.valueOf(dividerLocation));

        findDialog = new SourceFindDialog(contestApplet, page.getComponent("source_code_panel"), code);
        frame.addEventListener("Key", new KeyHandler());
        page.getComponent("source_code_panel").addEventListener("Key", new KeyHandler());
        splitPane.addEventListener("Key", new KeyHandler());
    }

    private void createButtonPanel() {
        challengeButton = page.getComponent("challenge_button");
        challengeButton.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    challengeButtonEvent();
                }
            });

        findButton = page.getComponent("find_button");
        findButton.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    findButtonEvent();
                }
            });
    }

    /**
     * called when the challenge button is pressed.
     */

    private void challengeButtonEvent() {
        //
        // do the challenge.  Use the challenge table to actually do it since it is
        // easier than implementing all of the ChallengeView interface
        //
        if (challengePanel instanceof ChallengeViewLogic) {
            ((ChallengeViewLogic) challengePanel).doChallenge(writer, coderComponent, this);
        }
    }

    private void findButtonEvent() {
        //popup find dialog
        findDialog.show();
    }
    // POPS - 12/22/2001 - added window close event to save sizes
    public void closeWindow() {
        if(!enabled)
            return;
        
        pref.removeSaveObserver(this);
        challengePanel.sourceViewerClosing();

        if (coderComponent != null && writer != null) {
            long problemID = coderComponent.getComponent().getID().longValue();
            //we need to remove the element,because it will send the CloseProblemRequest
            SourceViewer.PROBLEM_STATE.remove(problemID+"_"+writer);
            contestApplet.getRequester().requestCloseComponent(problemID, writer);
        }

        pref.setLocation(FRAMELOCATION, (Point) frame.getProperty("Location"));
        pref.setSize(FRAMESIZE, (Dimension) frame.getProperty("Size"));
        pref.setProperty(DIVIDERLOC, splitPane.getProperty("DividerLocation").toString());

        // Try to save the sizes - catch all errors (we probably don't have authority to save)
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public void notifyChallengeSucceeded(String writer, String value) {
        if (showChallengeSucceeded) {
            status.setProperty("Text", "This problem has been successfully challenged.");
            showChallengeSucceeded = false;
        }
    }

    public void doChallengeRequest(ArrayList info) {
        ComponentChallengeData ccd = coderComponent.getComponent().getComponentChallengeData();
        if (((Boolean) info.get(0)).booleanValue()) {

            String challengeHandle = writer;

            contestApplet.setCurrentFrame((JFrame) frame.getEventSource());
            contestApplet.getInterFrame().showMessage(
                    "Challenging...",
                    contestApplet.getCurrentFrame(),
                    ContestConstants.CHALLENGE
            );
            contestApplet.getRequester().requestChallenge(
                    challengeHandle,
                    ccd.getComponentID(),
                    (ArrayList) info.get(1)
            );
        } else {
            //System.out.println("cancel button");
        }

        //
        // do the challenge.  Use the challenge table to actually do it since it is
        // easier than implementing all of the ChallengeView interface
        //
        if (challengePanel instanceof ChallengeViewLogic) {
            ((ChallengeViewLogic)challengePanel).setOldArgs((ArrayList)info.get(1), ccd.getComponentID());
        }
    }

    // AdamSelene - 6/06/2002 - added to fix stubborn color settings.
    public void update(Observable o, Object arg) {
        try {
            // This function will update the color/font scheme as notified by localPref
            //            problem.setFont(new Font(localPref.getFont(LocalPreferences.CHALPROBFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.CHALPROBFONTSIZE)));
            code.setProperty("Font", new Font(pref.getFont(LocalPreferences.CHALSRCFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)));

            //            problem.setBackground(localPref.getColor(LocalPreferences.CHALPROBBACK));
            //            problem.setForeground(localPref.getColor(LocalPreferences.CHALPROBFORE));
            //            problem.setDisabledTextColor(localPref.getColor(LocalPreferences.CHALPROBFORE));

            code.setProperty("Background", pref.getColor(LocalPreferences.CHALSRCBACK));
            code.setProperty("Foreground", pref.getColor(LocalPreferences.CHALSRCFORE));
            code.setProperty("DisabledTextColor", pref.getColor(LocalPreferences.CHALSRCFORE));
        } catch (NullPointerException e) {
        } // if the window panels had not been instantiated/have been destroyed
        // TODO - should never treat exceptions as normal occurences
    }

    private class KeyHandler extends UIKeyAdapter {

        public void keyPressed(KeyEvent evt) {
            switch (evt.getKeyCode()) {

            case KeyEvent.VK_F:
                {
                    if (evt.isAltDown()) {
                        evt.consume();
                        findButtonEvent();
                        return;
                    }
                    break;
                }

            case KeyEvent.VK_F3:
                {
                    evt.consume();
                    findDialog.findAgain();
                    return;
                }
            }
        }
    }

    private TabSet getTabSet(int count) {
        
        String fontName = pref.getFont(LocalPreferences.EDSTDFONT);
        int fontStyle = 0;
        int fontSize = pref.getFontSize(LocalPreferences.EDSTDFONTSIZE);
        Font f = new Font(fontName, fontStyle, fontSize);
        int spaceSize = StyleContext.getDefaultStyleContext().getFontMetrics(f).stringWidth(" ");

        TabStop[] ts = new TabStop[TAB_COUNT];
        for(int i = 0; i < TAB_COUNT; i++) {
            ts[i] = new TabStop(count*spaceSize*(i+1), TabStop.ALIGN_LEFT, TabStop.LEAD_DOTS);
        }
        return new TabSet(ts);
    }
}
