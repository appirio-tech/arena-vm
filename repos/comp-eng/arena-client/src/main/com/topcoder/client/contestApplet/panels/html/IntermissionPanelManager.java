package com.topcoder.client.contestApplet.panels.html;

import java.util.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contestantMessages.UserInfo;


public class IntermissionPanelManager {

    public static final int DEFAULT_INTERMISSION_PANEL = 0;
    public static final int LOGIN_INTERMISSION_PANEL = 1;
    public static final int MOVE_INTERMISSION_PANEL = 2;

    private HTMLPanel defaultPanel;
    private HTMLPanel ratedLoginPanel;
    private HTMLPanel unratedLoginPanel;
    private HTMLPanel firstTimeLoginPanel;
    private HTMLPanel ratedMovePanel;
    private HTMLPanel unratedMovePanel;
    private HTMLPanel firstTimeMovePanel;

    private ContestApplet ca;

    public IntermissionPanelManager(ContestApplet _ca) {
        this.ca = _ca;

        try {
            defaultPanel = new HTMLPanel("images/default.html", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }

        firstTimeMovePanel =
                ratedMovePanel =
                unratedMovePanel =
                ratedLoginPanel =
                unratedLoginPanel =
                firstTimeLoginPanel =
                defaultPanel;


        Properties prop = new Properties();
        String propFile = "intermission.properties";
        try {
            prop.load(IntermissionPanelManager.class.getClassLoader().getResourceAsStream(propFile));
        } catch (Exception e) {
            System.err.println("Error loading " + propFile);
            e.printStackTrace();
            return;
        }

        String URLBase = prop.getProperty("URLBase");
        ;
        String value;

        value = prop.getProperty("ratedLoginPage");
        try {
            ratedLoginPanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + URLBase + value);
            e.printStackTrace();
        }

        value = prop.getProperty("unratedLoginPage");
        try {
            unratedLoginPanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + URLBase + value);
            e.printStackTrace();
        }

        value = prop.getProperty("firstTimeLoginPage");
        try {
            firstTimeLoginPanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + URLBase + value);
            e.printStackTrace();
        }

        value = prop.getProperty("unratedMovePage");
        try {
            unratedMovePanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + URLBase + value);
            e.printStackTrace();
        }

        value = prop.getProperty("ratedMovePage");
        try {
            ratedMovePanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + value);
            e.printStackTrace();
        }

        value = prop.getProperty("firstTimeMovePage");
        try {
            firstTimeMovePanel = new HTMLPanel(URLBase + value, ca);
        } catch (Exception e) {
            System.err.println("Error loading " + value);
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    public JPanel getIntermissionPanel(final int type)
            ////////////////////////////////////////////////////////////////////////////////
    {
        UserInfo userInfo = ca.getModel().getUserInfo();
        HTMLPanel r = null;
        switch (type) {
        case LOGIN_INTERMISSION_PANEL:
            if (userInfo.isFirstTimeUser())
                r = firstTimeLoginPanel;
            else if (userInfo.getRating() > 0)
                r = ratedLoginPanel;
            else
                r = unratedLoginPanel;
            break;
        case MOVE_INTERMISSION_PANEL:
            if (userInfo.isFirstTimeUser())
                r = firstTimeMovePanel;
            else if (userInfo.getRating() > 0)
                r = ratedMovePanel;
            else
                r = unratedMovePanel;
            break;
        default:
            r = defaultPanel;
        }

        r.replaceVariables(new String[]{
            "$USERNAME",
            "$RATING",
            "$NUMRATEDEVENTS",
            "$LASTLOGIN",
            "$EVENT"
        }, new String[]{
            userInfo.getHandle(),
            "" + userInfo.getRating(),
            "" + userInfo.getNumRatedEvents(),
            userInfo.isFirstTimeUser() ? "" : Common.formatTime(userInfo.getLastLogin()),
            "event" + (userInfo.getNumRatedEvents() != 1 ? "s" : "")
        });
        r.revalidate();
        r.repaint();
        return r;
    }
}
