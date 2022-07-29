package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.SecurityManagementFrame;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Added a menu item displaying newly defined SecurityManagementFrame.
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class ActionsMenu extends MonitorBaseMenu {

    private BroadcastSubmenu broadcastSubmenu;

    private final GenericDialog banIPDialog, bootUserDialog;
    private final GenericDialog clearPracticeRoomsDialog;
    private final GenericDialog setUserStatusDialog, moderatedChatDialog;
    private JMenu menu;
    private JMenuItem refreshBroadcastsItem, banIPItem, bootUserItem;
    private JMenuItem setUserStatusItem, contestManagementItem, moderatedChatItem, clearPracticeRoomsItem, messagesItem;
    //private JMenuItem loggingItem;
    private MonitorFrame frame;
    /**
     * A security man. frame for editting security schema
     *
     * @since Admin Tool 2.0
     */
    private SecurityManagementFrame securityManagementFrame = null;
    /**
     * A menu item providing access to SecurityManagementFrame
     *
     * @since Admin Tool 2.0
     */
    private JMenuItem securityItem = null;

    private static final Logger log = Logger.getLogger(ActionsMenu.class);
    
    /**
     * Thie method was updated for AdminTool 2.0 by creating a new
     * SecurityManagementFrame
     * 
     * @param parent - the parent frame
     * @param sender - a command sender
     * @param frame - the monitor frame
     */
    public ActionsMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        broadcastSubmenu = new BroadcastSubmenu(parent, sender, frame);

        banIPDialog = getBanIPDialog();
        setUserStatusDialog = getSetUserStatusDialog();
        bootUserDialog = getBootUserDialog();
        moderatedChatDialog = getModeratedChatDialog();
        clearPracticeRoomsDialog = getClearPracticeRoomsDialog();
        
        securityManagementFrame = new SecurityManagementFrame(frame.getUserId(), 
            frame.getContestSelectionFrame().getContestManagementController(),parent);

        refreshBroadcastsItem = getRefreshBroadcastsItem();
        banIPItem = getBanIPItem();
        setUserStatusItem = getSetUserStatusItem();
        bootUserItem = getBootUserItem();
        contestManagementItem = getContestManagementItem();
        messagesItem = getImportantMessagesItem();
        moderatedChatItem = getModeratedChatItem();
        securityItem = getSecurityItem();
        clearPracticeRoomsItem = getClearPracticeRoomsItem();

        menu = new JMenu("Actions");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.add(broadcastSubmenu.getBroadcastSubmenu());
        menu.add(refreshBroadcastsItem);
        menu.addSeparator();
        menu.add(clearPracticeRoomsItem);
        menu.add(messagesItem);
        menu.addSeparator();
        menu.add(banIPItem);
        menu.add(setUserStatusItem);
        menu.add(bootUserItem);
        menu.addSeparator();
        menu.add(contestManagementItem);
        menu.add(moderatedChatItem);
        menu.addSeparator();
        menu.add(securityItem);
    }

    /**
     *
     * This method is modified to take into consideration that given Set 
     * contains a TCPermission objects. This method must use new 
     * AdminConstants.getPermission(int) method to check if given Set 
     * contains a TCPermission corresponding to request ID in order to 
     * enable/disable a corresponding menu item.
     * @param allowedFunctions a Set of TCPermissions representing the permissions granted to user.
     */
    public void applySecurity(Set allowedFunctions) {
        broadcastSubmenu.applySecurity(allowedFunctions);
        refreshBroadcastsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_BROADCASTS)));
        banIPItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_BAN_IP)));
        setUserStatusItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SET_USER_STATUS)));
        bootUserItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_BOOT_USER)));
        contestManagementItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CONTEST_MANAGEMENT)));
        moderatedChatItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_MODERATED_CHAT)));
        securityItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT)));
        clearPracticeRoomsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_PRACTICE_ROOMS)));
        messagesItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_IMPORTANT_MESSAGES)));
    }
    
    private GenericDialog getBanIPDialog() {
        Entry[] entries = {
            new Entry("IP Address:", new StringField()),
        };
        final String commandName = "BanIP";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String ipAddress = (String) paramList.get(0);
                if (isConfirmed(getCommandMessage(commandName + ", ipAddress=" + ipAddress))) {
                    getSender().sendBanIP(ipAddress);
                }
            }
        });
    }

    private GenericDialog getClearPracticeRoomsDialog() {
        Entry[] entries = {
            new Entry("Clear Item:", new DropDownField(new Object[] {
                new MenuItem(ContestConstants.CLEAR_PRACTICE_NOT_OPEN, "Coders who have not opened any problems"),
                new MenuItem(ContestConstants.CLEAR_PRACTICE_NOT_SUBMIT, "Coders who have not submitted any problems"),
                new MenuItem(ContestConstants.CLEAR_PRACTICE_NOT_SUBMIT_RECENT, "Coders who have not submitted any problems in 6 monthes"),
                new MenuItem(ContestConstants.CLEAR_PRACTICE_ALL, "All coders"),
            }, 0)),
        };
        final String commandName = "ClearPracticeRooms";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    MenuItem type = (MenuItem) paramList.get(0);
                    int clearType = ContestConstants.CLEAR_PRACTICE_NOT_OPEN;
                    if (type != null) {
                        clearType = type.getType();
                    }
                    if (isConfirmed(getCommandMessage(commandName + ", type=" + clearType))) {
                        getSender().sendClearPracticeRooms(clearType);
                    }
                }
            });
    }

    private GenericDialog getSetUserStatusDialog() {
        Entry[] entries = {
            new Entry("Handle:", new StringField()),
            new Entry("IsActiveStatus (checked-'A', unchecked-'I'):", new BooleanField(false)),
        };
        final String commandName = "SetUserStatus";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String handle = (String) paramList.get(0);
                boolean isActiveStatus = ((Boolean) paramList.get(1)).booleanValue();
                if (isConfirmed(getCommandMessage(commandName + ", handle=" + handle +
                        ", isActiveStatus=" + isActiveStatus))) {
                    getSender().sendSetUserStatus(handle, isActiveStatus);
                }
            }
        });
    }
    
    private GenericDialog getBootUserDialog() {
        Entry[] entries = {
            new Entry("Handle:", new StringField()),
        };
        final String commandName = "BootUser";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String handle = (String) paramList.get(0);
                if (isConfirmed(getCommandMessage(commandName + ", handle=" + handle))) {
                    getSender().sendBootUser(handle);
                }
             }
        });
    }

    private GenericDialog getModeratedChatDialog() {
        Entry[] entries = {
            new Entry("Room ID:", new IntegerField(0)),
        };
        final String commandName = "Open Moderator Panel";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List params) {
                int roomID = ((Integer) params.get(0)).intValue();
                frame.openModeratorTab(roomID);
            }
        });
    }

    // Menu items
    private JMenuItem getRefreshBroadcastsItem() {
        return getMenuItem("Refresh Broadcasts", KeyEvent.VK_C, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Refresh Broadcasts, roundID=" + roundID))) {
                        getSender().sendRefreshBroadcasts(roundID);
                    }
                }
            }
        });
    }
    
    private JMenuItem getClearPracticeRoomsItem() {
        return getMenuItem("Clear Practice Rooms...", KeyEvent.VK_C, new Runnable() {
            public void run() {
                /*
                if (isConfirmed(getCommandMessage("Clear Practice Rooms"))) {
                }
                */
                clearPracticeRoomsDialog.show();
            }
        });
    }

    private JMenuItem getBanIPItem() {
        return getMenuItem("Ban IP...", KeyEvent.VK_N, new Runnable() {
            public void run() {
                banIPDialog.show();
            }
        });
    }

    private JMenuItem getSetUserStatusItem() {
        return getMenuItem("Set User Status...", KeyEvent.VK_S, new Runnable() {
            public void run() {
                setUserStatusDialog.show();
            }
        });
    }
    
    private JMenuItem getBootUserItem() {
        return getMenuItem("Boot User...", KeyEvent.VK_T, new Runnable() {
            public void run() {
                bootUserDialog.show();
            }
        });
    }
    
    private JMenuItem getImportantMessagesItem() {
        return getMenuItem("Important Messages...", KeyEvent.VK_M, new Runnable() {
            public void run() {
                frame.getImportantMessageSelectionFrame().display(frame.getWaiter());
            }
        });
    }

    private JMenuItem getContestManagementItem() {
        return getMenuItem("Contest Management...", KeyEvent.VK_M, new Runnable() {
            public void run() {
                frame.getContestSelectionFrame().display(frame.getWaiter());
            }
        });
    }

    private JMenuItem getModeratedChatItem() {
        return getMenuItem("Open Moderator Tab...", KeyEvent.VK_O, new Runnable() {
            public void run() {
                moderatedChatDialog.show();
            }
        });
    }
   
    /**
     * added for AdminTool 2.0 to display the SecurityManagementFrame
     * @return a menu item
     */
    private JMenuItem getSecurityItem() {
        return getMenuItem("Security Management...", KeyEvent.VK_E, new Runnable() {
            public void run() {
                securityManagementFrame.display();
            }
        });
    }
    public JMenu getActionsMenu() {
        return menu;
    }
    
    private class MenuItem {

        private int type;
        private String label;

        public MenuItem(int type, String label) {
            this.type = type;
            this.label = label;
        }

        public int getType() {
            return type;
        }

        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            return type == ((MenuItem) obj).type;
        }

        public String toString() {
            return label;
        }
    }
}

