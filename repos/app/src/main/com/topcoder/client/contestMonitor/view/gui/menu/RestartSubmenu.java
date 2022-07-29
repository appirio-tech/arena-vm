package com.topcoder.client.contestMonitor.view.gui.menu;

import java.awt.event.KeyEvent;
import javax.swing.*;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.ContestSelectionFrame;
import java.awt.Frame;
import java.util.List;
import java.util.Set;

/**
 * A class constructing a "Restart service" submenu that provides an access
 * to restart compilers/testers functionality. Constructs a submenu containing
 * the menu items which allow to restart compilers, testers separately or all
 * of them.
 *
 * @author  TCSDESIGNER
 * @author  TCSDEVELOPER
 * @version 1.0 07/31/2003
 * @since Admin Tool 2.0
 */
public class RestartSubmenu extends MonitorBaseMenu {

    /**
     * A "Restart services" submenu itself.
     */
    private JMenu menu = null;

    /**
     * A menu item to choose in order to restart the compilers. When this 
     * menu item is chosen the request to restart the compilers is sent to
     * Admin Listener server.
     */
    private JMenuItem restartCompilerItem = null;

    /**
     * A menu item to choose in order to restart the testers. When this 
     * menu item is chosen the request to restart the testers is sent to
     * Admin Listener server.
     */
    private JMenuItem restartTesterItem = null;
    
    private GenericDialog restartDialog = null;
    
    /**
     * A menu item to choose in order to restart the testers and compilers.
     * When this menu item is chosen the request to restart both the 
     * compilers and testers is sent to Admin Listener server.
     */
    private JMenuItem restartAllItem = null;

    /** saves our frame, so that we can get the controller later on */
    private MonitorFrame frame;

    /** Waiter to use for our requests */
    private ResponseWaiter compilerWaiter, testerWaiter, allWaiter;

    /**
     * Constructs new submenu with specified parent frame, CommandSender that 
     * should be used to send requests to Admin Listener server and 
     * MonitorFrame.<p>
     *
     * @param parent
     * @param sender
     * @param frame
     */
    public RestartSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;
        setupWaiter();
        menu = new JMenu("Restart Service");
        this.restartAllItem = this.getRestartAllItem();
        this.restartCompilerItem = this.getRestartCompilerItem();
        this.restartTesterItem = this.getRestartTesterItem();
        this.restartDialog = this.getRestartTesterDialog();
        menu.add(restartAllItem);
        menu.add(restartCompilerItem);
        menu.add(restartTesterItem);
    }

    /**
     * Creates a JMenuItem providing access to restart compilers functionality.
     * This JMenuItem is created with <code>getConfirmedMenuItem()</code>
     * method so when this menu item is selected the dialog asking for
     * confirmation is shown and if confirmed a request to restart compilers
     * is sent to Admin Listener server via CommandSender.
     *
     * @return a JMenuItem that should be chosen to restart the compilers
     */
    private JMenuItem getRestartCompilerItem() {
        return getConfirmedMenuItem("Restart compiler", KeyEvent.VK_C, "Restart compiler?",
                new Runnable() {
                    public void run() {
                        ContestSelectionFrame contest = frame.getContestSelectionFrame();
                        ContestManagementController controller = contest.getContestManagementController();
                        controller.restartService(AdminConstants.REQUEST_RESTART_COMPILERS, AdminConstants.RESTART_TESTERS_IMMEDIATELY, compilerWaiter);
                    }
                }
        );
    }

    /**
     * Creates a JMenuItem providing access to restart testers functionality.
     * This JMenuItem is created with <code>getConfirmedMenuItem()</code>
     * method so when this menu item is selected the dialog asking for
     * confirmation is shown and if confirmed a request to restart testers
     * is sent to Admin Listener server via CommandSender.
     *
     * @return a JMenuItem that should be chosen to restart the testers
     */
    
    private JMenuItem getRestartTesterItem() {
        return getMenuItem("Restart Testers...", KeyEvent.VK_T, new Runnable() {
            public void run() {
                restartDialog.show();
            }
        });
    }
    
    private GenericDialog getRestartTesterDialog() {
        Entry[] entries = {
            new Entry("Restart Mode:", new RestartModeField(-1))
        };
        final String commandName = "Restart Testers";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                Integer restartMode = (Integer) paramList.get(0);
                if (isConfirmed(getCommandMessage(commandName + ", restartMode=" + restartMode))) {
                    ContestSelectionFrame contest = frame.getContestSelectionFrame();
                    ContestManagementController controller = contest.getContestManagementController();
                    controller.restartService(AdminConstants.REQUEST_RESTART_TESTERS, restartMode.intValue(), testerWaiter);
                }
            }
        });
    }


    /**
     * Creates a JMenuItem providing access to restart both compilers and 
     * testers functionality.<p>
     * This JMenuItem is created with <code>getConfirmedMenuItem()</code>
     * method so when this menu item is selected the dialog asking for
     * confirmation is shown and if confirmed a request to restart testers
     * and compilers is sent to Admin Listener server via CommandSender.
     *
     * @return a JMenuItem that should be chosen to restart both the compilers
     *         and testers
     */
    private JMenuItem getRestartAllItem() {
        return super.getConfirmedMenuItem("Restart all", KeyEvent.VK_A, "Restart compiler and tester?",
                new Runnable() {
                    public void run() {
                        ContestSelectionFrame contest = frame.getContestSelectionFrame();
                        ContestManagementController controller = contest.getContestManagementController();
                        controller.restartService(AdminConstants.REQUEST_RESTART_ALL, AdminConstants.RESTART_TESTERS_IMMEDIATELY, allWaiter);
                    }
                }
        );
    }

    /**
     * Applies the set of functions(permissions) ,allowed(granted) to requestor,
     * to menu items. Each menu item is enabled if specified Set contains
     * Integer equal to corresponding AdminConstants.REQUEST_RESTART_*
     * constant; otherwise such menu item is disabled.<p>
     * For example, <code>"Restart compilers..."</code> item will be enabled 
     * if given Set contains Integer with value equal to <code>
     * AdminConstants.REQUEST_RESTART_COMPILERS</code>.
     *
     * @param  allowedFunctions a Set of Integers representing the constants
     *         from ADMIN_CONSTANTS class specifying the functions(permissions)
     *         allowed(granted) to current user
     * @throws IllegalArgumentException if given Set is null
     */
    public void applySecurity(Set allowedFunctions) {
        if (allowedFunctions == null) {
            throw new IllegalArgumentException();
        }
        restartAllItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_ALL)));
        restartCompilerItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_COMPILERS)));
        restartTesterItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_TESTERS)));
    }

    /**
     * Gets the submenu containing menu items providing access to restart 
     * compilers/testers functionality.
     *
     * @return a JMenu containing JMenuItems providing access to restart
     *         services functionality
     */
    public JMenu getRestartSubmenu() {
        return menu;
    }

    /**
     * Sets up the waiters for use when we send our restart requests
     */
    private void setupWaiter() {
        compilerWaiter = new WrappedResponseWaiter(frame.getWaiter()) {
            public void _responseReceived() {
                frame.displayMessage("Restarted compiler");
            }
            public void _errorResponseReceived(Throwable t) {
                frame.displayMessage("Error restarting");
            }
        };

        testerWaiter = new WrappedResponseWaiter(frame.getWaiter()) {
            public void _responseReceived() {
                frame.displayMessage("Restarted tester");
            }
            public void _errorResponseReceived(Throwable t) {
                frame.displayMessage("Error restarting tester");
            }
        };

        allWaiter = new WrappedResponseWaiter(frame.getWaiter()) {
            public void _responseReceived() {
                frame.displayMessage("Restarted compiler and tester");
            }
            public void _errorResponseReceived(Throwable t) {
                frame.displayMessage("Error restarting compiler and tester");
            }
        };
    }
}