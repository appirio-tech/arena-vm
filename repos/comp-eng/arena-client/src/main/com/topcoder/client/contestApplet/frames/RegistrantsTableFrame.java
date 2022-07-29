package com.topcoder.client.contestApplet.frames;

/*
 * RegistrantsTableFrame.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.awt.*;
import java.util.ArrayList;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.panels.table.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.*;

import javax.swing.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public class RegistrantsTableFrame extends TableFrame implements UserListListener {
    public static final int OTHER = 0;
    public static final int HIGH_SCHOOL = 1;
    public static final int MARATHON = 2;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////

    private boolean enabled = true;
    private int type;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        tp.setPanelEnabled(on);
    }
    
    public UserTablePanel tp;

    public RegistrantsTableFrame(ContestApplet parentFrame, int type)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super("Registrants");
        this.parentFrame = parentFrame;
        this.type = type;
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        // create all the panels/panes
        if (type == HIGH_SCHOOL) {
            tp = new HSRegistrantsTablePanel(parentFrame, this);
        } else {
            tp = new RegistrantsTablePanel(parentFrame, this);
        }

        // set misc properties
        tp.setMinimumSize(new Dimension(275, 350));
        tp.setPreferredSize(new Dimension(275, 350));

        create(tp);

        //hack to reposition table
        Component c;
        c = getContentPane().getComponent(0);
        getContentPane().remove(0);
        
        if (type == HIGH_SCHOOL) {
            getContentPane().add(((HSRegistrantsTablePanel)tp).totalLabel, new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),0);
            getContentPane().add(((HSRegistrantsTablePanel)tp).newbieLabel, new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),1);
            getContentPane().add(c, new GridBagConstraints(0,2,3,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0),2);
        } else if (type == MARATHON) {
            getContentPane().add(((RegistrantsTablePanel)tp).totalLabel, new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),0);
            getContentPane().add(((RegistrantsTablePanel)tp).newbieLabel, new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),1);
            getContentPane().add(c, new GridBagConstraints(0,2,3,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0),2);
        } else if (type == OTHER) {
            getContentPane().add(((RegistrantsTablePanel)tp).totalLabel, new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),0);
            getContentPane().add(((RegistrantsTablePanel)tp).div1Label, new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),1);
            getContentPane().add(((RegistrantsTablePanel)tp).div2Label, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),2);            
            getContentPane().add(((RegistrantsTablePanel)tp).newbieLabel, new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,25,0,5),0,0),3);
            getContentPane().add(c, new GridBagConstraints(0,2,3,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0),4);
        } 

        Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
    }

    /**
     * CREATE_USER_LIST
     *
     * [0] -> (ArrayList) users
     * [1] -> (ArrayList) ratings
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void updateUserList(final UserListItem[] items) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ((UserTablePanel) getTablePanel()).clear();
                ((UserTablePanel) getTablePanel()).updateUserList(items);

                validate();
                repaint();
                if (type == HIGH_SCHOOL) {
                    setSize(new Dimension(380,450));
                } else {
                    setSize(new Dimension(300,450));
                }

                show();
            }
        });
    }
}
