package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.components.BroadcastButton;
import com.topcoder.client.ui.UIPage;

public class BroadcastRoomPanel {
    private BroadcastButton but;

    public BroadcastRoomPanel(ContestApplet ca, UIPage page) {
        but = new BroadcastButton(ca, page.getComponent("broadcast_button"));
    }

    public void setButtonEnabled(boolean on) {
        but.setEnabled(on);
    }
}
