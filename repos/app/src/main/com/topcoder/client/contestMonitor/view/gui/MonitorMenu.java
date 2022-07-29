package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.menu.ActionsMenu;
import com.topcoder.client.contestMonitor.view.gui.menu.CachedObjectsMenu;
import com.topcoder.client.contestMonitor.view.gui.menu.ContestServerMenu;
import com.topcoder.client.contestMonitor.view.gui.menu.FileMenu;
import com.topcoder.client.contestMonitor.view.gui.menu.HelpMenu;
import com.topcoder.client.contestMonitor.view.gui.menu.RunContestMenu;

import javax.swing.JMenuBar;
import java.awt.Frame;
import java.util.Set;

//import com.topcoder.utilities.monitor.view.gui.menu.AdminCommandsMenu;
//import com.topcoder.utilities.monitor.view.gui.menu.ModeratorMenu;

final class MonitorMenu {

    //private final AdminCommandsMenu adminCommandsMenu;
    private final FileMenu fileMenu;
    private final RunContestMenu runContestMenu;
    private final ContestServerMenu contestServerMenu;
    private final CachedObjectsMenu cachedObjectsMenu;
    private final ActionsMenu actionsMenu;
    private final HelpMenu helpMenu;
    /* Da Twink Daddy - 05/14/2002 - Removed Hack; New Member */
    /**
     * The "Moderated Chat" menu controller.
     */
    // private     ModeratorMenu   modMenu;
    private JMenuBar menuBar;

    MonitorMenu(MonitorFrame frame, CommandSender sender) {
        final Frame parent = frame.getJFrame();

        // Menu items
        fileMenu = new FileMenu(parent, sender, frame);
        runContestMenu = new RunContestMenu(parent, sender, frame);
        contestServerMenu = new ContestServerMenu(parent, sender, frame);
        //adminCommandsMenu=new AdminCommandsMenu(parent,sender,frame);
        cachedObjectsMenu = new CachedObjectsMenu(parent, sender, frame);
        actionsMenu = new ActionsMenu(parent, sender, frame);
        /* Da Twink Daddy - 05/14/2002 - Removed hack; Initialize modMenu */
        // modMenu = new ModeratorMenu(parent,sender,frame);
        helpMenu = new HelpMenu(parent, sender, frame);

        // dpecora - Moved code below from getMenuBar().
        menuBar = new JMenuBar();
        menuBar.add(fileMenu.getFileMenu());
        menuBar.add(runContestMenu.getRunContestMenu());
        menuBar.add(contestServerMenu.getContestServerMenu());
        //menuBar.add(adminCommandsMenu.getAdminCommandsMenu());
        menuBar.add(cachedObjectsMenu.getCachedObjectMenu());
        menuBar.add(actionsMenu.getActionsMenu());
        /* Da Twink Daddy - 05/13/2002 - Hack removed; Replaced with corrected code. */
        //menuBar.add(modMenu.getModeratorMenu());
        menuBar.add(helpMenu.getHelpMenu());
    }

    JMenuBar getMenuBar() {
        return menuBar;
    }

    public void applySecurity(Set allowedFunctions) {
        fileMenu.applySecurity(allowedFunctions);
        runContestMenu.applySecurity(allowedFunctions);
        contestServerMenu.applySecurity(allowedFunctions);
        //adminCommandsMenu.applySecurity(allowedFunctions);
        cachedObjectsMenu.applySecurity(allowedFunctions);
        actionsMenu.applySecurity(allowedFunctions);
        //modMenu.applySecurity(allowedFunctions);
        helpMenu.applySecurity(allowedFunctions);
    }
}
