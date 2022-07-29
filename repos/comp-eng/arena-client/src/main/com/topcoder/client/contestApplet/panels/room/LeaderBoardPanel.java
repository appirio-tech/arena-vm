package com.topcoder.client.contestApplet.panels.room;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;

class LeaderBoardPanel extends ImageIconPanel {

    private static final String BACKGROUND_IMAGE = "broadcast_bg.gif";

    private LeaderBoardButton leaderBoardButton;
    
    ////////////////////////////////////////////////////////////////////////////////
    public LeaderBoardPanel(ContestApplet ca)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // place the image in the background of the panel
        super(new GridBagLayout(), Common.getImage(BACKGROUND_IMAGE, ca));
        GridBagConstraints gbc = Common.getDefaultConstraints();
        setMinimumSize(new Dimension(104, 74));
        setPreferredSize(new Dimension(104, 74));
        gbc.fill = GridBagConstraints.NONE;
        leaderBoardButton = new LeaderBoardButton(ca);
        leaderBoardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(36, 5, 20, 5);
        Common.insertInPanel(leaderBoardButton, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
    }
    
    public void setButtonEnabled(boolean on) {
        leaderBoardButton.setButtonEnabled(on);
    }

}
