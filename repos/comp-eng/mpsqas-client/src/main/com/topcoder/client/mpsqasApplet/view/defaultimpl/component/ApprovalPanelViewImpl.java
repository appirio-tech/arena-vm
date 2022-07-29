package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.view.component.ApprovalPanelView;
import com.topcoder.client.mpsqasApplet.controller.component.ApprovalPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ApprovalPanelModel;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;

import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of the approval panel view.  Gives the user a
 * check box and text area for the user to approve or disprove something
 * with a message.
 *
 * @author mitalub
 */
public class ApprovalPanelViewImpl extends ApprovalPanelView {

    private ApprovalPanelController controller;
    private ApprovalPanelModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JTextArea messageTextArea;
    private JCheckBox acceptedCheckBox;
    private JButton submitButton;

    public void init() {
        setLayout(layout = new GridBagLayout());
        gbc = new GridBagConstraints();
    }

    public void setController(ComponentController controller) {
        this.controller = (ApprovalPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (ApprovalPanelModel) model;
        model.addWatcher(this);
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            JLabel title = new JLabel("Pending Reply:");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(title, gbc);
            add(title);

            acceptedCheckBox = new JCheckBox("Accept: ", true);
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(acceptedCheckBox, gbc);
            add(acceptedCheckBox);

            JLabel messageLabel = new JLabel("Message:");
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 1);
            layout.setConstraints(messageLabel, gbc);
            add(messageLabel);

            messageTextArea = new JTextArea();
            messageTextArea.setLineWrap(true);
            messageTextArea.setWrapStyleWord(true);
            JScrollPane messageScrollPane = new JScrollPane(messageTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 100);
            layout.setConstraints(messageScrollPane, gbc);
            add(messageScrollPane);

            submitButton = new JButton("Submit");
            submitButton.addActionListener(new AppletActionListener(
                    "processSubmit", controller, false));
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            GUIConstants.buildConstraints(gbc, 0, 3, 2, 1, 0, 1);
            layout.setConstraints(submitButton, gbc);
            add(submitButton);
        }
    }

    public String getName() {
        return "Pending Reply";
    }

    public boolean isAccepted() {
        return acceptedCheckBox.isSelected();
    }

    public String getMessage() {
        return messageTextArea.getText();
    }
}
