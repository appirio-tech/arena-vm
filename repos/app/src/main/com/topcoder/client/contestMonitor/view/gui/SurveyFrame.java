/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.SurveyData;
import com.topcoder.server.contest.SurveyStatus;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

public class SurveyFrame extends InputFrame {

    private ContestManagementController controller;
    private SurveyData survey;
    private RoundData round;

    private boolean disposeMe = false;

    public SurveyFrame(ContestManagementController controller, JDialog parent) {
        super("Survey", parent);
        this.controller = controller;
        build();
    }

    private JTextField name = new JTextField(20);
    private DateField startDate = new DateField(frame);
    private JComboBox status = new JComboBox();
    private JTextField length = new JTextField(5);
    private JTextArea text = new JTextArea(10, 30);

    private JButton okButton, cancelButton;

    protected void addItems() {
        addItem("Name", name);
        addItem("Start Date", startDate);
        addItem("Length", length);
        addItem("Status", status);
        text.setLineWrap(true);
        addItem("Text", text, .1, .1, GridBagConstraints.HORIZONTAL);
    }

    protected void addButtons() {
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
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
        // reload each time, just in case
        status.removeAllItems();
        Collection c = controller.getSurveyStatusTypes();
        for (Iterator it = c.iterator(); it.hasNext();) {
            status.addItem(it.next());
        }
        this.round = round;
        survey = round.getSurvey();
        name.setText(survey.getName());
        startDate.setDate(survey.getStartDate());
        length.setText("" + survey.getLength());
        status.setSelectedItem(survey.getStatus());
        text.setText(survey.getText());
        name.requestFocusInWindow();

        super.display();
    }


    private void commit() {
        final SurveyData data = new SurveyData(
                survey.getId(),
                name.getText(),
                text.getText(),
                startDate.getDate(),
                Integer.parseInt(length.getText()),
                (SurveyStatus) status.getSelectedItem()
        );
        controller.setSurvey(data, new WrappedResponseWaiter(getFrameWaiter()) {
            protected void _waitForResponse() {
                okButton.setEnabled(false);
                cancelButton.setEnabled(false);
            }

            protected void _errorResponseReceived(Throwable t) {
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
            }

            protected void _responseReceived() {
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
                survey = data;
                round.setSurvey(survey);
                if (disposeMe) {
                    frame.dispose();
                    disposeMe = false;
                }
            }
        });
    }
}

