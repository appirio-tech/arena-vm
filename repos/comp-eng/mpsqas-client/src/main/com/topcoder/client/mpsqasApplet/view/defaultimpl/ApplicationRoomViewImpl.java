package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ApplicationRoomView;
import com.topcoder.client.mpsqasApplet.controller.ApplicationRoomController;
import com.topcoder.client.mpsqasApplet.model.ApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.netCommon.mpsqas.MessageConstants;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A panel through which a user can fill out an application to be a problem
 * developed or an admin can approve / disprove of a memebers application.
 *
 * @author mitalub
 */
public class ApplicationRoomViewImpl extends JPanelView
        implements ApplicationRoomView {

    private ApplicationRoomModel model;
    private ApplicationRoomController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JLabel titleLabel;
    private JLabel messageLabel;
    private JTextArea messageArea;
    private JScrollPane messagePane;
    private JButton submitButton;

    public void init() {
        model = MainObjectFactory.getApplicationRoomModel();
        controller = MainObjectFactory.getApplicationRoomController();

        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
        model.addWatcher(this);
    }

    public void update(Object arg) {
        removeAll();

        int type = model.getType();
        if (type == MessageConstants.TESTER_APPLICATION) {
            titleLabel = new JLabel("Tester Application:");
        } else {
            titleLabel = new JLabel("Writer Application:");
        }
        titleLabel.setFont(DefaultUIValues.HEADER_FONT);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        layout.setConstraints(titleLabel, gbc);
        add(titleLabel);

        messageLabel = new JLabel(
                "Enter any comments you have about your application:");
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
        layout.setConstraints(messageLabel, gbc);
        add(messageLabel);

        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messagePane = new JScrollPane(messageArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 100);
        layout.setConstraints(messagePane, gbc);
        add(messagePane);

        submitButton = new JButton("Submit Application");
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
        layout.setConstraints(submitButton, gbc);
        add(submitButton);

        submitButton.addActionListener(new AppletActionListener(
                "processSendApplication", controller, false));
    }

    /** Returns the contents of the TextArea. */
    public String getContents() {
        return messageArea.getText();
    }
}

