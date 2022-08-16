package com.topcoder.client.contestApplet.frames;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.table.*;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;

public class ImportantMessageSummaryFrame extends JFrame {

    private ContestApplet parentFrame = null;
    private ImportantMessageSummaryPanel summaryPanel = null;

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
    }
    
    public void update(GetImportantMessagesResponse resp) {
        summaryPanel.update(resp);
    }
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ImportantMessageSummaryFrame(ContestApplet parent)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super("TopCoder Competition Arena - Important Messages");
        parentFrame = parent;

        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showFrame()
            ////////////////////////////////////////////////////////////////////////////////
    {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), this);
        show();
        MoveFocus.moveFocus(summaryPanel.getTable());
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
        summaryPanel = new ImportantMessageSummaryPanel(parentFrame);
        summaryPanel.setPreferredSize(new Dimension(575, 250));
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(summaryPanel, getContentPane(), gbc, 0, 1, 1, 1, .5, 1.0);
        pack();
    }

    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------

}
