package com.topcoder.client.contestApplet.rooms;

/*
* LoginRoom.java
*
* Created on July 12, 2000, 4:08 PM
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.LoginException;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 *
 * @author  Alex Roman
 * @version
 */
public final class ShowdownLoginRoom extends RoomModule {

    // declared global for handling/referencing
    private final JPanel panel = new JPanel(new GridBagLayout());

    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private JButton loginButton;
    //private JButton guestButton;
    private final JTextArea legalese = new JTextArea("", 5, 40);
    /*
    private final JTextArea legalese = new JTextArea(
            "Any use of the TopCoder Arena, including the practice area, is limited to personal, " +
            "non-commercial or educational purposes only.  If you wish to utilize the TopCoder Arena, " +
            "or any TopCoder information, including statistical information, for commercial purposes, including, " +
            "but not limited to, recruiting, testing or training, please contact TopCoder by email: " +
            "service@topcoder.com or by phone: 860-633-5540.  By logging into the arena, you indicate your agreement " +
            "to these terms as well as those specified in the TopCoder Terms of Service on our website.", 5, 40);
    */
    private final JTextField legalese2 = new JTextField(
            "All content in the arena (c)2008 TopCoder, Inc.  All Rights Reserved", 40);
    
    /*
    private final JTextArea tunnelWarn = new JTextArea("**HTTP Tunneling is significantly slower than connecting directly and " +
            "should only be used by people who are unable to connect otherwise.", 2, 40);
    private final JCheckBox tunnel = new JCheckBox("HTTP Tunneling**");
    */

    // listeners
    private ActionListener lb_al;
    //private ActionListener tu_al;
    private ActionListener un_al;
    private ActionListener pw_al;
    private DocumentListener documentListener;
    private final ContestApplet ca;

    // login status
    private boolean loginEnabled = true;

    /**
     * Class constructor
     */
    public ShowdownLoginRoom(ContestApplet parent) {
        super(parent, ContestConstants.LOGIN_ROOM);
        ca = parent;
        create();
    }

    /**
     * return the room
     */
    public JPanel reload() {
        clear();
        return panel;
    }

    public void enter() {
        //guestButton.setEnabled(true);
        loginButton.addActionListener(lb_al);
        usernameField.addActionListener(un_al);
        passwordField.addActionListener(pw_al);
        //tunnel.addActionListener(tu_al);
        usernameField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);
        resetFocus();
    }

    /**
     * Create the room
     */
    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();
        panel.setBackground(Common.BG_COLOR);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel sponsorPanel = new ContestSponsorPanel(ca, CommonData.getSponsorLoginImageAddr(ca.getSponsorName()));
        Common.insertInPanel(sponsorPanel, panel, gbc, 0, 0, 3, 1, 0.1, 0.1);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        Common.insertInPanel(createLoginPanel(), panel, gbc, 0, 1, 3, 1, 0.1, 0.0);

        gbc.anchor = GridBagConstraints.NORTH;
        Common.insertInPanel(createGuestPanel(), panel, gbc, 0, 2, 3, 1, 0.1, 0.0);

        legalese.setEditable(false);
        legalese.setLineWrap(true);
        legalese.setWrapStyleWord(true);
        legalese.setForeground(Common.ID_COLOR);
        legalese.setBackground(Common.BG_COLOR);
        legalese.setPreferredSize(new Dimension(0, 0));
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 25, 5, 25);
        //Common.insertInPanel(legalese, panel, gbc, 0, 3, 3, 1, 0.0, 0.2);
        
        JLabel lbl = new JLabel("*** By logging into the arena you are agreeing to these rules and regulations ***");
        lbl.setForeground(Common.ID_COLOR);
        
        //gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(lbl, panel, gbc, 0, 4, 3, 1, 0, 0);
        
        gbc.anchor = GridBagConstraints.CENTER;
        String termsText = getTerms();
        
        JTextArea terms = new JTextArea(termsText);
        terms.setEditable(false);
        terms.setForeground(Common.ID_COLOR);
        terms.setBackground(Common.BG_COLOR);
        terms.setLineWrap(true);
        terms.setWrapStyleWord(true);
        
        JScrollPane scroller = new JScrollPane(terms,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 25, 5, 25);
        Common.insertInPanel(scroller, panel, gbc, 0, 5, 3, 1, 1, 1);

        legalese2.setEditable(false);
        legalese2.setForeground(Common.ID_COLOR);
        legalese2.setBackground(Common.BG_COLOR);
        legalese2.setHorizontalAlignment(JTextField.CENTER);
        legalese2.setBorder(null);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(15, 25, 5, 25);
        //Common.insertInPanel(tunnelWarn, panel, gbc, 1, 4, 1, 1, 0.1, 0.0);
        Common.insertInPanel(legalese2, panel, gbc, 1, 6, 1, 1, 0.1, 0.0);
        
    }

    //------------------------------------------------------------------------------
    // Customized pane creation
    //------------------------------------------------------------------------------

    private String getTerms() {
        String text = "";
        try {
            text = Common.getURLContent(new URL(System.getProperty("com.topcoder.showdown.termsURL")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private JPanel createLoginPanel() {
        int width = 339;
        int height = 230;
        Color color = Common.BG_COLOR;

        //JPanel panel = new ImageIconPanel(new GridBagLayout(), Common.getImage("login.gif", parentFrame));
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        Common.insertInPanel(createInnerLoginPanel(), panel, gbc, 0, 0, 1, 1);

        return panel;
    }

    public boolean leave() {
        loginButton.removeActionListener(lb_al);
        usernameField.removeActionListener(un_al);
        passwordField.removeActionListener(pw_al);
        //tunnel.removeActionListener(tu_al);
        usernameField.getDocument().removeDocumentListener(documentListener);
        passwordField.getDocument().removeDocumentListener(documentListener);

        return (true);
    }

    public void resetFocus() {
        MoveFocus.moveFocus(passwordField);
        MoveFocus.moveFocus(usernameField);
    }

    /**
     * Clear out all room data
     */
    public void clear() {
        loginEnabled = true;
        usernameField.setText("");
        passwordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    private JPanel createInnerLoginPanel() {
        int width = 230;
        int height = 300;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,2,2,2);

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setOpaque(false);

        loginButton = Common.getTextButton("");
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel phoneLabel = new JLabel("*Cell Phone:");
        JLabel emptyLabel = new JLabel("");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        loginButton.setIcon(Common.getImage("go_but.gif", parentFrame));
        loginButton.setPressedIcon(Common.getImage("go_but_in.gif", parentFrame));
        loginButton.setDisabledIcon(Common.getImage("go_but_gray.gif", parentFrame));
        loginButton.setBackground(Color.white);
        loginButton.setForeground(Color.white);
        loginButton.setEnabled(false);

        firstNameLabel.setForeground(Common.ID_COLOR);
        lastNameLabel.setForeground(Common.ID_COLOR);
        emailLabel.setForeground(Common.ID_COLOR);
        usernameLabel.setForeground(Common.ID_COLOR);
        phoneLabel.setForeground(Common.ID_COLOR);
        passwordLabel.setForeground(Common.ID_COLOR);

        /*
        tunnel.setBackground(Color.black);
        tunnel.setForeground(Color.white);
        tunnel.setContentAreaFilled(false);
        tunnel.setSelected(parentFrame.getModel().getTunnel());
        */

        lb_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButtonClick();
            }
        };
        un_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userNameEvent();
            }
        };
        pw_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passWordEvent();
            }
        };
        /*
        tu_al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentFrame.getModel().setTunnel(((JCheckBox) (e.getSource())).isSelected());
            }
        };
        */
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

        gbc.anchor = GridBagConstraints.NORTH;

        int firstNameY = 0;
        Common.insertInPanel(firstNameLabel, panel, gbc, 0, firstNameY, 1, 1, 0.0, 1.0);
        Common.insertInPanel(firstNameField, panel, gbc, 1, firstNameY, 1, 1, 1.0, 1.0);

        int lastNameY = firstNameY + 1;
        Common.insertInPanel(lastNameLabel, panel, gbc, 0, lastNameY, 1, 1, 0.0, 1.0);
        Common.insertInPanel(lastNameField, panel, gbc, 1, lastNameY, 1, 1, 1.0, 1.0);

        int emailY = lastNameY + 1;
        Common.insertInPanel(emailLabel, panel, gbc, 0, emailY, 1, 1, 0.0, 1.0);
        Common.insertInPanel(emailField, panel, gbc, 1, emailY, 1, 1, 1.0, 1.0);
        
        int phoneY = emailY + 1;
        Common.insertInPanel(phoneLabel, panel, gbc, 0, phoneY, 1, 1, 0.0, 1.0);
        Common.insertInPanel(phoneField, panel, gbc, 1, phoneY, 1, 1, 1.0, 1.0);

        JTextArea phoneInfo = new JTextArea(
            "* Your cell phone number will only be used for us to easily contact you in the event that you are one of the daily winners.", 3, 40);
        
        phoneInfo.setEditable(false);
        phoneInfo.setLineWrap(true);
        phoneInfo.setWrapStyleWord(true);
        phoneInfo.setForeground(Color.RED);
        phoneInfo.setBackground(Common.BG_COLOR);
        phoneInfo.setPreferredSize(new Dimension(0, 0));
        phoneInfo.setFont(phoneLabel.getFont());
        
        int piY = phoneY + 1;
        Common.insertInPanel(phoneInfo, panel, gbc, 0, piY, 2, 1, 0.0, 1.0);
        
        int emptyY = piY + 1;
        Common.insertInPanel(emptyLabel, panel, gbc, 0, emptyY, 1, 1, 0.0, 1.0);

        int usernameY = emptyY + 1;
        Common.insertInPanel(usernameLabel, panel, gbc, 0, usernameY, 1, 1, 0.0, 1.0);
        Common.insertInPanel(usernameField, panel, gbc, 1, usernameY, 1, 1, 1.0, 1.0);

        int passwordY = usernameY + 1;
        Common.insertInPanel(passwordLabel, panel, gbc, 0, passwordY, 1, 1, 0.0, 0.0);
        Common.insertInPanel(passwordField, panel, gbc, 1, passwordY, 1, 1, 1.0, 1.0);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        int loginButtonY = passwordY + 1;
        Common.insertInPanel(loginButton, panel, gbc, 1, loginButtonY, 1, 1, 1.0, 1.0);
                
        
        /*
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(tunnel, panel, gbc, 0, loginButtonY, 1, 1, 0.0, 0.0);
        */

        return panel;
    }

    private static JPanel createGuestPanel() {
        int width = 300;
        int height = 30;
        Color color = Common.BG_COLOR;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;

        panel.setMinimumSize(new Dimension(width, height));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        JLabel label1 = new JLabel("New User? Fill in all fields.");
        //JButton registerButton = Common.getTextButton(" Register");
        //JLabel label2 = new JLabel("or log in as a");
        JLabel label2 = new JLabel("Returning User? Just provide Username / Password.");
        //guestButton = Common.getTextButton("guest.");

        label1.setForeground(Common.ID_COLOR);
        label2.setForeground(Common.ID_COLOR);

        //guestButton.setToolTipText("Login as a Guest. Username/password not required");
        //registerButton.setToolTipText("Register for a username/password");

        /*
        guestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guestButtonClick();
            }
        });
        */

        Common.insertInPanel(label1, panel, gbc, 0, 0, 1, 1);
        //Common.insertInPanel(registerButton, panel, gbc, 1, 0, 1, 1);
        Common.insertInPanel(label2, panel, gbc, 0, 1, 1, 1);
        //Common.insertInPanel(guestButton, panel, gbc, 3, 0, 1, 1);

        return panel;
    }

    //------------------------------------------------------------------------------
    // Event Handling
    //------------------------------------------------------------------------------

    private synchronized void loginButtonClick() {
        if (loginEnabled) {
            loginEnabled = false;
            Contestant model = parentFrame.getModel();
            try {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String cellPhone = phoneField.getText();
                String email = emailField.getText();
                parentFrame.getModel().setConnectionType(ConnectionType.DIRECT);
                model.loginWithEmail(username, password, firstName, lastName, email, ContestConstants.COMPANY_SUN, cellPhone);
            } catch (LoginException e) {
                parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
                model.reset();
            }
            if (model.isLoggedIn()) {
                //check for new messages
                parentFrame.showImportantMessages();
                
                parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                        IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
                clear();
            } else {
                loginEnabled = true;
            }
            resetFocus();
        }
    }

    /*
    public void clearButtonClick(ActionEvent e) {
        clear();
    }

    private synchronized void guestButtonClick() {
        guestButton.setEnabled(false);

        if (loginEnabled) {
            loginEnabled = false;
            try {
                parentFrame.getModel().guestLogin();
            } catch (LoginException e) {
                parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
                parentFrame.getModel().reset();
            }
            if (parentFrame.getModel().isLoggedIn())
                parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                        IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
            loginEnabled = true;
            clear();
            resetFocus();
        }
    }
    */

    private void userNameEvent() {
        if (!isUsernameEmpty()) {
            MoveFocus.moveFocus(passwordField);
        }
    }

    private void passWordEvent() {
        if (isEnabled()) {
            loginButton.doClick();
        }
    }

    private void checkLoginButton() {
        boolean enabled = usernameField.getText().length() > 0 && passwordField.getPassword().length > 0;
        loginButton.setEnabled(enabled);
    }

    private boolean isUsernameEmpty() {
        return usernameField.getText().length() <= 0;
    }

    private boolean isEnabled() {
        return !isUsernameEmpty() && passwordField.getPassword().length > 0;
    }

    protected void addViews() {
    }

    void clearViews() {
    }

}
