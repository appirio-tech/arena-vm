package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class RoomPanel {
    private UIPage page;
    private UIComponent wp;
    private LeaderBoardPanel lp;
    private BroadcastRoomPanel bp;
    private TimerPanel tp;
    private CompPanel cp;
    private UIComponent connStatus;

    public RoomPanel(String rn, ContestApplet ca, UIComponent wp, RoomCompPanel ccp, UIPage page) {
        connStatus = page.getComponent("connection_status");
        bp = new BroadcastRoomPanel(ca, page);
        lp = new LeaderBoardPanel(ca, page);
        tp = new TimerPanel(ca, page);
        cp = new CompPanel(ca, ccp, rn, "", page);
        this.wp = wp;
        tp.setVisible(false);
        this.page = page;
    }

    public void clear() {
        cp.clear();
    }

    public UIComponent getPanel() {
        return page.getComponent("room_panel");
    }

    public CompPanel getCompPanel() {
        return cp;
    }

    public TimerPanel getTimerPanel() {
        return tp;
    }

    public void setStatusLabel(boolean on) {
        connStatus.setProperty("enabled", Boolean.valueOf(on));
        bp.setButtonEnabled(on);
        lp.setButtonEnabled(on);
    }

    public UIComponent getWorkPanel() {
        return wp;
    }

    public void showTimer() {
        tp.setVisible(true);
    }
}
