package com.topcoder.client.contestApplet.panels.room;

import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.net.URL;
//import java.net.MalformedURLException;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.frames.RoomListFrame;
import com.topcoder.client.contestApplet.panels.room.comp.*;
import com.topcoder.client.contestApplet.widgets.*;

/**
 * This class builds creates the general room panel.
 *
 * The contents include : CompPanel, RankPanel, and a WorkPanel
 */
public class RoomPanel extends ImageIconPanel {

    private String roomName = null;
    private ContestApplet parentFrame = null;
    private CompPanel cp = null;
    private TimerPanel tp = null;
    private RankPanel rp = null;
    private JPanel wp = null;
    private BroadcastRoomPanel bp = null;
    private LeaderBoardPanel leaderBoardPanel = null;
    private JLabel connStatus = null;
    protected RoomListFrame rlf = null;


    ////////////////////////////////////////////////////////////////////////////////
    public RoomPanel(String rn, ContestApplet ca, JPanel wp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(rn, ca, wp, new RoomCompPanel());
    }

    ////////////////////////////////////////////////////////////////////////////////
    public RoomPanel(String rn, ContestApplet ca, JPanel wp, RoomCompPanel ccp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout(), Common.getImage("background_2.jpg", ca));
        this.roomName = rn;
        this.parentFrame = ca;
        this.wp = wp;
        create(ccp);
    }

    private void create(RoomCompPanel ccp) {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // panel need to combine the competition/timer panels
        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);

        // set panel properties
        setBackground(Common.BG_COLOR);

        // create all the panels/panes
        cp = new CompPanel(parentFrame, ccp, roomName, "");
        tp = new TimerPanel(parentFrame);

        rp = new RankPanel(parentFrame);
        bp = new BroadcastRoomPanel(parentFrame);
        leaderBoardPanel = new LeaderBoardPanel(parentFrame);
        if (wp == null) {
            wp = new JPanel();            //create a default workpanel if one doesn't exist
        }

        // insert competition arena panel
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(cp, top, gbc, 0, 0, 1, 1, 0.0, 0.0);
        
        //insert connection status panel
        gbc.insets = new Insets(0, 0, 0, 0);
        connStatus = new JLabel(Common.getImage("connected.gif", parentFrame));
        JPanel sp = new JPanel(new GridBagLayout());
        sp.setBackground(Common.BG_COLOR);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        JLabel connText = new JLabel("Connection Status: ");
        connText.setForeground(Common.TIMER_COLOR);
        connText.setFont(new Font("SansSerif", Font.PLAIN, 10));
        Common.insertInPanel(connText, sp, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(connStatus, sp, gbc, 1, 0, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(5, 40, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(sp, top, gbc, 1, 0, 1, 1, 0.0, 0.0);
        
        // insert the timer panel
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(tp, top, gbc, 1, 0, 2, 1, 0.0, 0.0);

        // insert the partial grey panels
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel rightCorner = new JLabel(Common.getImage("top_rt.gif", parentFrame));
        JPanel fp = new JPanel(new GridBagLayout());
        fp.setBackground(Common.WPB_COLOR);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(rightCorner, fp, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(50, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(fp, top, gbc, 2, 0, 2, 1, 1.0, 0.0);

        // insert competition arena/timer panel
        gbc.insets = new Insets(10, 21, 0, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(top, this, gbc, 0, 0, 2, 1, 0.0, 0.0);

        // insert the ranks panel
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 21, 0, 0);
        Common.insertInPanel(rp, this, gbc, 0, 1, 1, 1, 0.0, 0.0);

        // insert the broadcast panel
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 21, 0, 0);
        Common.insertInPanel(bp, this, gbc, 0, 2, 1, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 21, 0, 0);
        Common.insertInPanel(leaderBoardPanel, this, gbc, 0, 3, 1, 1, 0.0, 0.0);

        /*
        if (parentFrame.getCompanyName().equals(ContestApplet.COMPANYSUN)) {
            JLabel poweredBy = new JLabel(Common.getImage("powered_tc_applet.gif", parentFrame));
            gbc.insets = new Insets(0, 21, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            Common.insertInPanel(poweredBy, this, gbc, 0, 4, 1, 1, 0.0, 0.0);
        }
        */
        /*
        if(parentFrame.getCompanyName().equals("TopCoder")) {
            JButton poweredBy = Common.getImageButton("tcs_applet_badge.gif", parentFrame);
            poweredBy.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                    TopCoderSoftwareButtonClick();
                }
            }
            );
            gbc.insets = new Insets(0, 3, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            Common.insertInPanel(poweredBy, this, gbc, 0, 3, 1, 1, 0.0, 0.0);
            //Common.insertInPanel(registerButton, panel, gbc, 1, 0, 1, 1);
         }
        */

        // insert the partial grey panels
        JLabel leftCorner = new JLabel(Common.getImage("bottom_left.gif", parentFrame));
        JPanel fp2 = new JPanel(new GridBagLayout());
        fp2.setBackground(Common.WPB_COLOR);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        Common.insertInPanel(leftCorner, fp2, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(0, 105, 15, 0);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(fp2, this, gbc, 0, 3, 1, 1, 0.0, 1.0);

        // insert the workspace panel
        gbc.insets = new Insets(0, 0, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        Common.insertInPanel(wp, this, gbc, 1, 1, 1, 3, 1.0, 1.0);


        // hide the timer panel until it is needed
        tp.setVisible(false);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //public void TopCoderSoftwareButtonClick()
    ////////////////////////////////////////////////////////////////////////////////
    //{
    //    try{
    //      Common.showURL(parentFrame.getAppletContext(),new URL(Common.URL_REG));
    //    }catch(MalformedURLException ex){
    //        ex.printStackTrace();
    //    }
    //}
    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        cp.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public CompPanel getCompPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (cp);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public TimerPanel getTimerPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (tp);
    }
    
    public void setStatusLabel(boolean on) {
        if(on) {
            connStatus.setIcon(Common.getImage("connected.gif", parentFrame));
        } else {
            connStatus.setIcon(Common.getImage("disconnected.gif", parentFrame));
        }
        
        bp.setButtonEnabled(on);
        leaderBoardPanel.setButtonEnabled(on);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public JPanel getWorkPanel()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (wp);
    }

    public void showTimer() {
        tp.setVisible(true);
    }
}
