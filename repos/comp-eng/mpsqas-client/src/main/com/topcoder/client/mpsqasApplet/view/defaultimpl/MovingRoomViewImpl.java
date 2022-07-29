package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.MovingRoomView;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * Implementation of MovingRoom view.
 *
 * @author mitalub
 */
public class MovingRoomViewImpl extends JPanelView
        implements MovingRoomView {

    private GridBagLayout mainLayout; //the GridBagLayout used by the main room.
    private GridBagLayout moveLayout; //the GridBagLayout used by the login panel
    private GridBagConstraints gbc;  //and its corresponding constraings

    //components:
    private JLabel statusLabel;
    private JPanel movePanel;
    private JLabel title;
    private Thread counter;

    public void init() {
        movePanel = new JPanel();
        moveLayout = new GridBagLayout();
        mainLayout = new GridBagLayout();
        gbc = new GridBagConstraints();
        movePanel.setLayout(moveLayout);
        setLayout(mainLayout);

        title = new JLabel("Moving:");
        title.setFont(DefaultUIValues.HEADER_FONT);

        statusLabel = new JLabel("Waiting for move response from server...");

        gbc.insets = new Insets(5, 5, 5, 5);

        GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        moveLayout.setConstraints(title, gbc);
        movePanel.add(title);

        gbc.anchor = GridBagConstraints.CENTER;

        GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 1, 1);
        moveLayout.setConstraints(statusLabel, gbc);
        movePanel.add(statusLabel);

        movePanel.setBorder(new EtchedBorder());

        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        mainLayout.setConstraints(movePanel, gbc);
        add(movePanel);
    }

    public void update(Object arg) {
    }
}
