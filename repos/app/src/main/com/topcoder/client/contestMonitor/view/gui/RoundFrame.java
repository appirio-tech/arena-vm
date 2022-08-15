/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.Region;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundType;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>The behavior of id instance variable changed. See Documentation for id variable.
 * <p>The behavior of <code>display(RoundData, boolean)</code> method
 * changed. See Documentation for this method.
 * <p>New JButton added to provide the access to room assignment algorithm
 * for edited(added) contest round.
 * <p>Method <code>addItems()</code> is modified to add newly defined button
 * providing access to room assignment details.
 * <p><code>enableButtons()</code> and <code>disableButtons()</code> methods
 * are modified to take into consideration the newly defined button.
 * <p>Method <code>commit()</code> is modified to initialize temporary newRound
 * variable with RoundRoomAssignment object with default values.
 *
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>add {@link com.topcoder.client.contestMonitor.view.gui.RoundEventFrame } to add the event data manipulation.</li>
 * </ol>
 * </p>
 * @author  TCDEVELOPER
 */
public class RoundFrame extends InputFrame {

    private ContestManagementController controller;
    private boolean committed = false;
    private RoundData round;
    private RoundSegmentFrame roundSegmentFrame;
    private RoundProblemFrame roundProblemFrame;
    private SurveyFrame surveyFrame;
    private QuestionSelectionFrame questionSelectionFrame;
    private RoundLanguageFrame roundLanguageFrame;
    private RoundEventFrame roundEventFrame;
    private VerifyRoundFrame verifyRoundFrame;

    private boolean disposeMe = false;
    
    /**
     * A frame allowing editting of round room assignment data
     *
     * @since Admin Tool 2.0
     */
    private RoundRoomAssignmentFrame roundRoomAssignmentFrame;

    public RoundFrame(ContestManagementController controller, JDialog parent) {
        super("Round", parent);
        this.controller = controller;
        this.roundSegmentFrame = new RoundSegmentFrame(controller, frame);
        this.roundProblemFrame = new RoundProblemFrame(controller, frame);
        this.surveyFrame = new SurveyFrame(controller, frame);        
        this.questionSelectionFrame = 
           new QuestionSelectionFrame(controller, frame);
        this.verifyRoundFrame = new VerifyRoundFrame();
        this.roundRoomAssignmentFrame = 
           new RoundRoomAssignmentFrame(controller, frame);
        this.roundLanguageFrame = new RoundLanguageFrame(controller, frame);
        this.roundEventFrame = new RoundEventFrame(controller,frame);
        build();
    }

    /**
     * Starting from Admin Tool 2.0 this JTextField is never editable. The 
     * value for this field is always taken from RoundData.getId() method.
     */
    private JTextField id = new JTextField(8);
    private JTextField name = new JTextField(20);
    private JTextField short_name = new JTextField(20);
    private JTextField status = new JTextField(3);
    private JComboBox roundType = new JComboBox();
    private JComboBox invite = new JComboBox();
    private JTextField limit = new JTextField(6);
    private JComboBox region = new JComboBox();

    private JButton segments = new JButton("Segments");
    private JButton problems = new JButton("Problems");
    private JButton survey = new JButton("Survey");
    private JButton questions = new JButton("Questions");
    private JButton languages = new JButton("Languages");
    private JButton event = new JButton("Event");
    private JButton verify = new JButton("Verify");

    private JButton okButton, cancelButton, applyButton;
    
    /**
     * A button providing access to modify the details of room assignment
     * algorithm for round edited by this RoundFrame.
     *
     * @since Admin Tool 2.0
     */
    private JButton roomAssignment = new JButton("Room assignment algorithm");


    /**
     * This method is modified to add newly defined JButton to this
     * RoundFrame. As a reaction to events from this button a
     * RoundRoomAssignmentFrame with room assignment details should be
     * displayed.
     * @see RoundRoomAssignmentFrame
     * @see RoundData#getRoomAssignment()
     */
    protected void addItems() {
        buildRoundTypeDropDown();
        buildRegionDropDown();
        buildInvitationDropDown();

        addItem("ID", id);
        addItem("Name", name);
        addItem("Short Name", short_name);
        addItem("Type", roundType);
        addItem("Registration Limit", limit);
        addItem("Invitational", invite);
        addItem("Status", status);
        addItem("Region", region);

        segments.setMnemonic('G');
        segments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                roundSegmentFrame.display(round);
            }
        });

        problems.setMnemonic('P');
        problems.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getProblems(round, new WrappedResponseWaiter(getFrameWaiter()) {
                    protected void _waitForResponse() {
                        disableButtons();
                    }

                    protected void _errorResponseReceived(Throwable t) {
                        enableButtons();
                    }

                    protected void _responseReceived() {
                        enableButtons();
                        controller.getAvailableProblemsTableModel().filterType((RoundType) roundType.getSelectedItem());
                        roundProblemFrame.display(round);
                    }
                });
            }
        });

        survey.setMnemonic('S');
        survey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                surveyFrame.display(round);
            }
        });
        questions.setMnemonic('Q');
        questions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getQuestions(round, new WrappedResponseWaiter(getFrameWaiter()) {
                    protected void _waitForResponse() {
                        disableButtons();
                    }

                    protected void _errorResponseReceived(Throwable t) {
                        enableButtons();
                    }

                    protected void _responseReceived() {
                        enableButtons();
                        questionSelectionFrame.display(round);
                    }
                });
            }
        });

        languages.setMnemonic('G');
        languages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //roundSegmentFrame.display(round);
                roundLanguageFrame.display(round);
            }
        });

        event.setMnemonic('E');
        event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                roundEventFrame.display(round);
            }
        });
        
        verify.setMnemonic('V');
        verify.addActionListener(new ActionListener() {
            StringBuffer buffer = new StringBuffer();

            public void actionPerformed(ActionEvent e) {
                controller.verifyRound(round, buffer, new WrappedResponseWaiter(getFrameWaiter()) {
                    protected void _waitForResponse() {
                        disableButtons();
                    }

                    protected void _errorResponseReceived(Throwable t) {
                        enableButtons();
                    }

                    protected void _responseReceived() {
                        enableButtons();
                        verifyRoundFrame.display(buffer.toString());
                    }
                });
            }
        });
        roomAssignment.setMnemonic('R');
        roomAssignment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                roundRoomAssignmentFrame.display(round);
            }
        });


        addItem(null, segments, true);
        addItem(null, problems, true);
        addItem(null, survey, true);
        addItem(null, questions, true);
        addItem(null, roomAssignment, true);
        addItem(null, languages, true);
        addItem(null, event, true);
        addItem(null, verify, true);
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

    private void buildInvitationDropDown() {
        invite.removeAllItems();
        invite.addItem("No");
        invite.addItem("Normal");
        invite.addItem("Negate");
        frame.repaint();
    }

    private void buildRoundTypeDropDown() {
        roundType.removeAllItems();
        Collection types = controller.getRoundTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            roundType.addItem(it.next());
        }
        frame.repaint();
    }

    private void buildRegionDropDown() {
        region.removeAllItems();
        Collection regions = controller.getRegions();
        region.addItem(new Region(null,""));
        for (Iterator it = regions.iterator(); it.hasNext();) {
            region.addItem(it.next());
        }
        frame.repaint();
    }
    
    /**
     * This method was modified in order to make id text field always 
     * uneditable. Also newly defined button should be enabled if commited is true.
     * 
     * @param round - the roundwe are editting
     * @param committed - has round been added already
     */
    public void display(RoundData round, boolean committed) {
        this.committed = committed;
        id.setEditable(false);
        segments.setEnabled(committed);
        problems.setEnabled(committed);
        survey.setEnabled(committed);
        questions.setEnabled(committed);
        languages.setEnabled(committed);
        event.setEnabled(committed);
        verify.setEnabled(committed);
        roomAssignment.setEnabled(committed);
        this.round = round;
        id.setText("" + round.getId());
        name.setText(round.getName());
        short_name.setText(round.getShortName());
        status.setText(round.getStatus());
        limit.setText("" + round.getRegistrationLimit());

        buildRoundTypeDropDown();
        buildRegionDropDown();
        buildInvitationDropDown();
        roundType.setSelectedItem(round.getType());
        region.setSelectedItem(round.getRegion());
        invite.setSelectedIndex(round.getInvitationType());

        name.requestFocusInWindow();
        frame.pack();
        super.display();
    }


    /**
     * This method was modified to initialize newRound variable with 
     * RoundRoomAssignment object obtained from existing "round" variable.
     */
    private void commit() {
        final RoundData newRound = new RoundData(
                round.getContest(),
                Integer.parseInt(id.getText()),
                name.getText(),
                (RoundType) roundType.getSelectedItem(),
                status.getText(),
                Integer.parseInt(limit.getText()),
                invite.getSelectedIndex(),
                short_name.getText(),
                (Region) region.getSelectedItem()
        );
        newRound.setRoomAssignment(round.getRoomAssignment());
        // Set other data, such as survey, segments, etc.
        newRound.setSurvey(round.getSurvey());
        newRound.setSegments(round.getSegments());
        newRound.setLanguages(round.getLanguages());
        newRound.setEvent(round.getEvent());
        
        ResponseWaiter waiter = new WrappedResponseWaiter(getFrameWaiter()) {
            protected void _waitForResponse() {
                disableButtons();
            }

            protected void _errorResponseReceived(Throwable t) {
                enableButtons();
            }

            protected void _responseReceived() {
                round = newRound;
                committed = true;
                id.setEditable(false);
                enableButtons();
                if (disposeMe) {
                    frame.dispose();
                    disposeMe = false;
                }
            }
        };
        if (committed) {
            controller.modifyRound(round, newRound, waiter);
        } else {
            controller.addRound(newRound, waiter);
        }
    }

    /**
     * This method was modified in order to enable newly added
     * "Room assignment" button.
     */
    private void enableButtons() {
        segments.setEnabled(committed);
        problems.setEnabled(committed);
        survey.setEnabled(committed);
        questions.setEnabled(committed);
        languages.setEnabled(committed);
        event.setEnabled(committed);
        verify.setEnabled(committed);
        roomAssignment.setEnabled(committed);        
        okButton.setEnabled(true);
        applyButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * This method was modified in order to enable newly added
     * "Room assignment" button.
     */
    private void disableButtons() {
        segments.setEnabled(false);
        problems.setEnabled(false);
        questions.setEnabled(false);
        survey.setEnabled(false);
        languages.setEnabled(false);
        event.setEnabled(false);
        verify.setEnabled(false);
        roomAssignment.setEnabled(false);        
        cancelButton.setEnabled(false);
        applyButton.setEnabled(false);
        okButton.setEnabled(false);
    }
}

