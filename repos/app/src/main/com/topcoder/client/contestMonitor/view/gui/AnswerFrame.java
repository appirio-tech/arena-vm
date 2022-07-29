/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.QuestionData;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnswerFrame extends InputFrame {

    private ContestManagementController controller;
    private boolean committed = false;
    private QuestionData question;
    private AnswerData answer;
    private JButton okButton, cancelButton;

    public AnswerFrame(ContestManagementController controller, JDialog parent) {
        super("Answer", parent);
        this.controller = controller;
        build();
    }


    private JTextArea text = new JTextArea(10, 30);
    private JTextField sortOrder = new JTextField(3);
    private JCheckBox correct = new JCheckBox();

    protected void addItems() {
        addItem("Sort Order", sortOrder);
        addItem("Correct", correct);
        text.setLineWrap(true);
        addItem("Text", text);
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
                commit();
                frame.dispose();
            }
        });
        addButton(okButton);

        frame.pack();
    }


    public void display(QuestionData question, AnswerData answer, boolean committed) {
        this.committed = committed;
        this.question = question;
        this.answer = answer;
        sortOrder.setText("" + answer.getSortOrder());
        text.setText(answer.getText());
        correct.setSelected(answer.isCorrect());
        sortOrder.requestFocusInWindow();
        super.display();
    }


    private void commit() {
        final AnswerData data = new AnswerData(
                answer.getId(),
                text.getText(),
                Integer.parseInt(sortOrder.getText()),
                correct.isSelected()
        );
        ResponseWaiter waiter = new WrappedResponseWaiter(getFrameWaiter()) {
            protected void _waitForResponse() {
                okButton.setEnabled(false);
                cancelButton.setEnabled(false);
            }

            protected void _errorResponseReceived(Throwable t) {
                okButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }

            protected void _responseReceived() {
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
                committed = true;
                answer = data;
                frame.dispose();
            }
        };
        if (committed) {
            controller.modifyAnswer(question, data, waiter);
        } else {
            controller.addAnswer(question, data, waiter);
        }
    }
}

