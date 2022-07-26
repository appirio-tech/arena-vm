package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Set;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class RunContestMenu extends MonitorBaseMenu {

    private JMenu menu;
    private BeforeContestSubmenu beforeContestSubmenu;
    private DuringContestSubmenu duringContestSubmenu;
    private AfterContestSubmenu afterContestSubmenu;
    private RestartSubmenu restartSubmenu;
    private JMenuItem disableRoundItem, enableRoundItem, refreshRoundItem, restoreRoundItem;
    private MonitorFrame frame;

    public RunContestMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        beforeContestSubmenu = new BeforeContestSubmenu(parent, sender, frame);
        duringContestSubmenu = new DuringContestSubmenu(parent, sender, frame);
        afterContestSubmenu = new AfterContestSubmenu(parent, sender, frame);
        restartSubmenu = new RestartSubmenu(parent, sender, frame);

        disableRoundItem = getDisableRoundItem();
        enableRoundItem = getEnableRoundItem();
        refreshRoundItem = getRefreshRoundItem();
        restoreRoundItem = getRestoreRoundItem();

        menu = new JMenu("Run contest");
        menu.setMnemonic(KeyEvent.VK_R);

        menu.add(beforeContestSubmenu.getBeforeContestSubmenu());
        menu.add(duringContestSubmenu.getDuringContestSubmenu());
        menu.add(afterContestSubmenu.getAfterContestSubmenu());
        menu.add(restartSubmenu.getRestartSubmenu());
        menu.addSeparator();
        menu.add(disableRoundItem);
        menu.add(enableRoundItem);
        menu.add(refreshRoundItem);
        menu.add(restoreRoundItem);
    }

    /**
     * This method is modified to take into consideration that given Set 
     * contains a TCPermission objects. This method must use new 
     * AdminConstants.getPermission(int) method to check if given Set 
     * contains a TCPermission corresponding to request ID in order to 
     * enable/disable a corresponding menu item.
     * @param permissions a Set of TCPermissions representing the permissions granted to user.
     */
    public void applySecurity(Set allowedFunctions) {
        beforeContestSubmenu.applySecurity(allowedFunctions);
        duringContestSubmenu.applySecurity(allowedFunctions);
        afterContestSubmenu.applySecurity(allowedFunctions);
        restartSubmenu.applySecurity(allowedFunctions);
        disableRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_DISABLE_ROUND)));
        enableRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ENABLE_ROUND)));
        refreshRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROUND)));
        restoreRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_ROUND)));
    }

    // Menu items
    private JMenuItem getDisableRoundItem() {
        return getMenuItem("Disable Round", KeyEvent.VK_S, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Disable Round, roundID=" + roundID))) {
                        getSender().sendDisableContestRound(roundID);
                    }
                }
            }
        });
    }

    private JMenuItem getEnableRoundItem() {
        return getMenuItem("Enable Round", KeyEvent.VK_E, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Enable Round, roundID=" + roundID))) {
                        getSender().sendEnableContestRound(roundID);
                    }
                }
            }
        });
    }

    private JMenuItem getRefreshRoundItem() {
        return getMenuItem("Refresh Round", KeyEvent.VK_R, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Refresh Round, roundID=" + roundID))) {
                        getSender().sendRefreshContestRound(roundID);
                    }
                }
            }
        });
    }

    private JMenuItem getRestoreRoundItem() {
        return getMenuItem("Restore Round", KeyEvent.VK_T, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Restore Round, roundID=" + roundID))) {
                        getSender().sendRestoreRound(roundID);
                    }
                }
            }
        });
    }

    public JMenu getRunContestMenu() {
        return menu;
    }
}
