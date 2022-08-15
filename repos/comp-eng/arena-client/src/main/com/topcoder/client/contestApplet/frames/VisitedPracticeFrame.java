package com.topcoder.client.contestApplet.frames;

import java.awt.*;
import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.table.VisitedPracticePanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;

public class VisitedPracticeFrame extends JFrame {

    private ContestApplet parentFrame = null;
    private VisitedPracticePanel visitedPracticePanel = null;

    private boolean enabled = true;
    
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
        super("TopCoder Competition Arena - Visited Practice Rooms");
        parentFrame = parent;

        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showFrame()
            ////////////////////////////////////////////////////////////////////////////////
    {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), this);
        show();
        MoveFocus.moveFocus(visitedPracticePanel.getTable());
    }

    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // create all the panels/panes
        visitedPracticePanel = new VisitedPracticePanel(parentFrame);
        visitedPracticePanel.setPreferredSize(new Dimension(400, 310));
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(visitedPracticePanel, getContentPane(), gbc, 0, 1, 1, 1, .5, 1.0);
        pack();
    }

    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

}

