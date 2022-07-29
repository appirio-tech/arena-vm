package com.topcoder.client.contestApplet.panels.room.comp;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;

//import com.topcoder.client.contestApplet.panels.room.*;

public final class CompPanel extends ImageIconPanel {

    private RoomCompPanel contestPanel = null;
    protected String n = null;
    private JLabel roomName = null;
    private JLabel contestName = null;

    ////////////////////////////////////////////////////////////////////////////////
    //public CompPanel(ContestApplet ca, String rn, String cn)
    public CompPanel(ContestApplet ca, RoomCompPanel cp, String rn, String cn)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, cp);
        setRoomName(rn);
        setContestName(cn);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //public CompPanel(ContestApplet ca, String rn)
    public CompPanel(ContestApplet ca, RoomCompPanel cp, String rn)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, cp);
        setRoomName(rn);
        setContestName("");
    }

    ////////////////////////////////////////////////////////////////////////////////
    public CompPanel(ContestApplet ca, RoomCompPanel cp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // place the image in the background of the panel
        super(new GridBagLayout(), Common.getImage("comp_area.gif", ca));

        roomName = new JLabel("", JLabel.CENTER);
        contestName = new JLabel("", JLabel.CENTER);

        roomName.setForeground(Common.COMPS_RN);
        contestName.setForeground(Common.COMPS_CN);
        // set the size
        setMinimumSize(new Dimension(552, 72));
        setPreferredSize(new Dimension(552, 72));

        contestPanel = cp;

        // create title panel
        createTitlePanel();
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void createTitlePanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();
        JPanel title = new JPanel(new GridBagLayout());

        title.setMinimumSize(new Dimension(240, 20));
        title.setPreferredSize(new Dimension(240, 20));
        title.setOpaque(false);

        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.NONE;

        gbc.insets = new Insets(0, 0, 0, 0);
        Common.insertInPanel(roomName, title, gbc, 0, 0, 1, 1, 0.0, 0.1);
        Common.insertInPanel(contestName, title, gbc, 1, 0, 1, 1, 1.0, 0.1);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 260, 0, 0);
        Common.insertInPanel(title, this, gbc, 0, 0, 1, 1, 0.1, 0.1);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);
        Common.insertInPanel(contestPanel, this, gbc, 0, 1, 1, 1, 0.1, 0.2);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setRoomName(String name)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (name.equals("")) {
            roomName.setText("");
        } else {
            roomName.setText("> " + name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setContestName(String name)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (name.equals("")) {
            contestName.setText("");
        } else {
            contestName.setText(": " + name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public RoomCompPanel getContestPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return contestPanel;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        setContestName("");
    }
}
