/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.Season;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>The behavior of id instance variable changed. See Documentation for id variable.
 * <p>The behavior of display(ContestData, boolean) method changed. See Documentation for this method.
 * 
 * @author  TCDEVELOPER
 */
public class ContestFrame extends InputFrame {

    private ContestManagementController controller;
    private boolean isNewContest = false;
    private ContestData contest;
    private ContestData newContest;
    private RoundSelectionFrame roundSelectionFrame;
    private JButton cancelButton;
    private JButton applyButton;
    private JButton okButton;

    private boolean disposeMe = false;

    private WrappedResponseWaiter roundsWaiter = new WrappedResponseWaiter(getFrameWaiter()) {
        protected void _waitForResponse() {
            disableButtons();
        }

        protected void _responseReceived() {
            roundSelectionFrame.display(contest);
            enableButtons();
        }

        protected void _errorResponseReceived(Throwable t) {
            enableButtons();
        }
    };

    private ResponseWaiter addModifyWaiter = new WrappedResponseWaiter(getFrameWaiter()) {
        public void _waitForResponse() {
            disableButtons();
        }

        public void _responseReceived() {
            contest = newContest;
            id.setEditable(false);
            isNewContest = false;
            enableButtons();

            if (disposeMe)
                frame.dispose();
            disposeMe = false;
        }

        public void _errorResponseReceived(Throwable t) {
            enableButtons();
            if (isNewContest) {
                rounds.setEnabled(false);
            } else {
                display(contest, false);
            }
        }
    };

    public ContestFrame(ContestManagementController controller, JDialog parent) {
        super("Contest", parent);
        this.controller = controller;
        roundSelectionFrame = new RoundSelectionFrame(controller, parent);
        build();
    }


    /**
     * Starting from Admin Tool 2.0 this JTextField is never editable. 
     * The value for this field is always taken from ContestData.getId() method.
     */
    private JTextField id = new JTextField(8);
    private JTextField name = new JTextField(20);
    private DateField startDate = new DateField(frame);
    private DateField endDate = new DateField(frame);
    private JTextField status = new JTextField(3);
    private JTextField group = new JTextField(5);
    private JTextField adText = new JTextField(20);
    private DateField adStartDate = new DateField(frame);
    private DateField adEndDate = new DateField(frame);
    private JTextField adTask = new JTextField(20);
    private JTextField adCommand = new JTextField(20);
    private JCheckBox activateMenu = new JCheckBox();
    private JComboBox season = new JComboBox();
    private JButton rounds = new JButton("Rounds");

    protected void addItems() {
        buildSeasonDropDown();
        
        addItem("ID", id);
        addItem("Name", name);
        addItem("Start Date", startDate);
        addItem("End Date", endDate);
        addItem("Group ID", group);
        addItem("Ad Text", adText);
        addItem("Ad Start Date", adStartDate);
        addItem("Ad End Date", adEndDate);
        addItem("Ad Task", adTask);
        addItem("Ad Command", adCommand);
        addItem("Activate Menu", activateMenu);
        addItem("Status", status);
        addItem("Season", season);

        rounds.setMnemonic('R');
        rounds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getRounds(contest, roundsWaiter);
            }
        });
        addItem(null, rounds, true);
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
                disposeMe = false;
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
    }   
    
    private void buildSeasonDropDown() {
        season.removeAllItems();
        Collection seasons = controller.getSeasons();
        season.addItem(new Season(null,""));
        for (Iterator it = seasons.iterator(); it.hasNext();) {
            season.addItem(it.next());
        }
        frame.repaint();
    }    
    /**
     * This method was modified for AdminTool 2.0 in order to make id 
     * text field always uneditable.
     * @param contest - contest to display
     * @param isNewContest - are we creating a new contest
     */
    public void display(ContestData contest, boolean isNewContest) {        
        this.isNewContest = isNewContest;
        id.setEditable(false);
        rounds.setEnabled(!isNewContest);
        this.contest = contest;
        id.setText("" + contest.getId());
        name.setText(contest.getName());
        startDate.setDate(contest.getStartDate());
        endDate.setDate(contest.getEndDate());
        group.setText("" + contest.getGroupId());
        adText.setText(contest.getAdText());
        adStartDate.setDate(contest.getAdStartDate());
        adEndDate.setDate(contest.getAdEndDate());
        adTask.setText(contest.getAdTask());
        adCommand.setText(contest.getAdCommand());
        activateMenu.setSelected(contest.isActivateMenu());
        status.setText(contest.getStatus());
        
        buildSeasonDropDown();        
        season.setSelectedItem(contest.getSeason());
        
        name.requestFocusInWindow();
        this.contest = contest;
        frame.pack();
        display();
    }

    private void enableButtons() {
        rounds.setEnabled(true);
        cancelButton.setEnabled(true);
        applyButton.setEnabled(true);
        okButton.setEnabled(true);
    }

    private void disableButtons() {
        rounds.setEnabled(false);
        cancelButton.setEnabled(false);
        applyButton.setEnabled(false);
        okButton.setEnabled(false);
    }

    private void commit() {
        newContest = new ContestData(
                Integer.parseInt(id.getText()),
                name.getText(),
                startDate.getDate(),
                endDate.getDate(),
                Integer.parseInt(group.getText()),
                adText.getText(),
                adStartDate.getDate(),
                adEndDate.getDate(),
                adTask.getText(),
                adCommand.getText(),
                status.getText(),
                activateMenu.isSelected(),
                (Season) season.getSelectedItem()
        );

        if (!isNewContest) {
            controller.modifyContest(contest, newContest, addModifyWaiter);
        } else {
            controller.addContest(newContest, addModifyWaiter);
        }
    }
}

