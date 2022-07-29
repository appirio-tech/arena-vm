package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.netClient.ResponseWaiter;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class MessageFrame implements FrameLogic {
    private UIPage page;
    private UIComponent frame;
    private UIComponent message;

    private final ContestApplet ca;
    private final Component baseComp;
    private ResponseWaiter rw;

    public UIComponent getFrame() {
        return frame;
    }

    public MessageFrame(String title, Component baseComp, ContestApplet ca) {
        this.ca = ca;
        this.baseComp = baseComp;
        page = ca.getCurrentUIManager().getUIPage("message_frame", true);
        frame = page.getComponent("root_frame");
        message = page.getComponent("message");
        frame.setProperty("title", title);
        frame.performAction("pack");
    }

    public void showMessage(String text, int requestType)
        ////////////////////////////////////////////////////////////////////////////////
    {
        showMessage(text, baseComp, requestType);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showMessage(String text, Component comp, final int requestType)
        ////////////////////////////////////////////////////////////////////////////////
    {
        terminate(); // in case we are already waiting on another response

        message.setProperty("Text", text);
        Common.setLocationRelativeTo(comp, (Component) frame.getEventSource());

        rw = new ResponseWaiter();

        Thread t = new Thread(new Runnable() {
                public void run() {
                    if (rw.block()) {
                        ca.getRoomManager().getCurrentRoom().timeOutEvent(requestType);
                        timeOut();
                    }
                }
            });

        t.start();

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }  // let the thread catch up.

        frame.performAction("show");   // show dialog
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void hideMessage()
        ////////////////////////////////////////////////////////////////////////////////
    {
        frame.performAction("hide");
        terminate();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void terminate()
        ////////////////////////////////////////////////////////////////////////////////
    {
        if (rw != null) {
            rw.unBlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void timeOut()
        ////////////////////////////////////////////////////////////////////////////////
    {
        message.setProperty("Text", "Your request timed out.");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            //System.out.println("Could not sleep");
        }
        frame.performAction("hide");
    }
}
