package com.topcoder.client.contestApplet.panels.room;

//import java.util.*;

import java.awt.*;
import javax.swing.*;
//import javax.swing.plaf.FontUIResource;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;

public class RankPanel extends ImageIconPanel {

    // rankings table

    ////////////////////////////////////////////////////////////////////////////////
    public RankPanel(ContestApplet ca)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // place the image in the background of the panel
        super(new GridBagLayout(), Common.getImage("ranks.gif", ca));

        // add the ranking table to the panel
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // set the size
        setMinimumSize(new Dimension(104, 156));
        setPreferredSize(new Dimension(104, 156));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;


        gbc.insets = new Insets(51, 16, 16, 20);
        Common.insertInPanel(getRankPanel(), this, gbc, 0, 0, 1, 1, 0.1, 0.1);

        setToolTipText("Associates categories of user ratings with a specific color.");

    }

    ////////////////////////////////////////////////////////////////////////////////
    private JPanel getRankPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 0));
        p.setMinimumSize(new Dimension(0, 0));

        Font myFont = null;

        if (UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
            myFont = new Font("Arial", Font.PLAIN, 10);
        } else {
            myFont = new Font("Arial", Font.PLAIN, 9);
        }

        for (int x = 0; x < Common.legendLabels.length; x++) {
            JLabel label = new JLabel(Common.legendLabels[x], SwingConstants.CENTER);
            label.setForeground(Common.getRankColor(Common.legendRanks[x]));
            label.setFont(myFont);
            Box s = Box.createHorizontalBox();
            s.add(Box.createHorizontalGlue());
            s.add(label);
            s.add(Box.createHorizontalGlue());
            p.add(s);
            //p.add(label);
        }

        return (p);
    }

}
