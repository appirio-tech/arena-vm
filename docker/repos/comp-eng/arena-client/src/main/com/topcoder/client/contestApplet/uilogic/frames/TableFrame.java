package com.topcoder.client.contestApplet.uilogic.frames;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.TablePanel;
import com.topcoder.client.ui.UIComponent;

public abstract class TableFrame implements FrameLogic {
    protected ContestApplet parentFrame;
    protected TablePanel tablePanel;
    protected UIComponent frame;

    protected abstract UIComponent createFrame();

    protected TableFrame(ContestApplet parentFrame) {
        this.parentFrame = parentFrame;
        frame = createFrame();
    }

    public UIComponent getFrame() {
        return frame;
    }

    public void create(TablePanel tablePanel) {
        this.tablePanel = tablePanel;

        frame.performAction("pack");
        Common.setLocationRelativeTo(parentFrame.getMainFrame(), (JFrame) frame.getEventSource());
    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }
}
