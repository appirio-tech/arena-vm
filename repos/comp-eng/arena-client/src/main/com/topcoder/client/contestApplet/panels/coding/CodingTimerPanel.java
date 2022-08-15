package com.topcoder.client.contestApplet.panels.coding;

import java.awt.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.panels.room.*;

public class CodingTimerPanel extends TimerPanel {

    public CodingTimerPanel(ContestApplet ca) {
        // place the image in the background of the panel
        super(ca, new GridBagLayout(), Common.getImage("single_clock.gif", ca));

        // set the size
        setMinimumSize(new Dimension(145, 53));
        setPreferredSize(new Dimension(145, 53));

        createTimer(-22, -18);
    }
}
