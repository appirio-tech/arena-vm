package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewApplicationRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewApplicationRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A panel through which an admin can view all the information about
 * an application and can accept or reject the application.
 *
 * @author mitalub
 */
public class ViewApplicationRoomViewImpl extends JPanelView
        implements ViewApplicationRoomView {

    private ViewApplicationRoomController controller;
    private ViewApplicationRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JLabel titleLabel;
    private JLabel handleLabel;
    private JLabel ratingLabel;
    private JLabel eventsLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel messageLabel;
    private JLabel replyLabel;
    private JTextField handleField;
    private JTextField ratingField;
    private JTextField eventsField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea messageArea;
    private JScrollPane messagePane;
    private JTextArea replyArea;
    private JScrollPane replyPane;
    private JCheckBox acceptCheck;
    private JButton submitButton;

    public void init() {
        model = MainObjectFactory.getViewApplicationRoomModel();
        controller = MainObjectFactory.getViewApplicationRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        model.addWatcher(this);
    }

    public void update(Object arg) {
        removeAll();

        ApplicationInformation info = model.getApplicationInformation();

        titleLabel = new JLabel(info.getApplicationType());
        titleLabel.setFont(DefaultUIValues.HEADER_FONT);
        gbc.anchor = gbc.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
        layout.setConstraints(titleLabel, gbc);
        add(titleLabel);

        handleLabel = new JLabel("Handle:");
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
        layout.setConstraints(handleLabel, gbc);
        add(handleLabel);

        ratingLabel = new JLabel("Rating:");
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        layout.setConstraints(ratingLabel, gbc);
        add(ratingLabel);

        eventsLabel = new JLabel("Events:");
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
        layout.setConstraints(eventsLabel, gbc);
        add(eventsLabel);

        nameLabel = new JLabel("Name:");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        layout.setConstraints(nameLabel, gbc);
        add(nameLabel);

        emailLabel = new JLabel("Email:");
        GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
        layout.setConstraints(emailLabel, gbc);
        add(emailLabel);

        messageLabel = new JLabel("Message:");
        GUIConstants.buildConstraints(gbc, 0, 6, 1, 1, 0, 1);
        layout.setConstraints(messageLabel, gbc);
        add(messageLabel);

        handleField = new JTextField(20);
        handleField.setEditable(false);
        handleField.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 100, 0);
        layout.setConstraints(handleField, gbc);
        add(handleField);

        ratingField = new JTextField(10);
        ratingField.setEditable(false);
        ratingField.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 0);
        layout.setConstraints(ratingField, gbc);
        add(ratingField);

        eventsField = new JTextField(10);
        eventsField.setEditable(false);
        eventsField.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 0);
        layout.setConstraints(eventsField, gbc);
        add(eventsField);

        nameField = new JTextField(30);
        nameField.setEditable(false);
        nameField.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 0);
        layout.setConstraints(nameField, gbc);
        add(nameField);

        emailField = new JTextField(30);
        emailField.setEditable(false);
        emailField.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 0, 0);
        layout.setConstraints(emailField, gbc);
        add(emailField);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messagePane = new JScrollPane(messageArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.fill = gbc.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 7, 2, 1, 0, 100);
        layout.setConstraints(messagePane, gbc);
        add(messagePane);

        replyLabel = new JLabel("Reply:");
        GUIConstants.buildConstraints(gbc, 0, 8, 1, 1, 0, 1);
        layout.setConstraints(replyLabel, gbc);
        add(replyLabel);

        acceptCheck = new JCheckBox("Accept");
        acceptCheck.setSelected(true);
        gbc.fill = gbc.NONE;
        gbc.anchor = gbc.EAST;
        GUIConstants.buildConstraints(gbc, 1, 8, 1, 1, 0, 0);
        layout.setConstraints(acceptCheck, gbc);
        add(acceptCheck);

        replyArea = new JTextArea();
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        replyPane = new JScrollPane(replyArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.fill = gbc.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 9, 2, 1, 0, 100);
        layout.setConstraints(replyPane, gbc);
        add(replyPane);

        submitButton = new JButton("Submit");
        gbc.fill = gbc.NONE;
        gbc.anchor = gbc.CENTER;
        GUIConstants.buildConstraints(gbc, 0, 10, 2, 1, 0, 1);
        layout.setConstraints(submitButton, gbc);
        add(submitButton);

        handleField.setText(info.getHandle());
        ratingField.setText("" + info.getRating());
        eventsField.setText("" + info.getEvents());
        nameField.setText(info.getName());
        emailField.setText(info.getEmail());
        messageArea.setText(info.getMessage());
        messageArea.setCaretPosition(0);

        submitButton.addActionListener(new AppletActionListener("processReply",
                controller, false));
    }

    public boolean accepted() {
        return acceptCheck.isSelected();
    }

    public String getMessage() {
        return replyArea.getText();
    }
}

