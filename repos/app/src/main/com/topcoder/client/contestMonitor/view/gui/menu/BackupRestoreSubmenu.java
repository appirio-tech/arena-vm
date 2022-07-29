package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.BaseResponseWaiter;
import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.response.GetBackupCopiesResponse;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class constructing a "Backup/restore tables" submenu that provides an
 * access to backup/restore tables functionality. Constructs a submenu
 * containing the menu items which allow to create backup copies of specified
 * tables or restore specified tables from specified copies.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class BackupRestoreSubmenu extends MonitorBaseMenu {

    /**
     * A "Backup/restore" submenu itself.
     */
    private JMenu menu = null;

    /**
     * A menu item to choose in order to create backup copies of specified
     * tables. When this menu item is chosen the GenericDialog allowing to
     * specify which tables should be backed up and send request to Admin
     * Listener server is shown.
     */
    private JMenuItem backupTablesItem = null;

    /**
     * A menu item to choose in order to restore specified tables from specified
     * backup copy. When this menu item is chosen the dialog frame allowing to
     * specify which tables should be restored and from what backup copy is shown.
     */
    private JMenuItem restoreTablesItem = null;

    /**
     * The monitor frame to which this submenu belongs
     */
    private MonitorFrame frame;

    /**
     * Constructs new submenu with specified parent frame, CommandSender that
     * should be used to send requests to Admin Listener server and
     * MonitorFrame.<p>
     *
     * @param parent
     * @param sender
     * @param frame
     */
    public BackupRestoreSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        backupTablesItem = getBackupItem();
        restoreTablesItem = getRestoreItem();

        menu = new JMenu("Backup/Restore");
        menu.add(backupTablesItem);
        menu.add(restoreTablesItem);
    }

    /**
     * Creates a JMenuItem providing access to backup tables functionality.
     * As response to selection of this item a dialog window allowing to
     * specify the tables that need to be backed up is shown. This dialog
     * window is shown only if some round is selected.
     *
     * @return a JMenuItem that should be chosen to backup specified tables.
     */
    private JMenuItem getBackupItem() {
        return getMenuItem("Create backup copy", KeyEvent.VK_B, new Runnable() {
            public void run() {
                getBackupTablesDialog().show();
            }
        });
    }

    /**
     * Creates a JMenuItem providing access to restart testers functionality.
     * This JMenuItem is created with <code>getConfirmedMenuItem()</code>
     * method so when this menu item is selected the dialog asking for
     * confirmation is shown and if confirmed a request to restart testers
     * is sent to Admin Listener server via CommandSender.
     *
     * @return a JMenuItem that should be chosen to restore specified tables
     *         from specified backup copy
     */
    private JMenuItem getRestoreItem() {
        return getMenuItem("Restore tables", KeyEvent.VK_R, new Runnable() {
            public void run() {
                getRestoreTablesDialog(frame.getRoundId()).show();
            }
        });
    }

    /**
     * Creates a GenericDialog that presents a list of checkboxes allowing to
     * specify which tables with names from <code>AdminConstants.TABLES_TO_BACKUP
     * </code> array should be backed up. By default all checkboxes are in
     * selected state.<p>
     * After confirmation if any of checkboxes is selected a request to backup
     * specified tables for specified round is sent to Admin Listener server via
     * ContestManagementController. An anonymous ResponseWaiter is provided to
     * ContestManagementController to be notified on response to request to
     * backup arrival. This ResponseWaiter will show a simple dialog window
     * notifying the user about successful or unsuccessful fulfilment of
     * request.
     *
     * @see AdminConstants#TABLES_TO_BACKUP
     * @see ContestManagementController#backupTables(int, Set, String, ResponseWaiter)
     */
    private GenericDialog getBackupTablesDialog() {
        Entry[] entries = new Entry[AdminConstants.TABLES_TO_BACKUP.length + 1];
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++) {
            entries[i] = new Entry(AdminConstants.TABLES_TO_BACKUP[i], new BooleanField(true));
        }
        entries[AdminConstants.TABLES_TO_BACKUP.length] = new Entry("Comment: ", new StringField());
        final String commandName = "Create backup copy";
        GenericDialog dialog = new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int roundId = frame.getRoundId();
                TreeSet tables = new TreeSet();
                StringBuffer tableList = new StringBuffer();
                for (int i = 0; i < paramList.size() - 1; i++) {
                    if (((Boolean) paramList.get(i)).booleanValue()) {
                        tables.add(AdminConstants.TABLES_TO_BACKUP[i]);
                        tableList.append(AdminConstants.TABLES_TO_BACKUP[i] + ", ");
                    }
                }
                if (tables.size() > 0) {
                    // strip the last commaspace from the string buffer
                    String comment = (String) paramList.get(paramList.size() - 1);
                    String cmdMessage = commandName
                            + ", round ID=" + roundId
                            + ", tables=" + tableList.substring(0, tableList.length() - 2)
                            + ", comment=" + comment;

                    if (isConfirmed(getCommandMessage(cmdMessage))) {
                        frame.getContestSelectionFrame().getContestManagementController().backupTables(roundId, tables, comment,
                                new BaseResponseWaiter() {
                                    public void waitForResponse() {
                                        backupTablesItem.setEnabled(false);
                                        restoreTablesItem.setEnabled(false);
                                    }

                                    public void errorResponseReceived(Throwable t) {
                                        frame.displayMessage("Error backing up tables: " + t.getMessage());
                                        backupTablesItem.setEnabled(true);
                                        restoreTablesItem.setEnabled(true);
                                    }

                                    public void responseReceived() {
                                        frame.displayMessage("Backup tables successful");
                                        backupTablesItem.setEnabled(true);
                                        restoreTablesItem.setEnabled(true);
                                    }
                                }
                        );
                    }
                } else {
                    frame.displayMessage("No tables were selected to backup");
                }
            }
        });
        return dialog;
    }

    /**
     * Creates a GenericDialog that presents a list of checkboxes allowing to
     * specify which tables with names from <code>AdminConstants.TABLES_TO_BACKUP
     * </code> array should be restored from specified backup copy. By default all checkboxes are in
     * selected state.
     *
     * After confirmation if any of checkboxes is selected a request to restore
     * specified tables for specified round from specified backup id is sent to
     * Admin Listener server via ContestManagementController. An anonymous ResponseWaiter is provided to
     * ContestManagementController to be notified on response to request to
     * restore tables arrival. This ResponseWaiter will show a simple dialog window
     * notifying the user about successful or unsuccessful fulfilment of
     * request.
     *
     * @see AdminConstants#TABLES_TO_BACKUP
     * @see ContestManagementController#getBackupCopies(int, ResponseWaiter)
     * @see ContestManagementController#getBackupCopiesResponse()
     * @see ContestManagementController#restoreTables(int, Set, ResponseWaiter)
     */
    private GenericDialog getRestoreTablesDialog(int round) {

        /**
         * A simple response waiter that keeps track of its state.
         * We only use to to wait for a GetBackupCopiesResponse.
         */
        class GetBackupCopiesResponseWaiter implements ResponseWaiter {
            /**
             * An int constant representing the state of response that is not
             * received yet.
             */
            public final static int NOT_RECEIVED = 0;

            /**
             * An int constant representing the state of response that is received
             * and is successful.
             */
            public final static int RECEIVED = 1;

            /**
             * An int constant representing the state of response that is received
             * and is unsuccessful.
             */
            public final static int ERROR = 2;

            /**
             * A state of response to request for problems data. This variable
             * is maintained by methods of <code>ResponseWaiter</code> interface.
             */
            private int responseState = NOT_RECEIVED;

            /**
             * This method is invoked when the request for problem data is sent.
             * Sets <code>responseRecieved</code> variable to NOT_RECEIVED.
             */
            public void waitForResponse() {
                responseState = NOT_RECEIVED;
            }

            /**
             * This method is invoked when response indicating about some error
             * with processing the request for problems data occurs. This method
             * sets the <code>responseRecieved</code> variable to ERROR.
             */
            public void errorResponseReceived(Throwable t) {
                responseState = ERROR;
                t.printStackTrace();
            }

            /**
             * This method is invoked when response to request for problems data
             * is received. This method sets the <code>responseRecieved</code>
             * variable to RECEIVED.
             */
            public void responseReceived() {
                responseState = RECEIVED;
            }

            /**
             * @return return the responseState variable
             */
            protected int getResponseState() {
                return responseState;
            }
        }

        final ContestManagementController controller = frame.getContestSelectionFrame().getContestManagementController();
        GetBackupCopiesResponseWaiter waiter = new GetBackupCopiesResponseWaiter();
        controller.getBackupCopies(round, waiter);
        while (waiter.getResponseState() == GetBackupCopiesResponseWaiter.NOT_RECEIVED) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (waiter.getResponseState() == GetBackupCopiesResponseWaiter.ERROR) {
            throw new IllegalStateException("Error response received while getting backup copies");
        }

        final String commandName = "Restore backup copy";
        GetBackupCopiesResponse response = controller.getBackupCopiesResponse();
        Entry[] entries;
        String description;
        if (response.getBackupCopies().size() == 0) {
            entries = new Entry[0];
            description = "No backup copies are available for selected round";
        } else {
            entries = new Entry[AdminConstants.TABLES_TO_BACKUP.length + 1];
            entries[0] = new Entry("Select backup copy to restore: ", new DropDownField(response.getBackupCopies().toArray()));
            // show a list of all available tables
            // only those that exist in selected backup will be restored
            for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++) {
                entries[i + 1] = new Entry(AdminConstants.TABLES_TO_BACKUP[i], new BooleanField(true));
            }
            description = ENTER_PARAMETERS;
        }

        GenericDialog dialog = new GenericDialog(getParent(), commandName, description, entries, new DialogExecutor() {
            public void execute(List paramList) {
                if (paramList.size() > 0) { // if there was a backup copy available for this round
                    if (paramList.get(0) != null) { // and if one was actually selected
                        // split the string representation of BackupCopy at dots "."
                        // take the first segment which is backupID and convert to int
                        String s[] = paramList.get(0).toString().split("\\.");
                        int backupID = Integer.valueOf(s[0]).intValue();

                        TreeSet tables = new TreeSet();
                        StringBuffer tableList = new StringBuffer();
                        for (int i = 1; i < paramList.size(); i++) {
                            if (((Boolean) paramList.get(i)).booleanValue()) {
                                tables.add(AdminConstants.TABLES_TO_BACKUP[i - 1]);
                                tableList.append(AdminConstants.TABLES_TO_BACKUP[i - 1] + ", ");
                            }
                        }

                        if (tables.size() > 0) {
                            // strip the last commaspace from the string buffer
                            String cmdMessage = commandName + " with id " + backupID + " for the following tables: "
                                    + tableList.substring(0, tableList.length() - 2);

                            if (isConfirmed(getCommandMessage(cmdMessage))) {
                                controller.restoreTables(backupID, tables, new BaseResponseWaiter() {
                                    public void waitForResponse() {
                                        backupTablesItem.setEnabled(false);
                                        restoreTablesItem.setEnabled(false);
                                    }

                                    public void errorResponseReceived(Throwable t) {
                                        frame.displayMessage("Error restoring tables: " + t.getMessage());
                                        backupTablesItem.setEnabled(true);
                                        restoreTablesItem.setEnabled(true);
                                    }

                                    public void responseReceived() {
                                        frame.displayMessage("Restore tables successful");
                                        backupTablesItem.setEnabled(true);
                                        restoreTablesItem.setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            frame.displayMessage("No tables were selected to restore");
                        }
                    } else {
                        frame.displayMessage("No backup copy was selected to restore");
                    }
                }
            }
        });
        return dialog;
    }

    /**
     * Applies the set of functions(permissions) ,allowed(granted) to requestor,
     * to menu items. Each menu item is enabled if specified Set contains
     * TCPermissions equal to corresponding AdminConstants.REQUEST_*
     * constant; otherwise such menu item is disabled.<p>
     *
     * @param  allowedFunctions a Set of TCPermissions representing the 
     *         functions(permissions) allowed(granted) to current user
     * @throws IllegalArgumentException if given Set is null
     */
    public void applySecurity(Set allowedFunctions) {
        menu.setEnabled(
                allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_BACKUP_TABLES))
                && allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_TABLES))
        );
        backupTablesItem.setEnabled(allowedFunctions.contains(
                AdminConstants.getPermission(AdminConstants.REQUEST_BACKUP_TABLES)));
        restoreTablesItem.setEnabled(allowedFunctions.contains(
                AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_TABLES)));
    }

    /**
     * Gets the submenu containing menu items providing access to restart
     * compilers/testers functionality.
     *
     * @return a JMenu containing JMenuItems providing access to restart
     *         services functionality
     */
    public JMenu getBackupRestoreSubmenu() {
        return menu;
    }
}
