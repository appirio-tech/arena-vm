package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.components.LeaderBoardButton;
import com.topcoder.client.ui.UIPage;

public class LeaderBoardPanel {
    private LeaderBoardButton but;

    public LeaderBoardPanel(ContestApplet ca, UIPage page) {
        but = new LeaderBoardButton(ca, page.getComponent("leaderboard_button"));
    }

    public void setButtonEnabled(boolean on) {
        but.setEnabled(on);
    }
}
