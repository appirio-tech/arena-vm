package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.ImportantMessageSummaryPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;

public class ImportantMessageSummaryFrame implements FrameLogic {

    private ContestApplet parentFrame = null;
    private ImportantMessageSummaryPanel summaryPanel = null;
    private UIPage page;
    private UIComponent frame;

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
    }
    
    public void update(GetImportantMessagesResponse resp) {
        summaryPanel.update(resp);
    }

    public UIComponent getFrame() {
        return frame;
    }
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ImportantMessageSummaryFrame(ContestApplet parent)
        ////////////////////////////////////////////////////////////////////////////////
    {
        parentFrame = parent;
        page = parent.getCurrentUIManager().getUIPage("important_message_frame", true);
        frame = page.getComponent("root_frame");
        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showFrame()
        ////////////////////////////////////////////////////////////////////////////////
    {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (Component) frame.getEventSource());
        frame.performAction("show");
        MoveFocus.moveFocus(summaryPanel.getTable());
    }

    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // create all the panels/panes
        summaryPanel = new ImportantMessageSummaryPanel(parentFrame, page);
        frame.performAction("pack");
    }

    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

}
