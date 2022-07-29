/**
 */
package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.frames.RoomListFrame;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public final class LeaderBoardButton extends JButton {

    private ContestApplet ca;
    private LocalPreferences pref;

    private static final String LEADER_BOARD_IMAGE_FILENAME = "applet_leaderboard_button.gif";
    private static final String DISABLED_LEADER_BOARD_IMAGE_FILENAME = "no_applet_leaderboard_button.gif";

    private ImageIcon enabledIcon;
    private ImageIcon disabledIcon;
    
    private boolean enabled = true;
    
    public void setButtonEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            setIcon(enabledIcon);
        } else {
            setIcon(disabledIcon);
        }
    }
    
    /**
     * Constructor
     */
    public LeaderBoardButton(ContestApplet _ca) {
        super();
        this.ca = _ca;
        pref = LocalPreferences.getInstance();

        enabledIcon = Common.getImage(LEADER_BOARD_IMAGE_FILENAME, ca);
        disabledIcon = Common.getImage(DISABLED_LEADER_BOARD_IMAGE_FILENAME, ca);
        
        setIcon(enabledIcon);
        setPreferredSize(new Dimension(enabledIcon.getIconWidth(), enabledIcon.getIconHeight()));
        setMaximumSize(new Dimension(enabledIcon.getIconWidth(), enabledIcon.getIconHeight()));
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(enabled) {
                    ca.getRequester().requestGetLeaderBoard();
                    RoomListFrame.getInstance(ca).show();
                }
            }
        });
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setToolTipText("Retrieve all Active Room Leaders.");
        setMnemonic('l');
        setOpaque(false);
    }
}