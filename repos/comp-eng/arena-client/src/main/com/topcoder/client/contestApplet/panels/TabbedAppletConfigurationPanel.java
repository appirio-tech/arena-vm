package com.topcoder.client.contestApplet.panels;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import java.awt.Frame;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.ChatConfigurationPanel;
import com.topcoder.client.contestApplet.panels.StdEdConfigurationPanel;
import com.topcoder.client.contestApplet.panels.ProblemConfigurationPanel;
import com.topcoder.client.contestApplet.panels.ChallengeConfigurationPanel;

public final class TabbedAppletConfigurationPanel extends JPanel {

    private JTabbedPane tabbedCfgPane;
    private LocalPreferences localPref = LocalPreferences.getInstance();
    private ChatConfigurationPanel chatPanel;
    private StdEdConfigurationPanel stdEdPanel;
    private ProblemConfigurationPanel probPanel;
    private ChallengeConfigurationPanel chalPanel;
    private SummaryConfigurationPanel summPanel;
    private AdditionalFontsConfigurationPanel menuPanel;
    private HighlightConfigurationPanel highlightPanel;

    boolean changesPending = false;
    private JDialog parent;

    public TabbedAppletConfigurationPanel(JDialog iparent) {
        super(false);
        parent = iparent;
        this.setBackground(Common.BG_COLOR);

        // the brains behind the panel
        tabbedCfgPane = new JTabbedPane();

        // Create the templates
        chatPanel = new ChatConfigurationPanel(parent);
        stdEdPanel = new StdEdConfigurationPanel(parent);
        probPanel = new ProblemConfigurationPanel(parent);
        chalPanel = new ChallengeConfigurationPanel(parent);
        summPanel = new SummaryConfigurationPanel(parent);
        menuPanel = new AdditionalFontsConfigurationPanel();
        highlightPanel = new HighlightConfigurationPanel(parent);
        //panel4 = new JPanel(false);

        // Add panels to pane (title, icon, object, tooltip)
        tabbedCfgPane.addTab("Chat", null, new JScrollPane(chatPanel), "Chat colors.");
        tabbedCfgPane.addTab("Editors", null, new JScrollPane(stdEdPanel), "Standard Editor.");
        tabbedCfgPane.addTab("Problems / Messages", null, new JScrollPane(probPanel), "Summary, info, and so forth.");
        tabbedCfgPane.addTab("Challenge", null, new JScrollPane(chalPanel), "Source view during challenges.");
        tabbedCfgPane.addTab("Room Summary", null, new JScrollPane(summPanel), "Room summary.");
        tabbedCfgPane.addTab("Additional Fonts", null, new JScrollPane(menuPanel), "Menu / Summary font.");
        tabbedCfgPane.addTab("Syntax Highlighting", null, new JScrollPane(highlightPanel), "Highlighting font.");
        tabbedCfgPane.setSelectedIndex(0);		// goto first tab

        setLayout(new GridLayout(1, 1));  // one row one col - nothing else on this panel
        add(tabbedCfgPane); 
    }

    public void saveAll() {
        chatPanel.saveChatPreferences();
        stdEdPanel.saveStdEdPreferences();
        probPanel.saveProblemPreferences();
        chalPanel.saveChallengePreferences();
        summPanel.savePreferences();
        menuPanel.saveMenuPreferences();
        highlightPanel.saveHighlightPreferences();

        // Force a repaint of all frames 
        // (in case they aren't listening for changes in the preferences)
        Frame[] frame = Frame.getFrames();
        for(int x=frame.length-1;x>=0;x--) frame[x].repaint();
    }

    public boolean isChangesPending() {
        return summPanel.areChangesPending() || chalPanel.areChangesPending() || chatPanel.areChangesPending() || stdEdPanel.areChangesPending() || probPanel.areChangesPending() || menuPanel.areChangesPending() || highlightPanel.areChangesPending();
    }
}
