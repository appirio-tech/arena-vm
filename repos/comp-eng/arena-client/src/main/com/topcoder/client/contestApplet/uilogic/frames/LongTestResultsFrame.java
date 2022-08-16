package com.topcoder.client.contestApplet.uilogic.frames;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.LongTestResultsSummaryPanel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;

public class LongTestResultsFrame implements FrameLogic {
    private UIComponent frame;
    private UIPage page;
    private ContestApplet parentFrame = null;
    private LongTestResultsSummaryPanel summaryPanel = null;
    private LongTestResultsResponse response;

    public UIComponent getFrame() {
        return frame;
    }

    public LongTestResultsFrame(ContestApplet parent, LongTestResultsResponse response) {
        this.parentFrame = parent;
        this.response = response;
        this.page = parent.getCurrentUIManager().getUIPage("long_test_results_frame", true);
        frame = page.getComponent("root_frame");
        frame.setProperty("title", response.getHandle() + "'s "+response.getResultTypeString());
        create();
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (JFrame) frame.getEventSource());
        frame.setProperty("visible", Boolean.TRUE);
    }

    private void create() {
        summaryPanel = new LongTestResultsSummaryPanel(parentFrame, response, page);
        frame.performAction("pack");
    }

    public void setVisible(boolean on) {
        frame.setProperty("Visible", Boolean.valueOf(on));
    }
}
