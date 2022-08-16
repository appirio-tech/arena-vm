package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.VisitedPracticePanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;

public class VisitedPracticeFrame implements FrameLogic {
    private UIPage page;
    private UIComponent frame;
    private ContestApplet parentFrame = null;
    private VisitedPracticePanel visitedPracticePanel = null;

    private boolean enabled = true;
    
    public UIComponent getFrame() {
        return frame;
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
        visitedPracticePanel.setPanelEnabled(on);
    }
    
    public void update(CreateVisitedPracticeResponse resp) {
        visitedPracticePanel.update(resp);
    }
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public VisitedPracticeFrame(ContestApplet parent)
        ////////////////////////////////////////////////////////////////////////////////
    {
        parentFrame = parent;
        page = parent.getCurrentUIManager().getUIPage("visited_practice_frame", true);
        frame = page.getComponent("root_frame");
        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showFrame()
        ////////////////////////////////////////////////////////////////////////////////
    {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (Component) frame.getEventSource());
        frame.performAction("show");
        MoveFocus.moveFocus(visitedPracticePanel.getTable());
    }

    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // create all the panels/panes
        visitedPracticePanel = new VisitedPracticePanel(parentFrame, page);
        frame.performAction("pack");
    }

    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

}
