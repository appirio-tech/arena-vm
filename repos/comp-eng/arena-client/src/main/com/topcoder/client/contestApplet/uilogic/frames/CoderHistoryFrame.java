package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.CoderHistoryPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.data.CoderHistoryData;

public class CoderHistoryFrame implements FrameLogic {
    private UIPage page;
    private UIComponent frame;
    private ContestApplet parentFrame = null;
    private CoderHistoryPanel coderHistoryPanel = null;

    public UIComponent getFrame() {
        return frame;
    }

    public CoderHistoryFrame(ContestApplet parent, CoderHistoryResponse response) {
        parentFrame = parent;
        page = parent.getCurrentUIManager().getUIPage("coder_history_frame", true);
        frame = page.getComponent("root_frame");
        coderHistoryPanel = new CoderHistoryPanel(parentFrame, page, response, frame);
        frame.performAction("pack");
    }

    public void showFrame() {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), frame);
        frame.performAction("show");
        MoveFocus.moveFocus(coderHistoryPanel.getTable());
    }
}
