package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Frame;

import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class TabbedAppletConfigurationPanel {
    private ChatConfigurationPanel chatPanel;
    private StdEdConfigurationPanel stdEdPanel;
    private ProblemConfigurationPanel probPanel;
    private ChallengeConfigurationPanel chalPanel;
    private SummaryConfigurationPanel summPanel;
    private AdditionalFontsConfigurationPanel menuPanel;
    private HighlightConfigurationPanel highlightPanel;
    private SkinConfigurationPanel skinPanel;

    public TabbedAppletConfigurationPanel(FrameLogic parent, UIPage page) {
        UIComponent tabbedPane = page.getComponent("tabbed_applet_configuration_panel");
        tabbedPane.setProperty("SelectedIndex", new Integer(0));
        chatPanel = new ChatConfigurationPanel(parent, page);
        stdEdPanel = new StdEdConfigurationPanel(parent, page);
        probPanel = new ProblemConfigurationPanel(parent, page);
        chalPanel = new ChallengeConfigurationPanel(parent, page);
        summPanel = new SummaryConfigurationPanel(parent, page);
        menuPanel = new AdditionalFontsConfigurationPanel(page);
        highlightPanel = new HighlightConfigurationPanel(parent, page);
        skinPanel = new SkinConfigurationPanel(parent, page);
    }

    public void saveAll() {
        chatPanel.saveChatPreferences();
        stdEdPanel.saveStdEdPreferences();
        probPanel.saveProblemPreferences();
        chalPanel.saveChallengePreferences();
        summPanel.savePreferences();
        menuPanel.saveMenuPreferences();
        highlightPanel.saveHighlightPreferences();
        skinPanel.savePreferences();

        // Force a repaint of all frames 
        // (in case they aren't listening for changes in the preferences)
        Frame[] frame = Frame.getFrames();
        for(int x=frame.length-1;x>=0;x--) frame[x].repaint();
    }

    public boolean isChangesPending() {
        return summPanel.areChangesPending() || chalPanel.areChangesPending() || chatPanel.areChangesPending() || stdEdPanel.areChangesPending() || probPanel.areChangesPending() || menuPanel.areChangesPending() || highlightPanel.areChangesPending() || skinPanel.areChangesPending();
    }
}
