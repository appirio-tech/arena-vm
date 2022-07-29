package com.topcoder.client.contestApplet.uilogic.frames;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

public class BackgroundTaskStatusDialog implements FrameLogic {
    private UIPage page;
    private UIComponent dialog;
    private UIComponent msg;
    private UIComponent cancelButton;

    public UIComponent getFrame() {
        return dialog;
    }

    public BackgroundTaskStatusDialog(ContestApplet ca, JFrame frame, String title, String message) {
        page = ca.getCurrentUIManager().getUIPage("background_task_status_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("title", title);
        dialog.setProperty("owner", frame);
        dialog.create();
        msg = page.getComponent("message_label");
        cancelButton = page.getComponent("cancel_button");
        msg.setProperty("text", message);
        dialog.performAction("pack");
        dialog.setProperty("locationrelativeto", frame);
    }

    public void updateMessage(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    msg.setProperty("Text", text);
                    dialog.performAction("pack");
                    dialog.performAction("repaint");
                }
        
            });
    }        

    public void show() {
        dialog.performAction("show");
    }

    public void dispose() {
        dialog.performAction("dispose");
    }

    public void setVisible(boolean on ) {
        dialog.setProperty("visible", Boolean.valueOf(on));
    }

    public void addCancelActionListener(UIActionListener listener) {
        cancelButton.addEventListener("action", listener);
    }
    
    public void removeCancelActionListener(UIActionListener listener) {
        cancelButton.removeEventListener("action", listener);
    }
}
