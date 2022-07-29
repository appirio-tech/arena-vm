package com.topcoder.client.contestApplet.uilogic.frames;

import java.applet.AppletContext;
import java.awt.Component;
import java.awt.event.ActionEvent;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.HyperLinkLoader;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

public class ImportantMessageDialog implements FrameLogic {
    private UIPage page;
    private UIComponent frame;
    private AppletContext arenaContext;
    private UIComponent okButton;
    private boolean agreed;

    public UIComponent getFrame() {
        return frame;
    }

    public ImportantMessageDialog(ContestApplet ca, String text) {
        page = ca.getCurrentUIManager().getUIPage("important_message_dialog", true);
        frame = page.getComponent("root_dialog", false);
        frame.setProperty("Owner", ca.getCurrentFrame());
        frame.create();
        arenaContext = ca.getAppletContext();
        okButton = page.getComponent("ok_button");
        okButton.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });
        UIComponent pane = page.getComponent("message_pane");
        pane.setProperty("text", Common.htmlEncode(text));
        pane.addEventListener("hyperlink", new HyperLinkLoader(arenaContext));
        frame.performAction("pack");

        Common.setLocationRelativeTo(ca.getCurrentFrame(), (Component) frame.getEventSource());
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
        ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = false;

        frame.performAction("show");

        return (agreed);
    }

    public void show() {
        frame.performAction("show");
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = true;
        frame.performAction("dispose");  // frees up the show() -- must be last
    }
}
