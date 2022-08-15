package com.topcoder.client.contestApplet.uilogic.frames;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.SubmissionHistoryPanel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;

public class SubmissionHistoryFrame implements FrameLogic {
    private ContestApplet parentFrame = null;
    private SubmissionHistoryPanel summaryPanel = null;
    private SubmissionHistoryResponse response;
    private UIComponent frame;
    private UIPage page;

    public UIComponent getFrame() {
        return frame;
    }

    public SubmissionHistoryFrame(ContestApplet parent, SubmissionHistoryResponse response) {
        parentFrame = parent;
        this.response = response;
        page = parent.getCurrentUIManager().getUIPage(response.isExampleHistory() ? "example_submission_history_frame" : "full_submission_history_frame", true);
        frame = page.getComponent("root_frame");
        frame.setProperty("Title", response.getHandle() + "'s Submission History");
        create();
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (JFrame) frame.getEventSource());
        frame.setProperty("visible", Boolean.TRUE);
    }

    private void create() {
        summaryPanel = new SubmissionHistoryPanel(parentFrame, response, page);
        frame.performAction("pack");
    }

    public void setVisible(boolean on) {
        frame.setProperty("Visible", Boolean.valueOf(on));
    }
}
