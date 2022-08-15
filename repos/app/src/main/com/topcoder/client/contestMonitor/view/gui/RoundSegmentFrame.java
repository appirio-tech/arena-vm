/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundSegmentData;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RoundSegmentFrame extends InputFrame {

    private ContestManagementController controller;
    private RoundData round;

    public RoundSegmentFrame(ContestManagementController controller, JDialog parent) {
        super("Round Segments", parent);
        this.controller = controller;
        build();
    }

    private DateField regStart = new DateField(frame);
    private JTextField regLength = new JTextField(5);
    private JTextField regStatus = new JTextField(3);
    private DateField codingStart = new DateField(frame);
    private JTextField codingLength = new JTextField(5);
    private JTextField codingStatus = new JTextField(3);
    private JTextField intermissionLength = new JTextField(5);
    private JTextField intermissionStatus = new JTextField(3);
    private JTextField challengeLength = new JTextField(5);
    private JTextField challengeStatus = new JTextField(3);
    private JTextField systestStatus = new JTextField(3);

    JButton okButton, cancelButton;

    private boolean disposeMe = false;

    protected void addItems() {
        addItem("Registration Start", regStart);
        addItem("Registration Length", regLength);
        addItem("Registration Status", regStatus);
        addItem("Coding Start", codingStart);
        addItem("Coding Length", codingLength);
        addItem("Coding Status", codingStatus);
        addItem("Intermission Length", intermissionLength);
        addItem("Intermission Status", intermissionStatus);
        addItem("Challenge Length", challengeLength);
        addItem("Challenge Status", challengeStatus);
        addItem("System Status", systestStatus);
    }

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


    public void display(RoundData round) {
        this.round = round;
        RoundSegmentData segments = round.getSegments();
        regStart.setDate(segments.getRegistrationStart());
        regLength.setText("" + segments.getRegistrationLength());
        regStatus.setText("" + segments.getRegistrationStatus());
        codingStart.setDate(segments.getCodingStart());
        codingLength.setText("" + segments.getCodingLength());
        codingStatus.setText("" + segments.getCodingStatus());
        intermissionLength.setText("" + segments.getIntermissionLength());
        intermissionStatus.setText("" + segments.getIntermissionStatus());
        challengeLength.setText("" + segments.getChallengeLength());
        challengeStatus.setText("" + segments.getChallengeStatus());
        systestStatus.setText("" + segments.getSystemTestStatus());

        regLength.requestFocusInWindow();
        display();
    }

    private void commit() {
        final RoundSegmentData data = new RoundSegmentData(
                round.getId(),
                regStart.getDate(),
                Integer.parseInt(regLength.getText()),
                codingStart.getDate(),
                Integer.parseInt(codingLength.getText()),
                Integer.parseInt(intermissionLength.getText()),
                Integer.parseInt(challengeLength.getText()),
                regStatus.getText(),
                codingStatus.getText(),
                intermissionStatus.getText(),
                challengeStatus.getText(),
                systestStatus.getText()
        );
        controller.setRoundSegments(data, new WrappedResponseWaiter(getFrameWaiter()) {
            protected void _waitForResponse() {
                okButton.setEnabled(false);
                cancelButton.setEnabled(false);
            }

            protected void _errorResponseReceived(Throwable t) {
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
            }

            protected void _responseReceived() {
                round.setSegments(data);
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
                if (disposeMe) {
                    frame.dispose();
                    disposeMe = false;
                }
            }
        });
    }
}

