package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class RoomCompPanel {
    private UIComponent contest;

    protected String getContestInfoName() {
        return "contest_info";
    }

    public RoomCompPanel(UIPage page) {
        contest = page.getComponent(getContestInfoName());
    }

    public void updateContestInfo(String msg) {
        contest.setProperty("text", msg);
        contest.performAction("revalidate");
        contest.performAction("repaint");
    }

    public void clear() {
    }
}
