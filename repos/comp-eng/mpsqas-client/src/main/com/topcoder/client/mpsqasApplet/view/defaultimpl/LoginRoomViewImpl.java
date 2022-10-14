package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.LoginRoomView;
import com.topcoder.client.mpsqasApplet.controller.LoginRoomController;
import com.topcoder.client.mpsqasApplet.model.LoginRoomModel;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

/**
 * Implementation of Login Room view.
 *
 * @author mitalub
 */
public class LoginRoomViewImpl extends JPanelView implements LoginRoomView {

    private GridBagLayout mainLayout; //the GridBagLayout used by the main room.
    private GridBagLayout loginLayout; //the GridBagLayout used by the login panel
    private GridBagConstraints gbc;  //and its corresponding constraings

    //components:
    private JPanel loginPanel;
    private JLabel title;
    private JLabel handleLabel;
    private JTextField handleField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JComboBox connectionSelector;
    private JLabel connectionSelectorLabel;

    private LoginRoomController controller;
    private LoginRoomModel model;

    public void init() {
        model = MainObjectFactory.getLoginRoomModel();
        controller = MainObjectFactory.getLoginRoomController();

        loginPanel = new JPanel();
        loginLayout = new GridBagLayout();
        mainLayout = new GridBagLayout();
        gbc = new GridBagConstraints();
        loginPanel.setLayout(loginLayout);
        setLayout(mainLayout);

        title = new JLabel("Login:");
        title.setFont(DefaultUIValues.HEADER_FONT);

        handleLabel = new JLabel("Handle:");

        passwordLabel = new JLabel("Password:");

        statusLabel = new JLabel("");

        connectionSelectorLabel = new JLabel("Connection:");
        connectionSelector = new JComboBox(ConnectionType.getAvailableTypes());

        handleField = new JTextField(25);
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");

        gbc.insets = new Insets(5, 5, 5, 5);

        GUIConstants.buildConstraints(gbc, 1, 1, 2, 1, 0, 28);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        loginLayout.setConstraints(title, gbc);
        loginPanel.add(title);

        GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 40, 0);
        loginLayout.setConstraints(handleLabel, gbc);
        loginPanel.add(handleLabel);

        GUIConstants.buildConstraints(gbc, 2, 2, 1, 1, 60, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginLayout.setConstraints(handleField, gbc);
        loginPanel.add(handleField);

        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 0);
        loginLayout.setConstraints(passwordLabel, gbc);
        loginPanel.add(passwordLabel);

        GUIConstants.buildConstraints(gbc, 2, 3, 1, 1, 0, 18);
        loginLayout.setConstraints(passwordField, gbc);
        loginPanel.add(passwordField);

        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 0);
        loginLayout.setConstraints(connectionSelectorLabel, gbc);
        loginPanel.add(connectionSelectorLabel);

        GUIConstants.buildConstraints(gbc, 2, 4, 1, 1, 0, 18);
        loginLayout.setConstraints(connectionSelector, gbc);
        loginPanel.add(connectionSelector);

        GUIConstants.buildConstraints(gbc, 1, 6, 2, 1, 0, 18);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginLayout.setConstraints(loginButton, gbc);
        loginPanel.add(loginButton);

        GUIConstants.buildConstraints(gbc, 1, 7, 2, 1, 0, 18);
        loginLayout.setConstraints(statusLabel, gbc);
        loginPanel.add(statusLabel);

        loginPanel.setBorder(new EtchedBorder());

        handleField.addActionListener(new AppletActionListener(
                "processLoginPressed", controller, false));
        passwordField.addActionListener(new AppletActionListener(
                "processLoginPressed", controller, false));
        loginButton.addActionListener(new AppletActionListener(
                "processLoginPressed", controller, false));

        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        mainLayout.setConstraints(loginPanel, gbc);
        add(loginPanel);

        model.addWatcher(this);
    }

    /**
     * Makes gui match model by getting status message from model.
     */
    public void update(Object arg) {
        statusLabel.setText(model.getStatus());
    }

    /**
     * Returns value in Handle field.
     */
    public String getHandle() {
        return handleField.getText();
    }

    /**
     * Returns value in Password field.
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public ConnectionType getConnectionType() {
        return (ConnectionType) connectionSelector.getSelectedItem();
    }

}
