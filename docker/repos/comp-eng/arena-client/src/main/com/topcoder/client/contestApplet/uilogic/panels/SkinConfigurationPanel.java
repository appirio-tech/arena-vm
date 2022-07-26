package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.AppletPreferencesDialog;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIFactory;
import com.topcoder.client.ui.UIManager;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

public class SkinConfigurationPanel {
    /** Determines if changes are needing to be saved */
    private boolean changesPending = false;
    
    /** Reference to the preferences */
    private LocalPreferences localPref = LocalPreferences.getInstance();

    private ActionHandler handler = new ActionHandler();

    private AppletPreferencesDialog parentFrame;
    private UIPage page;
    private UIComponent skinSelection;
    private UIComponent skinDescription;
    private Map map;

    public SkinConfigurationPanel(FrameLogic parent, UIPage page) {
        this.page = page;
        parentFrame = (AppletPreferencesDialog) parent;
        skinSelection = page.getComponent("skin_selection_list");
        skinDescription = page.getComponent("skin_description_pane");
        UIManager[] managers = localPref.getAllUIManagers();
        UIManager defaultManager = managers[0];
        map = new HashMap(managers.length + 1);

        for (int i=0;i<managers.length;++i) {
            skinSelection.performAction("addItem", new Object[] {managers[i].getName()});
            map.put(managers[i].getName(), managers[i].getDescription());
        }
        skinSelection.setProperty("UI", skinSelection.getProperty("UI"));
        skinSelection.setProperty("SelectedItem", parentFrame.getApplet().getCurrentUIManager().getName());
        skinDescription.setProperty("Text", parentFrame.getApplet().getCurrentUIManager().getDescription());
        skinSelection.addEventListener("action", handler);
    }

    /** Return whether changes are pending or not */
    public boolean areChangesPending() {
        return changesPending;
    }

    /** Saves the preferences */
    public void savePreferences() {
        localPref.setProperty(LocalPreferences.UI_THEME, skinSelection.getProperty("selecteditem").toString());

        // Save the profile        
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }
    
    /** Action handler for changing color */
    private class ActionHandler implements UIActionListener {
       public void actionPerformed(ActionEvent e) {
           skinDescription.setProperty("Text", map.get(skinSelection.getProperty("SelectedItem")));
           changesPending = true;
        }
    }
}
