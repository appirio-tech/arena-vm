package com.topcoder.client.contestMonitor.view.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;

/**
 * An input frame that should be used to edit round room assignment algorithm
 * details for specified round that is represented with RoundData object passed
 * to <code>display()</code> method.
 * 
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since Admin Tool 2.0
 */
public class RoundRoomAssignmentFrame extends InputFrame {

    /**
	 * A controller to be used to send requests to server.
	 */
    private ContestManagementController controller = null;

    /**
	 * A details of round room assignment algorithm. This frame displays the
	 * values of fields of this object for edit and then saves them back to
	 * this object with setters methods and saves the whole object to RoundData
	 * object.
	 */
    private RoundRoomAssignment roundRoomAssignment = null;

    /**
     * holds the list of room assignment algorithm names
     */
    private static HashMap seedList = new HashMap();

    /**
	 * An object representing a round that this frame was displayed to edit
	 * room assignment details for.
	 */
    private RoundData round = null;

    /**
	 * A flag to indicate we should close after commit
	 */
    private boolean disposeMe = false;

    /**
	 * A text field to edit the number of coders per room.
	 */
    private JTextField codersPerRoom = new JTextField(4);

    /**
	 * A combobox allowing to choose a type of algorithm to be used to assign
	 * coders to rooms.
	 */
    private JComboBox algorithm = new JComboBox();
    /**
	 * A checkbox to set the flag indicating that room assignment should be
	 * performed on "by division" basis.
	 */
    private JCheckBox isByDivision = new JCheckBox();

    /**
	 * A checkbox to set the flag indicating that room assignment should be
	 * performed on "by region" basis.
	 */
    private JCheckBox isByRegion = new JCheckBox();

    /**
	 * A checkbox to set the flag indicating that results of room assignment
	 * should be persisted in database.
	 */
    private JCheckBox isFinal = new JCheckBox();

    /**
	 * A text field to edit the p
	 */
    private JTextField pEdit = new JTextField(10);

    /**
	 * A button that should be pressed to confirm the changes and save them in
	 * database.
	 */
    private JButton okButton;

    /**
	 * A button that should be pressed to cancel changes
	 */
    private JButton cancelButton;

    /**
	 * Constructs new RoundRoomAssignmentFrame with specified <code>
	 * ContestManagementController</code>
	 * that should be used to send requests to server and parent JDialog frame.
	 * 
	 * @param controller
	 *            a ContestManagementController that should be used to send
	 *            requests to server
	 * @param parent
	 *            a parent JDialog frame
	 * @throws IllegalArgumentException
	 *             if any of given parameters is null
	 */
    public RoundRoomAssignmentFrame(
        ContestManagementController controller,
        JDialog parent) {
        super("Round Room Assignment", parent);
        this.controller = controller;
        build();
    }
    
    /**
	 * Add items allowing to edit the fields of RoundRoomAssignment object to
	 * this RoundRoomAssignmentFrame.
     * These strings should be moved to ContestConstants. Can be done at
     * integration time.
	 */
    protected void addItems() {
        addItem("Coders Per Room", codersPerRoom);
        seedList.put(new Integer(ContestConstants.RANDOM_SEEDING),"Random Seeding" );
        seedList.put(new Integer(ContestConstants.IRON_MAN_SEEDING), "Iron-Man");
        seedList.put(new Integer(ContestConstants.NCAA_STYLE), "NCAA Style");
        seedList.put(new Integer(ContestConstants.EMPTY_ROOM_SEEDING), "Empty Room");
        seedList.put(new Integer(ContestConstants.WEEKEST_LINK_SEEDING), "Weakest Link");
        seedList.put(new Integer(ContestConstants.ULTRA_RANDOM_SEEDING), "Ultra Random Seeding");
        seedList.put(new Integer(ContestConstants.TCO05_SEEDING), "TCO05 Seeding");
        seedList.put(new Integer(ContestConstants.DARTBOARD_SEEDING), "Dartboard Seeding");
        seedList.put(new Integer(ContestConstants.TCHS_SEEDING  ), "TCHS Seeding");
        seedList.put(new Integer(ContestConstants.ULTRA_RANDOM_DIV2_SEEDING), "Ultra Random Div 2 Seeding");
        DefaultComboBoxModel typeModel = new DefaultComboBoxModel();
        for(Iterator it = seedList.values().iterator(); it.hasNext();)
            typeModel.addElement(it.next());
        algorithm.setModel(typeModel);
        addItem("Type", algorithm);
        addItem("Is By Division", isByDivision);
        addItem("Is Final", isFinal);
        addItem("Is By Region", isByRegion);
        addItem("p", pEdit);
    }

    /**
	 * Add control buttons (namely, <code>okButton</code> and <code>
	 *       * cancelButton</code>)
	 * to this frame. The reaction on events from these buttons should be the
	 * same as in other frames, for example : SurveyFrame.
	 */
    protected void addButtons() {
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
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
	 * Displays this frame to user. Retrieves RoundRoomAssignment from
	 * specified RoundData object and initializes all edit fields with values
	 * taken from RoundRoomAssignment object.
	 * 
	 * @throws IllegalArgumentException
	 *             if given round is null
	 * @see RoundData#getRoomAssignment()
	 * @see RoundRoomAssignment
	 */
    public void display(RoundData round) {
        this.round = round;
        roundRoomAssignment = round.getRoomAssignment();
        codersPerRoom.setText("" + roundRoomAssignment.getCodersPerRoom());
        for(Iterator it = seedList.keySet().iterator(); it.hasNext();) {
            Integer itype = (Integer)it.next();
            if( itype.intValue() == roundRoomAssignment.getType()) {
                algorithm.getModel().setSelectedItem(seedList.get(itype));
                break;
            }
        }
        isByDivision.setSelected(roundRoomAssignment.isByDivision());
        isFinal.setSelected(roundRoomAssignment.isFinal());
        isByRegion.setSelected(roundRoomAssignment.isByRegion());
        pEdit.setText("" + roundRoomAssignment.getP());
        super.display();
    }

    /**
     * Displays an error message then sets the focus to the component
     * @param component that the focus should go to
     * @param msg the message that should be displayed
     */
    private boolean displayError( JComponent component, String msg ) {
        JOptionPane.
         showMessageDialog(frame,msg, "Validation Error",JOptionPane.OK_OPTION);
        component.requestFocusInWindow();
        return false;
    }
    /**
     * validates the entry fields for valid data. If bad data is found,
     * an error message is displayed and focus is placed into the control.
     */
    private boolean validate() {
        try { // check the coders per room field
            int cpr = Integer.parseInt(codersPerRoom.getText());
            if( cpr <= 0 ) {
                return displayError(codersPerRoom, 
                    "Coders per Room must be a number greater than 0");
            }
        } catch( NumberFormatException e ) {
            return displayError(codersPerRoom, 
                "Coders per Room must be a number greater than 0");
        }
        try { // check the value of 'p'
            double p = Double.parseDouble(pEdit.getText());
        } catch( NumberFormatException e ) {
            return displayError(pEdit, "P must be a valid number");
        }
        return true;    // data is all okay
    }
    
    /**
	 * Validates the input and then commits the changes made to round room 
     * assignment algorithm details to server and saves them to original 
     * RoundData object that was passed to display() method. 
     * Sends a request to server to save new details of round
	 * room assignment via contorller's saveRoundRoomAssignment() method.
	 * Provides an anonymous ResponseWaiter that should save new details of
	 * room assignment algorithm passed to display() method if notification on
	 * success is received. See SurveyFrame.commit() for example.
	 * 
	 * @see ContestManagementController#saveRoundRoomAssignment(RoundRoomAssignment)
	 */
    private void commit() {
        if( validate() == false ) return;
        
        int type = ContestConstants.RANDOM_SEEDING;
        String selectedItem = (String)algorithm.getModel().getSelectedItem();
        for(Iterator it = seedList.keySet().iterator(); it.hasNext();) {
            Integer key = (Integer)it.next();
            if( seedList.get(key).equals(selectedItem)) {
                type = key.intValue();
                break;
            }
        }
        /* 
         * must check for valid input here (int's & double's)
         */
        final RoundRoomAssignment data =
            new RoundRoomAssignment(
                round.getId(),
                Integer.parseInt(codersPerRoom.getText()),
                type,
                isByDivision.isSelected(),
                isFinal.isSelected(),
                isByRegion.isSelected(),
                Double.parseDouble(pEdit.getText()));
		   controller.saveRoundRoomAssignment(data, new
		      WrappedResponseWaiter(getFrameWaiter()) { 
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
                     round.setRoomAssignment(data); 
                     if (disposeMe) { 
                         frame.dispose(); 
                         disposeMe = false; 
                  }
              } 
           });
    }
}
