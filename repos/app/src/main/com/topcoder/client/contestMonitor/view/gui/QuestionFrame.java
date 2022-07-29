/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.QuestionStyle;
import com.topcoder.server.contest.QuestionType;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.SurveyStatus;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

public class QuestionFrame extends InputFrame {

    private ContestManagementController controller;
    private boolean committed = false;
    private RoundData round;
    private QuestionData question;
    private AnswerSelectionFrame answerSelectionFrame;

    private boolean disposeMe = false;


    public QuestionFrame(ContestManagementController controller, JDialog parent) {
        super("Question", parent);
        this.controller = controller;
        this.answerSelectionFrame = new AnswerSelectionFrame(controller, frame);
        build();
    }

    private JComboBox type = new JComboBox();
    private JComboBox style = new JComboBox();
    private JComboBox status = new JComboBox();
    private JTextField keyword = new JTextField(10);
    private JTextArea text = new JTextArea(10, 30);

    private JButton okButton,applyButton,cancelButton;

    private JButton answers = new JButton("Answers");

    protected void addItems() {
        buildDropDowns();

        addItem("Keyword", keyword);
        addItem("Type", type);
        addItem("Style", style);
        addItem("Status", status);
        text.setLineWrap(true);
        addItem("Text", text);

        answers.setMnemonic('n');
        answers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getAnswers(question, new WrappedResponseWaiter(getFrameWaiter()) {
                    protected void _waitForResponse() {
                        disableButtons();
                    }

                    protected void _errorResponseReceived(Throwable t) {
                        enableButtons();
                    }

                    protected void _responseReceived() {
                        answerSelectionFrame.display(question);
                        enableButtons();
                    }
                });
            }
        });

        addItem(null, answers, true);
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

        applyButton = new JButton("Apply");
        applyButton.setMnemonic('a');
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commit();
            }
        });
        addButton(applyButton);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disposeMe = true;
                commit();
            }
        });
        addButton(okButton);

        frame.pack();
    }

    private void buildDropDowns() {
        type.removeAllItems();
        Collection types = controller.getQuestionTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            type.addItem(it.next());
        }

        status.removeAllItems();
        Collection statii = controller.getSurveyStatusTypes();
        for (Iterator it = statii.iterator(); it.hasNext();) {
            status.addItem(it.next());
        }

        style.removeAllItems();
        Collection styles = controller.getQuestionStyles();
        for (Iterator it = styles.iterator(); it.hasNext();) {
            style.addItem(it.next());
        }
    }


    public void display(RoundData round, QuestionData question, boolean committed) {
        buildDropDowns();
        this.committed = committed;
        this.round = round;
        this.question = question;
        answers.setEnabled(committed);
        keyword.setText(question.getKeyword());
        text.setText(question.getText());
        buildDropDowns();
        status.setSelectedItem(question.getStatus());
        style.setSelectedItem(question.getStyle());
        type.setSelectedItem(question.getType());

        keyword.requestFocusInWindow();
        super.display();
    }


    private void commit() {
        final QuestionData data = new QuestionData(
                question.getId(),
                keyword.getText(),
                text.getText(),
                (QuestionType) type.getSelectedItem(),
                (QuestionStyle) style.getSelectedItem(),
                (SurveyStatus) status.getSelectedItem()
        );
        ResponseWaiter waiter = new WrappedResponseWaiter(getFrameWaiter()) {
            protected void _errorResponseReceived(Throwable t) {
                enableButtons();
            }

            protected void _waitForResponse() {
                disableButtons();
            }

            protected void _responseReceived() {
                committed = true;
                enableButtons();
                question = data;
                if (disposeMe) {
                    frame.dispose();
                    disposeMe = false;
                }
            }
        };

        if (committed) {
            controller.modifyQuestion(data, waiter);
        } else {
            controller.addQuestion(round, data, waiter);
        }
    }

    private void enableButtons() {
        answers.setEnabled(committed);
        okButton.setEnabled(true);
        applyButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    private void disableButtons() {
        answers.setEnabled(false);
        cancelButton.setEnabled(false);
        applyButton.setEnabled(false);
        okButton.setEnabled(false);
    }
}

