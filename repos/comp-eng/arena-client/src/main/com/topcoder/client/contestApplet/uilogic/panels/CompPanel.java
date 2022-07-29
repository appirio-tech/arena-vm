package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class CompPanel {
    private RoomCompPanel cp;
    private UIComponent roomName;
    private UIComponent contestName;

    public CompPanel(ContestApplet ca, RoomCompPanel cp, String rn, String cn, UIPage page) {
        this(ca, cp, page);
        setRoomName(rn);
        setContestName(cn);
    }

    public CompPanel(ContestApplet ca, RoomCompPanel cp, String rn, UIPage page) {
        this(ca, cp, page);
        setRoomName(rn);
    }

    public CompPanel(ContestApplet ca, RoomCompPanel cp, UIPage page) {
        this.cp = cp;
        roomName = page.getComponent("room_name");
        contestName = page.getComponent("contest_name");
        setRoomName("");
        setContestName("");
    }

    public RoomCompPanel getContestPanel() {
        return cp;
    }

    public void setRoomName(String name) {
        if (name.equals("")) {
            roomName.setProperty("Text", "");
        } else {
            roomName.setProperty("Text", "> " + name);
        }
    }

    public void setContestName(String name) {
        if (name.equals("")) {
            contestName.setProperty("Text", "");
        } else {
            contestName.setProperty("Text", ": " + name);
        }
    }

    public void clear() {
        setContestName("");
    }
}
