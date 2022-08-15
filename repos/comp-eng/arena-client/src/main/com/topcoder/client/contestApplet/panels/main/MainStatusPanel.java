package com.topcoder.client.contestApplet.panels.main;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;

public class MainStatusPanel extends ImageIconPanel {

    private FaderPanel faderPanel = null;

    public MainStatusPanel(ContestApplet ca) {
        // place the image in the background of the panel
        super(new GridBagLayout(), Common.getImage("top_bar.gif", ca));

        // set the size
        setMinimumSize(new Dimension(440, 44));
        setPreferredSize(new Dimension(440, 44));
        faderPanel = new FaderPanel(ca.getModel());
        create();
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

//        JLabel l = new JLabel("Room Leaders : ");
//        l.setForeground(Common.STATUS_COLOR);
//
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets(5, 30, 9, 5);
//        Common.insertInPanel(l, this, gbc, 0, 0, 1, 1, 0.0, 0.0);
//        gbc.insets = new Insets(7, 0, 9, 15);
        gbc.insets = new Insets(7, 30, 9, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(faderPanel, this, gbc, 1, 0, 1, 1, 1.0, 0.1);
    }

    public FaderPanel getFaderPanel() {
        return (faderPanel);
    }
}
