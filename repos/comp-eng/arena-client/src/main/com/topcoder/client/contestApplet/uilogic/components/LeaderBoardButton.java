package com.topcoder.client.contestApplet.uilogic.components;

import java.awt.event.ActionEvent;

import com.topcoder.client.contestApplet.uilogic.frames.RoomListFrame;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.event.*;

public class LeaderBoardButton {
    private ContestApplet ca;
    private UIComponent button;

    public LeaderBoardButton(ContestApplet applet, UIComponent component) {
        this.ca = applet;
        this.button = component;

        component.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(((Boolean) button.getProperty("enabled")).booleanValue()) {
                        ca.getRequester().requestGetLeaderBoard();
                        RoomListFrame.getInstance(ca).show();
                    }
                }
            });
    }

    public void setEnabled(boolean on) {
        button.setProperty("enabled", Boolean.valueOf(on));
    }
}
