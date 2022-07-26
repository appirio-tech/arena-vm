package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIPage;

public class CodingTimerPanel extends TimerPanel {
    public CodingTimerPanel(ContestApplet ca, UIPage page) {
        super(ca, page, "coding_timer_panel", "timer_title", "timer");
    }
}
