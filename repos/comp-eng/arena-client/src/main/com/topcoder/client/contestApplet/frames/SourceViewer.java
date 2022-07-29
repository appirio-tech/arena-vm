package com.topcoder.client.contestApplet.frames;

/*
* SourceViewer.java
*
* Created on July 10, 2000, 4:08 PM
*/

//import java.util.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.html.HTMLDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.editors.Standard.StandardEditorPanel;
import com.topcoder.client.contestApplet.panels.table.BaseAlgoSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.ChallengeTablePanel;
import com.topcoder.client.contestApplet.panels.table.DivSummaryTablePanel;
import com.topcoder.client.contestApplet.widgets.MouseLessEditorPane;
import com.topcoder.client.contestApplet.widgets.MouseLessTextArea;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.render.ProblemRenderer;
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

/**
 *
 * @author Alex Roman
 * @version
 */

public final class SourceViewer extends JFrame implements Observer {

    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.sourceViewer.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.sourceViewer.size";
    private static final String DIVIDERLOC = "com.topcoder.jmaContestApplet.frames.sourceViewer.dividerloc";


    // Problem panel variables
    // SourceCode panel variables
    private final JEditorPane problemPane = new JEditorPane("text/html", "");
//    private MouseLessTextArea problem = new MouseLessTextArea("");
    private JEditorPane code = new MouseLessEditorPane("text/html", "");
    private final MouseLessTextArea status = new MouseLessTextArea("");

    private final LocalPreferences pref = LocalPreferences.getInstance();

    private final ContestApplet contestApplet;
    private String writer;
    private CoderComponent coderComponent;
    private SyntaxHighlighter highlighter;

    private boolean enabled = true;
    private boolean showChallengeSucceeded = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            if(challengeable) {
                challengeButton.setEnabled(true);
            }
        } else {
            if(challengeable) {
                challengeButton.setEnabled(false);
            }
        }
    }

    private JScrollPane problemScroll;

    //
    // Added 6/25/2002 by schveiguy (schveiguy@yahoo.com)
    // new members to support challenge button
    //

    /**
     * button object for challenging.
     *
     * We keep this around because it can be disabled or enabled at a later
     * time.
     */
    private JButton challengeButton = null;
    private JButton findButton = null;

    private SourceFindDialog findDialog;

    /**
     * object to use for challenging
     */
    private SourceViewerListener challengePanel = null;

    /**
     * determines whether this sourceviewer should be able to challenge
     * problems.
     */
    private final boolean challengeable;
    private ProblemModel problem;
    private JSplitPane splitPane;
    private boolean allowCopy;
    private String oldText;
    private Language oldLanguage;
    private String oldHtml;

    //
    // End additions by schveiguy 6/25/2002
    //

	// Pops - 1/6/2004 - added static link to the other viewer
	// to force a close of it if a new instance has been opened
	// (fixes challenge issue related to ChallengeTablePanel)
	private static SourceViewer otherViewer = null;
	private static Object viewerLock = new Object();
    /**
     * Class constructor
     */
    
    public SourceViewer(ContestApplet ca, boolean chlge) {
        this(ca, chlge, false);
    }
    public SourceViewer(ContestApplet ca, boolean chlge, boolean allowCopy) {
        super("Source Code Viewer");

		synchronized(viewerLock) {
			if(otherViewer!=null && otherViewer!=this) {
				otherViewer.closeWindow();
				otherViewer.dispose();
			}

			otherViewer=this;
		}
		this.allowCopy = allowCopy;
        this.contestApplet = ca;
        getContentPane().setBackground(Common.BG_COLOR);
//        setSize(575, 350);
        challengeable = chlge;

        create();

        // set the placement
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setLocation(screenSize.width/2 - getSize().width/2,
//                screenSize.height/2 - getSize().height/2);
        
        // POPS - 12/22/2001 - added new windowclosing listener to save the sizes to the local preferences
        //this.addWindowListener(new wl("windowClosing", "closeCodingWindow", this));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });
    }

    SourceViewer(ContestApplet ca) {
        this(ca, false);
    }

    //
    // Added 6/25/2002 by schveiguy (schveiguy@yahoo.com)
    //

    /**
     * Sets whether this problem can be challenged
     */

    public void setChallengeable(boolean newval) {
        if (challengeButton != null) {
            //
            // disable/enable the button if it is already created
            //
            challengeButton.setEnabled(newval);

            //
            // TODO: if this problem was challengeable and is now NOT challengeable, it
            // would be nice to popup a dialog which tells the coder viewing the
            // source to stop looking at it
            //
        }
    }

    public void clear() {
    	// Do NOT clear if the source viewer is visible
    	// If it's visible - the clear call is usually an update
    	// to the source which will be handled in the setCode
    	// below.  Without this - you'd get a refresh that
    	// forces the caret positions back up to the top
    	// when the source hasn't changed...
    	if(this.isVisible()) return;

        problemPane.setText("");
        problemScroll.getVerticalScrollBar().setValue(0);
        code.setText("");
        code.revalidate();
        oldText = null;
        oldLanguage = null;
        oldHtml = null;
    }

    public void setProblem(ProblemModel problem) {
//        System.out.println("SourceViewer, setting problem: " + problem);
        this.problem = problem;
    }

    public void setCode(String text, Language language) {
        //System.out.println(hashCode() + " SourceViewer, setting code: " + text);

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
                code.setContentType("text/html");
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
                code.setContentType("text/plain");
                codeText = text;
            }

        } catch(StackOverflowError ex) {
            //stupid syntax highlighter not being a proper state machine or parser
            codeText = HtmlOutput.span(HtmlOutput.convertStringToHtml(text),
                        new Font(pref.getFont(LocalPreferences.CHALSRCFONT),
                                0, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)),
                        pref.getColor(LocalPreferences.CHALSRCFORE),
                        pref.getColor(LocalPreferences.CHALSRCBACK));
        }
        
        //We want to avoid refresh if the code has not change or the problem statement has not change. 
        
        // Only set the text if it changed
        if (oldText == null || !oldText.equals(codeText) || oldLanguage == null || !oldLanguage.equals(language)) {
                //for java 1.4, needs a valid HTML doc
                //codeText = "<html><body text=\"#ffffff\">sadf<font color='blue'>test</font><font style=\"color: blue\">asdf</font></body></html>";
            code.setText(codeText);
            code.setCaretPosition(0);
            oldText = codeText;
            oldLanguage = language;
	        code.revalidate();
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
            problemPane.setText(html);
            problemPane.setCaretPosition(0);
            oldHtml = html;
        }
    }

    
    private static final int TAB_COUNT = 20;

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setCoderComponent(CoderComponent coderComponent) {
        this.coderComponent = coderComponent;
        setChallengeable(false);
        
        if (coderComponent.getStatus().intValue() == ContestConstants.CHALLENGE_SUCCEEDED) {
            status.setText("This problem has been successfully challenged.");
        } else {
            status.setText("");
        }
    }

    /**
     * Sets the panel which should be used for challenges.
     */

    public void setPanel(SourceViewerListener c) {
        challengePanel = c;
    }
    
    /**
     * Create the room
     */
    private void create() {
        if (allowCopy) {
            code = new JEditorPane("text/html","");
            code.setEditable(false);
        }
        GridBagConstraints gbc = Common.getDefaultConstraints();

        JPanel noWrapPanel = new JPanel(new BorderLayout()); 
        noWrapPanel.add(code);
        noWrapPanel.setBackground(Common.MB_COLOR);
        noWrapPanel.setForeground(Common.MF_COLOR);
        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(13);
        
        // create all the panels/panes
        JPanel problemStatementPanel = Common.createMessagePanel("Problem Statement", problemPane, 0, 0, Common.BG_COLOR);
        JPanel sourceCodePanel = Common.createMessagePanel("Source Code", scrollPane, 0, 0, Common.BG_COLOR);
        
        this.problemScroll = (JScrollPane) problemStatementPanel.getComponent(0);

        //
        // Changed 6/25/2002 by schveiguy (schveiguy@yahoo.com)
        // Instead of creating a simple panel to hold the source code, it is a
        // panel with two subpanels, one holding a challenge button, and one
        // holding the source viewer.  However, if a challenge could not be
        // issued, the button should not be created.
        //
        JPanel combined;
        if (challengeable) {
            JPanel buttonPanel = createButtonPanel();
            combined = new JPanel(new BorderLayout());
            combined.add(sourceCodePanel, BorderLayout.CENTER);
            combined.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            combined = sourceCodePanel;
        }

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, problemStatementPanel, combined);

        //JScrollPane sourceCodeScrollPane = (JScrollPane) sourceCodePanel.getComponent(0);
        //JViewport sourceCodeViewport = (JViewport) sourceCodeScrollPane.getComponent(0);
        //JEditorPane sourceCodeEditorPane = (JEditorPane) sourceCodeViewport.getComponent(0);

        // set misc properties
//        splitPane.setPreferredSize(new Dimension(575, 350));
//        splitPane.setDividerLocation(150);

        pref.addSaveObserver(this);

        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.CHALPROBFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFONTSIZE) + "pt;}");
        
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.CHALPROBFIXEDFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE) + "pt;}");
        this.problemPane.setForeground(pref.getColor(LocalPreferences.CHALPROBFORE));
        this.problemPane.setBackground(pref.getColor(LocalPreferences.CHALPROBBACK));
        
//        problem.setFont(new Font(localPref.getFont(LocalPreferences.CHALPROBFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.CHALPROBFONTSIZE)));
        code.setFont(new Font(pref.getFont(LocalPreferences.CHALSRCFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)));

        problemPane.setEditable(false);

        //this.problemPane.setFont(new Font(pref.getFont(LocalPreferences.PROBLEMFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE)));
        //this.problemPane.setForeground(pref.getColor(LocalPreferences.PROBLEMFORE));
        //this.problemPane.setBackground(pref.getColor(LocalPreferences.PROBLEMBACK));

//        problemPane.setCaret(null);

//        problem.setBackground(localPref.getColor(LocalPreferences.CHALPROBBACK));
//        problem.setForeground(localPref.getColor(LocalPreferences.CHALPROBFORE));
//        problem.setDisabledTextColor(localPref.getColor(LocalPreferences.CHALPROBFORE));

        code.setBackground(pref.getColor(LocalPreferences.CHALSRCBACK));
        code.setForeground(pref.getColor(LocalPreferences.CHALSRCFORE));
        code.setDisabledTextColor(pref.getColor(LocalPreferences.CHALSRCFORE));
        getContentPane().setBackground(Common.WPB_COLOR);
        getContentPane().setLayout(new GridBagLayout());
        gbc.insets = new Insets(15, 15, 15, 15);
        getContentPane().add(splitPane, gbc);

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

        // Restore the divider location
        String dividerLocation = pref.getProperty(DIVIDERLOC);
        if (dividerLocation == null) dividerLocation = "80";
        splitPane.setDividerLocation(Integer.parseInt(dividerLocation));

        findDialog = new SourceFindDialog(sourceCodePanel, code);

        this.addKeyListener(new KeyHandler());
        sourceCodePanel.addKeyListener(new KeyHandler());
        splitPane.addKeyListener(new KeyHandler());
        //pack();
    }

    //
    // Added 6/25/2002 by schveiguy (schveiguy@yahoo.com)
    //

    /**
     * creates a button panel with one button, on the left hand side.
     */

    private JPanel createButtonPanel() {
        challengeButton = new JButton("Challenge");
        challengeButton.setForeground(Common.FG_COLOR);
        challengeButton.setBackground(Color.black);
        challengeButton.setFocusPainted(false);
        //
        // TODO: use this instead, when the graphic becomes available:
        //
        // challengeButton = Common.getImageButton("imagename.gif", ca);
        //
        challengeButton.setToolTipText("Challenge this code");
        challengeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                challengeButtonEvent();
            }
        });

        findButton = new JButton("Find");
        findButton.setForeground(Common.FG_COLOR);
        findButton.setBackground(Color.black);
        findButton.setFocusPainted(false);

        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findButtonEvent();
            }
        });
        
        status.setDisabledTextColor(Color.red);
        status.setFont(new Font("", (Font.BOLD | Font.ITALIC), 13));
        status.setBackground(Common.THB_COLOR);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 5, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(challengeButton, buttonPanel, gbc, 0, 0, 1, 1, 0, 0);
        //Common.insertInPanel(findButton, buttonPanel, gbc, 1, 0, 1, 1, .9, 0);
        Common.insertInPanel(findButton, buttonPanel, gbc, 1, 0, 1, 1, 0, 0);
        Common.insertInPanel(status, buttonPanel, gbc, 2, 0, 1, 1, .9, 0);

        return buttonPanel;
    }

    //
    // Added 6/25/2002 by schveiguy (schveiguy@yahoo.com)
    //

    /**
     * called when the challenge button is pressed.
     */

    private void challengeButtonEvent() {
        //
        // do the challenge.  Use the challenge table to actually do it since it is
        // easier than implementing all of the ChallengeView interface
        //
        if (challengePanel != null) {
            if(challengePanel instanceof BaseAlgoSummaryTablePanel)
            {
                ((BaseAlgoSummaryTablePanel)challengePanel).doChallenge(writer, coderComponent, this);
            }
        }
    }

    private void findButtonEvent() {
        //popup find dialog
        findDialog.show();
    }

    // POPS - 12/22/2001 - added window close event to save sizes
    private void closeWindow() {
        if(!enabled)
            return;
        
        pref.removeSaveObserver(this);
        if (challengePanel != null) {
           challengePanel.sourceViewerClosing();
        }
        if (coderComponent != null && writer != null) {
            contestApplet.getRequester().requestCloseComponent(coderComponent.getComponent().getID().longValue(), writer);
        }
        pref.setLocation(FRAMELOCATION, this.getLocation());
        pref.setSize(FRAMESIZE, getSize());
        pref.setProperty(DIVIDERLOC, String.valueOf(splitPane.getDividerLocation()));

        // Try to save the sizes - catch all errors (we probably don't have authority to save)
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public void notifyChallengeSucceeded(String writer, String value) {
        if (showChallengeSucceeded) {
            status.setText("This problem has been successfully challenged.");
            showChallengeSucceeded = false;
        }
    }

    public void doChallengeRequest(ArrayList info) {
        ComponentChallengeData ccd = coderComponent.getComponent().getComponentChallengeData();
        if (((Boolean) info.get(0)).booleanValue()) {

            String challengeHandle = writer;

            contestApplet.setCurrentFrame(this);
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
        if (challengePanel != null) {
            if(challengePanel instanceof ChallengeTablePanel)
            {
                ((ChallengeTablePanel)challengePanel).setOldArgs((ArrayList)info.get(1), ccd.getComponentID());
            }
            else if(challengePanel instanceof DivSummaryTablePanel)
            {
                ((DivSummaryTablePanel)challengePanel).setOldArgs((ArrayList)info.get(1), ccd.getComponentID());
            }
        }
    }

    // AdamSelene - 6/06/2002 - added to fix stubborn color settings.
    public void update(Observable o, Object arg) {
        try {
            // This function will update the color/font scheme as notified by localPref
//            problem.setFont(new Font(localPref.getFont(LocalPreferences.CHALPROBFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.CHALPROBFONTSIZE)));
            code.setFont(new Font(pref.getFont(LocalPreferences.CHALSRCFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)));

//            problem.setBackground(localPref.getColor(LocalPreferences.CHALPROBBACK));
//            problem.setForeground(localPref.getColor(LocalPreferences.CHALPROBFORE));
//            problem.setDisabledTextColor(localPref.getColor(LocalPreferences.CHALPROBFORE));

            code.setBackground(pref.getColor(LocalPreferences.CHALSRCBACK));
            code.setForeground(pref.getColor(LocalPreferences.CHALSRCFORE));
            code.setDisabledTextColor(pref.getColor(LocalPreferences.CHALSRCFORE));
        } catch (NullPointerException e) {
        } // if the window panels had not been instantiated/have been destroyed
        // TODO - should never treat exceptions as normal occurences
    }

    private class KeyHandler extends KeyAdapter {

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
    
    
    public interface SourceViewerListener {
        public void sourceViewerClosing();
    }
}
