package com.topcoder.client.contestApplet.frames;

/*
 * ActiveUsersTableFrame.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.awt.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.panels.table.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class ActiveUsersTableFrame extends TableFrame implements UserListListener {

    /**
     * Class constructor
     */
    
    public void setPanelEnabled(boolean on) {
        tp.setPanelEnabled(on);
    }
    
    private ActiveUsersTablePanel tp;
    
    ////////////////////////////////////////////////////////////////////////////////
    public ActiveUsersTableFrame(ContestApplet parentFrame)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super("");
        this.parentFrame = parentFrame;

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        // create all the panels/panes
        tp = new ActiveUsersTablePanel(parentFrame, this);

        // set misc properties
        tp.setMinimumSize(new Dimension(275, 350));
        tp.setPreferredSize(new Dimension(275, 350));

        create(tp);

        Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
    }

    public void updateUserList(final UserListItem[] items) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getTablePanel().clear();
                ((ActiveUsersTablePanel) getTablePanel()).updateUserList(items);
                show();
            }
        });
    }
}
