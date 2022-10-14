package com.topcoder.client.contestApplet.rooms;

/*
* LoginRoom.java
*
* Created on July 12, 2000, 4:08 PM
*/

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.event.*;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.*;
import com.topcoder.client.contestant.*;

/**
 *
 * @author  Alex Roman
 * @version
 */

public final class TopCoderHSLoginRoom extends RoomModule {

    // login status
    private boolean loginEnabled = true;

    // declared global for handling/referencing
    private JPanel panel = new JPanel(new GridBagLayout());
    private JTextField userName = new JTextField();
    private JPasswordField passWord = new JPasswordField();
    private JButton loginButton = null;
    //private JButton guestButton = null;
    private JTextArea legalese = new JTextArea("Any use of the TopCoder Arena, including the practice area, is limited to personal, " +
            "non-commercial or educational purposes only.  If you wish to utilize the TopCoder Arena, " +
            "or any TopCoder information, including statistical information, for commercial purposes, including, " +
            "but not limited to, recruiting, testing or training, please contact TopCoder by email: " +
            "service@topcoder.com or by phone: 860-633-5540.  By logging into the arena, you indicate your agreement " +
            "to these terms as well as those specified in the TopCoder Terms of Service on our website.", 5, 40);
    private JTextField legalese2 = new JTextField("All content on the website and in the arena (c)2003 TopCoder, Inc.  All Rights Reserved", 40);
    private JTextField legalese3 = new JTextField("Protected by U.S. patent number 6,569,012", 40);
    private JTextArea tunnelWarn = new JTextArea("**HTTP Tunneling is significantly slower than connecting directly and should only be used by people who are unable to connect otherwise.", 2, 40);
    private JCheckBox tunnel = null;

    // listeners
    private ActionListener lb_al = null;
    private ActionListener tu_al = null;
    private ActionListener un_al = null;
    private ActionListener pw_al = null;
    private DocumentListener documentListener;
    private ContestApplet ca;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public TopCoderHSLoginRoom(ContestApplet parent)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(parent, ContestConstants.LOGIN_ROOM);
        ca = parent;
        create();
    }


    /**
     * return the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public JPanel reload()
            ////////////////////////////////////////////////////////////////////////////////
    {
        clear();
        return (panel);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void enter()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //guestButton.setEnabled(true);
        loginButton.addActionListener(lb_al);
        userName.addActionListener(un_al);
        passWord.addActionListener(pw_al);
        tunnel.addActionListener(tu_al);
        userName.getDocument().addDocumentListener(documentListener);
        passWord.getDocument().addDocumentListener(documentListener);
        resetFocus();
    }



    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();
        panel.setBackground(Common.BG_COLOR);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel sponsorPanel = new ContestSponsorPanel(ca, null, CommonData.getSponsorLoginImageAddr(ca.getCompanyName()));
        Common.insertInPanel(sponsorPanel, panel, gbc, 0, 0, 3, 1, 0.1, 0.1);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        Common.insertInPanel(createLoginPanel(339, 166, Common.BG_COLOR), panel, gbc, 0, 1, 3, 1, 0.1, 0.0);

        gbc.anchor = GridBagConstraints.NORTH;
        Common.insertInPanel(createRegisterPanel(265, 50, Common.BG_COLOR), panel, gbc, 0, 2, 3, 1, 0.1, 0.0);

        legalese.setEditable(false);
        legalese.setLineWrap(true);
        legalese.setWrapStyleWord(true);
        legalese.setForeground(Common.ID_COLOR);
        legalese.setBackground(Common.BG_COLOR);
        legalese.setPreferredSize(new Dimension(0, 0));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(5, 25, 5, 25);
        Common.insertInPanel(legalese, panel, gbc, 0, 3, 3, 1, 0.0, 0.2);

        legalese2.setEditable(false);
        legalese2.setForeground(Common.ID_COLOR);
        legalese2.setBackground(Common.BG_COLOR);
        legalese2.setHorizontalAlignment(JTextField.CENTER);
        legalese2.setBorder(null);

        legalese3.setEditable(false);
        legalese3.setForeground(Common.ID_COLOR);
        legalese3.setBackground(Common.BG_COLOR);
        legalese3.setHorizontalAlignment(JTextField.CENTER);
        legalese3.setBorder(null);

        tunnelWarn.setEditable(false);
        tunnelWarn.setForeground(Common.ID_COLOR);
        tunnelWarn.setBackground(Common.BG_COLOR);
        tunnelWarn.setLineWrap(true);
        tunnelWarn.setWrapStyleWord(true);
        tunnelWarn.setPreferredSize(new Dimension(0, 0));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(15, 25, 5, 25);
        Common.insertInPanel(tunnelWarn, panel, gbc, 1, 4, 1, 1, 0.1, 0.0);
        Common.insertInPanel(legalese3, panel, gbc, 1, 5, 1, 1, 0.1, 0.0);
        Common.insertInPanel(legalese2, panel, gbc, 1, 6, 1, 1, 0.1, 0.0);
    }




    //------------------------------------------------------------------------------
    // Customized pane creation
    //------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    private JPanel createLoginPanel(int width, int height, Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ImageIconPanel panel = new ImageIconPanel(new GridBagLayout(), Common.getImage("login.gif", parentFrame));
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        Common.insertInPanel(createInnerLoginPanel(230, 125), panel, gbc, 0, 0, 1, 1);

        return (panel);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public boolean leave()
            ////////////////////////////////////////////////////////////////////////////////
    {
        loginButton.removeActionListener(lb_al);
        userName.removeActionListener(un_al);
        passWord.removeActionListener(pw_al);
        tunnel.removeActionListener(tu_al);
        userName.getDocument().removeDocumentListener(documentListener);
        passWord.getDocument().removeDocumentListener(documentListener);

        return (true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void resetFocus()
            ////////////////////////////////////////////////////////////////////////////////
    {
        MoveFocus.moveFocus(passWord);
        MoveFocus.moveFocus(userName);
    }

    /**
     * Clear out all room data
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        loginEnabled = true;
        userName.setText("");
        passWord.setText("");
    }


    ////////////////////////////////////////////////////////////////////////////////
    private JPanel createInnerLoginPanel(int width, int height)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setOpaque(false);

        //loginButton = createAButton("");
        loginButton = Common.getTextButton("");
        JLabel label1 = new JLabel("Username:");
        JLabel label2 = new JLabel("Password:");

        loginButton.setIcon(Common.getImage("go_but.gif", parentFrame));
        loginButton.setPressedIcon(Common.getImage("go_but_in.gif", parentFrame));
        loginButton.setDisabledIcon(Common.getImage("go_but_gray.gif", parentFrame));
        loginButton.setBackground(Color.white);
        loginButton.setForeground(Color.white);
        loginButton.setEnabled(false);
        label1.setForeground(Common.ID_COLOR);
        label2.setForeground(Common.ID_COLOR);

        tunnel = new JCheckBox("HTTP Tunneling**");
        tunnel.setBackground(Color.black);
        tunnel.setForeground(Color.white);
        tunnel.setContentAreaFilled(false);
        //TODO not implemented
        //tunnel.setSelected(parentFrame.getModel().getConnectionType());

        //lb_al = new al("actionPerformed", "loginButtonClick", this);
        lb_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButtonClick();
            }
        };
        //un_al = new al("actionPerformed", "userNameEvent", this);
        un_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userNameEvent();
            }
        };
        //pw_al = new al("actionPerformed", "passWordEvent", this);
        pw_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passWordEvent();
            }
        };

        tu_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //TODO not implemented
                //parentFrame.getModel().setConnectionType(((JCheckBox) (e.getSource())).isSelected());
            }
        };
        //iu_dl = new dl("insertUpdate", "loginTextType", this);
        //ru_dl = new dl("removeUpdate", "loginTextType", this);
        documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkLoginButton();
            }

            public void removeUpdate(DocumentEvent e) {
                checkLoginButton();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        };

        //loginButton.addFocusListener(new fl());
        //userName.addFocusListener(new fl());
        //passWord.addFocusListener(new fl());
        //label1.addFocusListener(new fl());
        //label2.addFocusListener(new fl());

        gbc.anchor = GridBagConstraints.SOUTH;
        Common.insertInPanel(label1, panel, gbc, 0, 0, 1, 1, 0.0, 1.0);
        Common.insertInPanel(userName, panel, gbc, 1, 0, 1, 1, 1.0, 1.0);
        gbc.anchor = GridBagConstraints.NORTH;
        Common.insertInPanel(label2, panel, gbc, 0, 1, 1, 1, 0.0, 0.0);
        Common.insertInPanel(passWord, panel, gbc, 1, 1, 1, 1, 1.0, 0.0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(loginButton, panel, gbc, 1, 2, 1, 1, 1.0, 1.0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(tunnel, panel, gbc, 0, 2, 1, 1, 0.0, 0.0);
        return (panel);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private JPanel createRegisterPanel(int width, int height, Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        JLabel label1 = new JLabel("New User ?");
        JButton registerButton = Common.getTextButton("Click here to Register");
        //JLabel label2 = new JLabel("or log in as a");
        //guestButton = Common.getTextButton("guest.");

        label1.setForeground(Common.ID_COLOR);
        //label2.setForeground(Common.ID_COLOR);

        //guestButton.setToolTipText("Login as a Guest. Username/password not required");
        registerButton.setToolTipText("Register for a username/password");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerButtonClick();
            }
        }
        );

        //guestButton.addActionListener(new ActionListener(){
        //   public void actionPerformed(ActionEvent e) {
        //        guestButtonClick();
        //    }
        //});

        Common.insertInPanel(label1, panel, gbc, 0, 0, 1, 1);
        Common.insertInPanel(registerButton, panel, gbc, 1, 0, 1, 1);
        //Common.insertInPanel(label2, panel, gbc, 2, 0, 1, 1);
        //Common.insertInPanel(guestButton, panel, gbc, 3, 0, 1, 1);

        return (panel);
    }



    //------------------------------------------------------------------------------
    // Event Handling
    //------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void loginButtonClick()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (loginEnabled) {
            loginEnabled = false;
            try {
                parentFrame.getModel().login(userName.getText(), passWord.getPassword(), null);
            } catch (LoginException e) {
                parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
                parentFrame.getModel().reset();
            }
            if (parentFrame.getModel().isLoggedIn()) {
                parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                        IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
            }
            loginEnabled = true;
            clear();
            resetFocus();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    public void clearButtonClick(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        clear();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void registerButtonClick()
            ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_REG_HS));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //private synchronized void guestButtonClick()
    ////////////////////////////////////////////////////////////////////////////////
    //{
    //    //guestButton.setEnabled(false);
    //
    //    if ( loginEnabled ) {
    //       loginEnabled = false;
    //        try {
    //            parentFrame.getModel().guestLogin();
    //        }
    //        catch (LoginException e) {
    //            parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
    //            parentFrame.getModel().reset();
    //        }
    //        if (parentFrame.getModel().isLoggedIn())
    //            parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
    //        loginEnabled = true;
    //        clear();
    //        resetFocus();
    //    }
    //}

    ////////////////////////////////////////////////////////////////////////////////
    private void userNameEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (!isUsernameEmpty()) {
            MoveFocus.moveFocus(passWord);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void passWordEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (isEnabled()) {
            loginButton.doClick();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void checkLoginButton()
            ////////////////////////////////////////////////////////////////////////////////
    {
        boolean enabled = userName.getText().length() > 0 && passWord.getPassword().length > 0;
        loginButton.setEnabled(enabled);
    }

    private boolean isUsernameEmpty() {
        return userName.getText().length() <= 0;
    }

    private boolean isEnabled() {
        return !isUsernameEmpty() && passWord.getPassword().length > 0;
    }

    protected void addViews() {
    }

    void clearViews() {
    }

}
