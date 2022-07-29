package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.BroadcastSummaryPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class BroadcastSummaryFrame implements FrameLogic {
    private static BroadcastSummaryFrame singleton;

    public static BroadcastSummaryFrame getInstance(ContestApplet ca) {
        if (singleton == null)
            singleton = new BroadcastSummaryFrame(ca);
        return singleton;
    }

    private ContestApplet parentFrame = null;
    private BroadcastSummaryPanel summaryPanel = null;
    private boolean enabled = true;
    private UIComponent frame;
    private UIPage page;

    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
    }

    public UIComponent getFrame() {
        return frame;
    }

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    private BroadcastSummaryFrame(ContestApplet parent)
        ////////////////////////////////////////////////////////////////////////////////
    {
        parentFrame = parent;
        page = parent.getCurrentUIManager().getUIPage("broadcast_summary_frame", true);
        frame = page.getComponent("root_frame");
        summaryPanel = new BroadcastSummaryPanel(parentFrame, page);
        frame.performAction("pack");
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showFrame()
        ////////////////////////////////////////////////////////////////////////////////
    {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (Component) frame.getEventSource());
        frame.performAction("show");
        MoveFocus.moveFocus(summaryPanel.getTable());
    }
}

