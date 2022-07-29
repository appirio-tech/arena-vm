package com.topcoder.client.contestApplet.frames;

/*
 * MessageFrame.java
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
import com.topcoder.client.netClient.ResponseWaiter;

//import com.topcoder.netCommon.contest.ContestConstants;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class MessageFrame extends JFrame {

    // Problem panel variables
    private final ContestApplet ca;
    private final Component baseComp;

    private JLabel msg;
    private ResponseWaiter rw;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public MessageFrame(String title, Component baseComp, ContestApplet ca)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(title);

        this.ca = ca;

        getContentPane().setBackground(Common.WPB_COLOR);
        getRootPane().setPreferredSize(new Dimension(200, 50));
        getRootPane().setMinimumSize(new Dimension(200, 50));
        getContentPane().setLayout(new BorderLayout());

        create();

        this.baseComp = baseComp;

        pack();
    }

    /*
  ////////////////////////////////////////////////////////////////////////////////
  public void clear()
  ////////////////////////////////////////////////////////////////////////////////
  {
    msg.setText("");
  }
  */

    ////////////////////////////////////////////////////////////////////////////////
    public void showMessage(String text, int requestType)
            ////////////////////////////////////////////////////////////////////////////////
    {
        showMessage(text, baseComp, requestType);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showMessage(String text, Component comp, final int requestType)
            ////////////////////////////////////////////////////////////////////////////////
    {
        terminate(); // in case we are already waiting on another response

        msg.setText(text);
        Common.setLocationRelativeTo(comp, this);

        rw = new ResponseWaiter();

        Thread t = new Thread(new Runnable() {
            public void run() {
                if (rw.block()) {
                    ca.getRoomManager().getCurrentRoom().timeOutEvent(requestType);
                    timeOut();
                }
            }
        });

        t.start();

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }  // let the thread catch up.

        show();   // show dialog
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void hideMessage()
            ////////////////////////////////////////////////////////////////////////////////
    {
        hide();
        terminate();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void terminate()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (rw != null) {
            rw.unBlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void timeOut()
            ////////////////////////////////////////////////////////////////////////////////
    {
        msg.setText("Your request timed out.");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            //System.out.println("Could not sleep");
        }
        hide();
    }

    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    private void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //GridBagConstraints gbc = Common.getDefaultConstraints();

        JLabel l1 = new JLabel("", SwingConstants.CENTER);

        l1.setForeground(Color.white);
        l1.setFont(new Font(null, Font.PLAIN, 12));

        getContentPane().add(l1, BorderLayout.CENTER);

        msg = l1;
    }
}
