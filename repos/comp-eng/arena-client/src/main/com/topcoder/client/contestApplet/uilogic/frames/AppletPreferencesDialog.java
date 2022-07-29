package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.frames.MainFrame;
import com.topcoder.client.contestApplet.uilogic.panels.TabbedAppletConfigurationPanel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowListener;

public class AppletPreferencesDialog implements FrameLogic, UIActionListener, UIWindowListener  {
    private UIComponent saveButton;
    private UIPage page;
    private UIComponent closeButton;
    private TabbedAppletConfigurationPanel configPanel;
    private JFrame myParent;
    private UIComponent dialog;
    private Dimension size;

    public UIComponent getFrame() {
        return dialog;
    }

    public ContestApplet getApplet() {
        return ((MainFrame)myParent).getContestApplet();
    }

    public AppletPreferencesDialog(JFrame parent) {
        myParent = parent;
        page = getApplet().getCurrentUIManager().getUIPage("applet_preferences_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("Owner", parent);
        dialog.create();
        saveButton = page.getComponent("save_button");
        closeButton = page.getComponent("close_button");
        configPanel = new TabbedAppletConfigurationPanel(this, page);

        Common.setLocationRelativeTo(parent, (Component) dialog.getEventSource());
        dialog.addEventListener("Window", this);
        saveButton.addEventListener("Action", this);
        closeButton.addEventListener("Action", this);
    }

    public void show() {
        dialog.performAction("show");
    }

    public void actionPerformed(ActionEvent e) {

        // Get the source of the action
        Object source = e.getSource();
        if (source == saveButton.getEventSource()) {
            configPanel.saveAll();
        } else if (source == closeButton.getEventSource()) {
            windowClosing(new WindowEvent((JDialog)dialog.getEventSource(), WindowEvent.WINDOW_CLOSING));
        }
    }

    public void windowClosing(WindowEvent e) {
        // Are saves pending?
        if (configPanel.isChangesPending()) {

            // Should we save?
            if (Common.confirm("Save Pending", "Changes are pending.  Do you want to save before closing?", (JDialog) dialog.getEventSource())) {
                configPanel.saveAll();                
            }
        }
        // Close the window
        dialog.performAction("dispose");
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}
