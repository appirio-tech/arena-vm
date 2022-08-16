/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;

import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundEventData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;

import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * <p>
 * this is the frame to add round event.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class RoundEventFrame extends InputFrame {
    /**
     * <p>
     * the regex pattern to check the input event id.
     * </p>
     */
    private static final Pattern pattern = Pattern.compile("[0-9]*");

    /**
     * <p>
     * the contest management controller.
     * </p>
     */
    private ContestManagementController controller;

    /**
     * <p>
     * the round event data entity.
     * </p>
     */
    private RoundData round;

    /**
     * <p>
     * make sure the event id must be integer.
     * </p>
     */
    private JTextField eventId = new JTextField(8);

    /**
     * <p>
     * the event name text field.
     * </p>
     */
    private JTextField eventName = new JTextField(16);

    /**
     * <p>
     * the registration url text field.
     * </p>
     */
    private JTextField registrationUrl = new JTextField(64);

    /**
     * the ok,cancel buttons.
     */
    JButton okButton;

    /**
     * the ok,cancel buttons.
     */
    JButton cancelButton;
    private boolean disposeMe = false;

    /**
     * <p>
     * the constructor of round event frame.
     * </p>
     * @param controller
     *         the contest management controller.
     * @param parent
     *         the parent dialog.
     */
    public RoundEventFrame(ContestManagementController controller, JDialog parent) {
        super("Round Event", parent);
        this.controller = controller;
        build();
    }

    /**
     * <p>
     * add the buttons to the round event frame.
     * </p>
     */
    protected void addButtons() {
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });
        addButton(cancelButton);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    disposeMe = true;
                    commit();
                }
            });
        addButton(okButton);
    }

    /**
     * <p>
     * display the record to the round event frame.
     * </p>
     * @param round
     *        the round event data.
     */
    public void display(RoundData round) {
        this.round = round;

        RoundEventData eventData = round.getEvent();

        if (eventData == null) {
            eventData = new RoundEventData();
            round.setEvent(eventData);
        }

        eventId.setText((eventData.getEventId() > 0) ? String.valueOf(eventData.getEventId()) : "");
        eventName.setText((eventData.getEventName() == null) ? "" : eventData.getEventName());
        registrationUrl.setText((eventData.getRegistrationUrl() == null) ? "" : eventData.getRegistrationUrl());
        eventId.requestFocusInWindow();
        display();
    }

    /**
     * <p>
     * the event_id,event_name,registration_url can all be empty
     * but if the event_id is not null,the other two input field
     * must be filled with none-trimmed content
     * </p>
     *
     * @return
     *    true the all input fields have passed the validation.
     *    false some fields is illegal.
     */
    private boolean validateInputFields() {
        String eventIdStr = eventId.getText();

        if ((eventIdStr == null) || (eventIdStr.trim().length() == 0)) {
            return true;
        }

        String eventNameStr = eventName.getText();
        String regUrl = registrationUrl.getText();

        return (eventNameStr != null) && (eventNameStr.trim().length() > 0) && (regUrl != null)
            && (regUrl.trim().length() > 0);
    }

    /**
     * <p>
     * check if the event id is legal
     * </p>
     * @param eventIdStr
     *        the event id
     * @return
     *        true: if the event id can be casted into integer.
     *        false: the event id is illegal.
     */
    private boolean isEventIdLegal(String eventIdStr) {
        return pattern.matcher(eventIdStr).matches();
    }

    /**
     * <p>
     * commit the round event data to server.
     * </p>
     */
    private void commit() {
        if (!validateInputFields()) {
            JOptionPane.showMessageDialog(null, "the event name or registration url can not be empty",
                "Content Needed", JOptionPane.ERROR_MESSAGE);

            return;
        }

        String eventIdStr = eventId.getText();

        //if the eventIdStr is empty, we should also need to commit to delete this record
        int inputEventId = 0;

        if ((eventIdStr != null) && (eventIdStr.trim().length() > 0)) {
            if (!isEventIdLegal(eventIdStr)) {
                JOptionPane.showMessageDialog(null, "the event id can only be number", "EventId Illegal",
                    JOptionPane.ERROR_MESSAGE);

                return;
            }

            inputEventId = Integer.parseInt(eventIdStr);
        }

        final RoundEventData data = new RoundEventData(round.getId(), inputEventId, eventName.getText(),
                registrationUrl.getText());
        controller.setRoundEvents(data,
            new WrappedResponseWaiter(getFrameWaiter()) {
                protected void _waitForResponse() {
                    okButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }

                protected void _errorResponseReceived(Throwable t) {
                    okButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }

                protected void _responseReceived() {
                    round.setEvent(data);
                    okButton.setEnabled(true);
                    cancelButton.setEnabled(true);

                    if (disposeMe) {
                        frame.dispose();
                        disposeMe = false;
                    }
                }
            });
    }

    /**
     * <p>
     * add the fields to the round event frame.
     * </p>
     */
    protected void addItems() {
        addItem("Event ID", eventId);
        addItem("Event Name", eventName);
        addItem("Registration Url", registrationUrl);
    }
}
