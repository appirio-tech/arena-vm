package com.topcoder.client.contestApplet.panels.room.comp;

//import java.util.*;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;

public class RoomCompPanel extends JPanel {

    private JLabel contest = null;

    ////////////////////////////////////////////////////////////////////////////////
    public RoomCompPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout());
        setOpaque(false);
        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // contest label
        JLabel l2 = new JLabel("");
        l2.setForeground(Color.white);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;

        int i = 0;
        Common.insertInPanel(l2, this, gbc, i++, 0, 1, 1, 1.0, 0.1);

        contest = l2;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void updateContestInfo(String mesg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        contest.setText(mesg);
        contest.revalidate();
        contest.repaint();
    }

    public void clear() {
    }
}
