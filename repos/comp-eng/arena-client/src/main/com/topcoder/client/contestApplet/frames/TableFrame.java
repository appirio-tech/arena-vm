package com.topcoder.client.contestApplet.frames;

/*
 * TableFrame.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

//import java.util.*;

import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.widgets.*;
//import com.topcoder.client.contestApplet.panels.main.*;
import com.topcoder.client.contestApplet.panels.table.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public abstract class TableFrame extends JFrame {

    protected ContestApplet parentFrame = null;
    protected TablePanel tablePanel = null;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public TableFrame(String title)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(title);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public TableFrame(ContestApplet parentFrame)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super("Table Panel");
        this.parentFrame = parentFrame;

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        // create all the panels/panes
        TablePanel tp = null;

        // set misc properties
        tp.setMinimumSize(new Dimension(275, 350));
        tp.setPreferredSize(new Dimension(275, 350));

        create(tp);
    }

    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create(TablePanel tp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.insets = new Insets(15, 15, 15, 15);
        getContentPane().add(tp, gbc);

        // globalize variables
        this.tablePanel = tp;

        // center on the screen
        Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);

        pack();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public TablePanel getTablePanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (tablePanel);
    }
}
