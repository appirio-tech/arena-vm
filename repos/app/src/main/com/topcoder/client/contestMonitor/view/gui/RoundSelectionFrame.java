/*
* User: Mike Cervantes (emcee)
* Date: May 16, 2002
* Time: 10:57:26 PM
*/
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.RoundSelectionTableModel;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.shared.util.DBMS;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>addEvent() method is modified to ask ContestManagementController to
 * send a request for ID for newly created round to Admin Listener server
 * to meet the "1.2.1 Using Sequences when creating rounds" requirement.
 * <p>New inner private class NewRoundIDWaiter implementing ResponseWaiter
 * interface defined. See Documentation for this class.
 * <p>New public method <code>getContestManagementController()</code> added.
 * This method will be used by BroadcastSubmenu's inner class to send a
 * request for problem data for currently selected round. ("1.2.5 Admin
 * Problem - Broadcast" requirement).
 * 
 * @author TCDEVELOPER
 */
public class RoundSelectionFrame extends SelectionFrame {

    private RoundFrame addModifyFrame;
    private ContestManagementController controller;
    private RoundSelectionTableModel model;
    private ContestData contest;

    public RoundSelectionFrame(ContestManagementController controller, JDialog parent) {
        super("Round Selection", controller.getRoundSelectionTableModel(), parent);
        this.model = (RoundSelectionTableModel) super.tableModel;
        this.controller = controller;
        addModifyFrame = new RoundFrame(getContestManagementController(), frame);
        build();
    }

    /**
     * Gets the <code>ContestManagementController</code> assigned to this
     * <code>RoundSelectionFrame</code>. This controller may be used to
     * send requests to Admin Listener.
     *
     * @since Admin Tool 2.0
     */
    public ContestManagementController getContestManagementController() {
        return controller;
    }

    public void display(ContestData contest) {
        this.contest = contest;
        display();
    }

    protected int getPreferredTableHeight() {
        return 200;
    }

    protected int getPreferredTableWidth() {
        return 360;
    }

    protected void deleteEvent(int row) {
        RoundData round = model.getRound(row);
        int confirm = -1;
        if (!round.getStatus().equals("F")) {
            confirm = JOptionPane.showConfirmDialog(frame,
                    "This round's status field suggests it may have already taken place.\nAttempts to delete it will probably fail, but may produce undesired results.\n" +
                    "Are you sure you want to delete round #" +
                    round.getId() + " - " + round.getName() + "?",
                    "Confirm Round Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete round #" +
                    round.getId() + " - " + round.getName() + "?",
                    "Confirm Round Deletion",
                    JOptionPane.YES_NO_OPTION
            );
        }

        if (confirm == JOptionPane.YES_OPTION) {
            getContestManagementController().deleteRound(round, getWaiter());
        }
    }

    protected void modifyEvent(int row) {
        addModifyFrame.display(model.getRound(row), true);
    }

    /**
     * asks the controller to send a request for a new contest ID. Creates
     * a new contest id waiter to accept the reply. 
     *
     * @see    com.topcoder.shared.util.DBMS
     * @see    ContestSelectionFrame/NewRoundIDWaiter
     */
    protected void addEvent() {
        getContestManagementController().
                getNewID(DBMS.ROUND_SEQ, new NewRoundIDWaiter());
    }

    protected void setColumnWidths() {
        setColumnWidth(0, 60);
        setColumnWidth(1, 240);
        setColumnWidth(2, 50);
    }
    /**
     * This class is used to receive a notification about the availability
     * of new round ID after request for such new contest ID was sent by this
     * ContestSelectionFrame via ContestManagementController and 
     * CommandSender. Once the response for request is received this class
     * obtains the ID for newly created contest from GetNewIDResponse
     * object obtained with <code>
     * ContestManagementController.getNewIDResponse()</code> method and 
     * displays RoundFrame with RoundData created with specified ID.
     * 
     * Notes: Instead of just implementing a ResponseWaiter as in the design,
     * this class is a subclass of WrappedResponseWaiter(). This allows us
     * to re-use the FrameWaiter functionality for disabling the frame and
     * handling exceptions.
     * In order to be able to disable the buttons themselves, changes would 
     * be required to the base class (SelectionFrame) to add an 
     * enableButtons(boolean flag) method. This wasn't in the design. 
     * 
     * @author TCSDESIGNER
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.AdminListener.response.GetNewIDResponse
     * @see    RoundData
     */
    private class NewRoundIDWaiter extends WrappedResponseWaiter {
        NewRoundIDWaiter() {
            super(getWaiter());
        }

        /**
         * Enables all control elements of this <code>RoundSelectionFrame
         * </code> and retrieves the response to request for new round ID
         * with <code>ContestManagementController.getNewIDResponse()
         * </code> method. Retrieves ID for newly created round, constructs
         * new <code>RoundData</code> with specified ID and displays
         * <code>RoundFrame</code> object with newly created <code>
         * RoundData</code> object.
         *
         * @see ContestManagementController#getNewIDResponse()
         * @see RoundFrame#display
         * @see ContestData
         */
        public void _responseReceived() {
            int id = getContestManagementController().
                    getNewIDResponse().getNewId();
            addModifyFrame.display(new RoundData(contest,id), false);
        }
    }

}
