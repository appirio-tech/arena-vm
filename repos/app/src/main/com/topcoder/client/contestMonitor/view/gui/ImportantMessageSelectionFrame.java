package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ImportantMessageSelectionTableModel;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.shared.util.DBMS;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ImportantMessageSelectionFrame extends SelectionFrame {

    private ImportantMessageFrame addModifyFrame;
    ContestManagementController controller;
    private ImportantMessageSelectionTableModel model;

    public ImportantMessageSelectionFrame(ContestManagementController controller, MonitorFrame parent) {
        this(controller, parent.getJFrame());
        mFrame = parent;
    }

    private MonitorFrame mFrame;

    public ImportantMessageSelectionFrame(ContestManagementController controller, JFrame parent) {
        super("Important Message Selection", controller.getImportantMessageSelectionTableModel(), parent);
        this.controller = controller;
        model = getContestManagementController().getImportantMessageSelectionTableModel();
        addModifyFrame = new ImportantMessageFrame(controller, frame, 0);
        build();
    }
    
    public void setUserID(long u) {
        addModifyFrame = new ImportantMessageFrame(controller, frame, u);
    }

    /**
     * Gets the <code>ContestManagementController</code> assigned to this
     * <code>ContestSelectionFrame</code>. This controller may be used to
     * send requests to Admin Listener.
     *
     * @since Admin Tool 2.0
     */
    public ContestManagementController getContestManagementController() {
        return controller;
    }
    
    private WrappedResponseWaiter waiterClient = new WrappedResponseWaiter() {
        protected void _responseReceived() {
            addModifyFrame = new ImportantMessageFrame(controller, frame, mFrame.getUserId());
            display();
        }
    };

    public void display(FrameWaiter parentWaiter) {
        synchronized (parentWaiter) {
            waiterClient.setChild(parentWaiter);
            getContestManagementController().getAllImportantMessages(waiterClient);
        }
    }

    protected int getPreferredTableHeight() {
        return 300;
    }

    protected int getPreferredTableWidth() {
        return 680;
    }

    protected void deleteEvent(int row) {
        //not supported
    }


    protected void modifyEvent(int row) {
        addModifyFrame.display(model.getMessage(row), false);
    }

    /**
     * asks the controller to send a request for a new contest ID. Creates
     * a new contest id waiter to accept the reply. 
     *
     * @see    DBMS
     * @see    ContestSelectionFrame.NewContestIDWaiter
     */
    protected void addEvent() {
        getContestManagementController().
            getNewID(DBMS.MESSAGE_SEQ, new NewContestIDWaiter());
    }

    protected void setColumnWidths() {
        setColumnWidth(0, 60);
        setColumnWidth(1, 240);
        setColumnWidth(2, 165);
        setColumnWidth(3, 165);
        //setColumnWidth(4, 50);
    }

    /**
     * This class is used to receive a notification about the availability
     * of new contest ID after request for such new contest ID was sent by this
     * ContestSelectionFrame via ContestManagementController and 
     * CommandSender. Once the response for request is received this class
     * obtains the ID for newly created contest from GetNewIDResponse
     * object obtained with <code>
     * ContestManagementController.getNewIDResponse()</code> method and 
     * displays ContestFrame with ContestData created with specified ID.
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
     * @see    ContestData
     */
    private class NewContestIDWaiter extends WrappedResponseWaiter {
        NewContestIDWaiter() {
            super(getWaiter());
        }

        /**
         * Enables all control elements of this <code>ContestSelectionFrame
         * </code> and retrieves the response to request for new contest ID
         * with <code>ContestManagementController.getNewIDResponse()
         * </code> method. Retrieves ID for newly created contest, constructs
         * new <code>ContestData</code> with specified ID and displays
         * <code>ContestFrame</code> object with newly created <code>
         * ContestData</code> object.
         *
         * @see ContestManagementController#getNewIDResponse()
         * @see ContestFrame#display(ContestData, boolean)
         * @see ContestData
         */
        public void _responseReceived() {
            int id = getContestManagementController().
                getNewIDResponse().getNewId();
            addModifyFrame.display(new ImportantMessageData(id), true);
        }
    }

}
