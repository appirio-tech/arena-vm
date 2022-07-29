/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.AnswerSelectionTableModel;
import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.QuestionData;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class AnswerSelectionFrame extends SelectionFrame {

    private AnswerFrame addModifyFrame;
    private ContestManagementController controller;
    private AnswerSelectionTableModel model;
    private QuestionData question;

    public AnswerSelectionFrame(ContestManagementController controller, JDialog parent) {
        super("Answer Selection", controller.getAnswerSelectionTableModel(), parent);
        this.model = controller.getAnswerSelectionTableModel();
        this.controller = controller;
        addModifyFrame = new AnswerFrame(controller, parent);
        build();
    }


    public void display(QuestionData question) {
        this.question = question;
        super.display();
    }

    protected int getPreferredTableHeight() {
        return 200;
    }

    protected int getPreferredTableWidth() {
        return 360;
    }

    protected void deleteEvent(int row) {
        AnswerData answer = model.getAnswer(row);
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete answer #" +
                answer.getId() + " - " + answer.getText() + "?",
                "Confirm Answer Deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteAnswer(answer, getWaiter());
        }
    }

    protected void modifyEvent(int row) {
        addModifyFrame.display(question, model.getAnswer(row), true);
    }

    protected void addEvent() {
        addModifyFrame.display(question, new AnswerData(), false);
    }

    protected void setColumnWidths() {
        setColumnWidth(0, 40);
        setColumnWidth(1, 60);
        setColumnWidth(2, 40);
        setColumnWidth(3, 200);
    }
}
