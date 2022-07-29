package com.topcoder.client.contestApplet.frames;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.table.*;


public class BroadcastSummaryFrame extends JFrame {

    private static BroadcastSummaryFrame singleton;

    public static BroadcastSummaryFrame getInstance(ContestApplet ca) {
        if (singleton == null)
            singleton = new BroadcastSummaryFrame(ca);
        return singleton;
    }

    private ContestApplet parentFrame = null;
    private BroadcastSummaryPanel summaryPanel = null;

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
    }
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    private BroadcastSummaryFrame(ContestApplet parent)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super("TopCoder Competition Arena - Broadcast Summary");
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
        summaryPanel = new BroadcastSummaryPanel(parentFrame);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        summaryPanel.setPreferredSize(new Dimension(575, 250));
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(createColorPanel(), getContentPane(), gbc, 0, 0, 1, 1, 0.5, 0.0);
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(summaryPanel, getContentPane(), gbc, 0, 1, 1, 1, .5, 1.0);
        pack();
    }

    /**
     * Clear out all broadcast data
     */
// --Recycle Bin START (5/8/02 2:09 PM):
//    ////////////////////////////////////////////////////////////////////////////////
//    public void clear()
//    ////////////////////////////////////////////////////////////////////////////////
//    {
//        summaryPanel.clear();
//    }
// --Recycle Bin STOP (5/8/02 2:09 PM)


    // ------------------------------------------------------------
    // Customized pane creation
    // ------------------------------------------------------------


    //private static Font bold = new Font("",Font.BOLD,13);
    ////////////////////////////////////////////////////////////////////////////////
    public JPanel createColorPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel ret = new JPanel(new GridBagLayout());
        JPanel p1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        FillingOvalIcon icon = new FillingOvalIcon();
        icon.setForeground(Common.CODER_BLUE);
        icon.setPercentage(1);
        JLabel lbl = new JLabel("General", icon, JLabel.CENTER);
        lbl.setFont(new Font("", Font.BOLD, 13));
        lbl.setForeground(Common.CODER_BLUE);
        lbl.setVerticalTextPosition(JLabel.CENTER);
        lbl.setHorizontalTextPosition(JLabel.RIGHT);
        lbl.setPreferredSize(new Dimension(60, 12));
        Common.insertInPanel(lbl, p1, gbc, 0, 0, 1, 1, .1, .1);
        icon = new FillingOvalIcon();
        icon.setForeground(Common.CODER_RED);
        icon.setPercentage(1);
        lbl = new JLabel("Problem", icon, JLabel.CENTER);
        lbl.setFont(new Font("", Font.BOLD, 13));
        lbl.setForeground(Common.CODER_RED);
        lbl.setVerticalTextPosition(JLabel.CENTER);
        lbl.setHorizontalTextPosition(JLabel.RIGHT);
        lbl.setPreferredSize(new Dimension(60, 12));
        Common.insertInPanel(lbl, p1, gbc, 1, 0, 1, 1, .1, .1);
        icon = new FillingOvalIcon();
        icon.setForeground(Common.CODER_GREEN);
        icon.setPercentage(1);
        lbl = new JLabel("Round", icon, JLabel.CENTER);
        lbl.setForeground(Common.CODER_GREEN);
        lbl.setFont(new Font("", Font.BOLD, 13));
        lbl.setVerticalTextPosition(JLabel.CENTER);
        lbl.setHorizontalTextPosition(JLabel.RIGHT);
        lbl.setPreferredSize(new Dimension(60, 12));
        Common.insertInPanel(lbl, p1, gbc, 2, 0, 1, 1, .1, .1);
        p1.setBackground(Color.black);
        p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        p1.setPreferredSize(new Dimension(0, 20));
        gbc.insets = new Insets(-1, -1, -1, -1);
        ret.setBorder(Common.getTitledBorder("Legend"));
        Common.insertInPanel(p1, ret, gbc, 0, 0, 1, 1, .1, .1);
        return (ret);
    }
}
