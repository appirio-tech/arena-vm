/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.QuestionSelectionTableModel;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundData;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class QuestionSelectionFrame extends SelectionFrame {

    private QuestionFrame addModifyFrame;
    private ContestManagementController controller;
    private QuestionSelectionTableModel model;
    private RoundData round;

    public QuestionSelectionFrame(ContestManagementController controller, JDialog parent) {
        super("Question Selection", controller.getQuestionSelectionTableModel(), parent);
        this.controller = controller;
        this.model = controller.getQuestionSelectionTableModel();
        addModifyFrame = new QuestionFrame(controller, frame);
        build();
    }


    public void display(RoundData round) {
        this.round = round;
        display();
    }

    protected int getPreferredTableHeight() {
        return 200;
    }

    protected int getPreferredTableWidth() {
        return 320;
    }

    protected void deleteEvent(int row) {
        QuestionData question = model.getQuestion(row);
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete question #" +
                question.getId() + " - " + question.getKeyword() + "?",
                "Confirm Question Deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteQuestion(question, getWaiter());
        }
    }

    protected void modifyEvent(int row) {
        addModifyFrame.display(round, model.getQuestion(row), true);
    }

    protected void addEvent() {
        addModifyFrame.display(round, new QuestionData(), false);
    }

    protected void setColumnWidths() {
        setColumnWidth(0, 40);
        setColumnWidth(1, 100);
        setColumnWidth(2, 60);
        setColumnWidth(3, 200);
    }
}
