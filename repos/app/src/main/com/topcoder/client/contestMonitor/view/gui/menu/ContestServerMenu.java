package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.ConnectionItem;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.MonitorGUIUtils;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class ContestServerMenu extends MonitorBaseMenu {

    private final GenericDialog disconnectClientDialog,specAppShowRoomDialog,specAppStartRotationDialog,forwardingAddressDialog,adminForwardingAddressDialog, roundForwardingDialog;
    private JMenu menu;
    private JMenuItem disconnectItem, loadContestItem, shutdownItem, garbageCollectionItem;
    private JMenuItem //replayListenerItem,
        //replayReceiverItem,
        specAppStartRotateItem,
        specAppStopRotateItem,
        specAppShowRoomItem,
        restartEventTopicListenerItem,
        forwardingAddressItem,
        adminForwardingAddressItem,
        roundForwardingItem,
        showSpecResultsItem;

//    private MonitorFrame frame;

    public ContestServerMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
//        this.frame = frame;

        disconnectClientDialog = getDisconnectClientDialog();
        specAppStartRotationDialog = getSpecAppStartRotateDialog();
        specAppShowRoomDialog = getSpecAppShowRoomDialog();
        forwardingAddressDialog = getForwardingAddressDialog();
        adminForwardingAddressDialog = getAdminForwardingAddressDialog();
        roundForwardingDialog = getRoundForwardingDialog();

        disconnectItem = getDisconnectItem();
        shutdownItem = getShutdownItem();
        showSpecResultsItem = getShowSpecResultsItem();
        garbageCollectionItem = getGarbageCollectionItem();
        //replayListenerItem = getReplayListenerItem();
        //replayReceiverItem = getReplayReceiverItem();
        specAppStartRotateItem = getSpecAppStartRotateItem();
        specAppStopRotateItem = getSpecAppStopRotateItem();
        specAppShowRoomItem = getSpecAppShowRoomItem();
        restartEventTopicListenerItem = getRestartEventTopicListenerItem();
        adminForwardingAddressItem = getAdminForwardingAddressItem();
        roundForwardingItem = getRoundForwardingItem();
        forwardingAddressItem = getForwardingAddressItem();
        menu = new JMenu("Contest server");
        menu.setMnemonic(KeyEvent.VK_C);
        menu.add(disconnectItem);
        menu.add(shutdownItem);
        menu.add(garbageCollectionItem);
        //menu.add(replayListenerItem);
       // menu.add(replayReceiverItem);
        menu.add(forwardingAddressItem);
        menu.add(adminForwardingAddressItem);
        menu.add(roundForwardingItem);
        menu.add(showSpecResultsItem);
        menu.addSeparator();
        menu.add(specAppStartRotateItem);
        menu.add(specAppStopRotateItem);
        menu.add(specAppShowRoomItem);
        menu.add(restartEventTopicListenerItem);
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
        disconnectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_DISCONNECT_CLIENT)));
        shutdownItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SHUTDOWN)));
        garbageCollectionItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_GARBAGE_COLLECTION)));
        //replayListenerItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_LISTENER)));
        //replayReceiverItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_RECEIVER)));
        specAppStartRotateItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_START_ROTATE)));
        specAppStopRotateItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_STOP_ROTATE)));
        specAppShowRoomItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_SHOW_ROOM)));
        restartEventTopicListenerItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_EVENT_TOPIC_LISTENER)));
        forwardingAddressItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_FORWARDING)));
        adminForwardingAddressItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ADMIN_FORWARDING)));
    }

    // Dialog boxes
    private GenericDialog getDisconnectClientDialog() {
        Entry[] disconnectEntries = {
            new Entry("Server ID:", new IntegerField()),
            new Entry("Connection ID:", new IntegerField()),
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Disconnect Applet Client",
                "What applet client do you want to disconnect?", disconnectEntries, new DialogExecutor() {
                    public void execute(List paramList) {
                        int serverID = ((Integer) paramList.get(0)).intValue();
                        int connID = ((Integer) paramList.get(1)).intValue();
                        ConnectionItem connectionItem = getSender().getConnection(serverID, connID);
                        if (connectionItem == null) {
                            JOptionPane.showMessageDialog(parent, "Wrong parameters", "Disconnect Client Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        MonitorGUIUtils.disconnectAppletClient(connectionItem, getSender());
                    }
                });
    }

    private GenericDialog getSpecAppStartRotateDialog() {
        Entry[] entries = {
            new Entry("Delay Seconds:", new IntegerField(10))
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Start Spec App Rotation",
                "", entries, new DialogExecutor() {
                    public void execute(List paramList) {
                        int delay = ((Integer) paramList.get(0)).intValue();
                        getSender().sendStartSpecAppRotation(delay);
                    }
                });
    }

    private GenericDialog getSpecAppShowRoomDialog() {
        Entry[] entries = {
            new Entry("Room ID:", new IntegerField())
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Set Spec App Room",
                "", entries, new DialogExecutor() {
                    public void execute(List paramList) {
                        int roomID = ((Integer) paramList.get(0)).intValue();
                        getSender().sendSpecAppShowRoom(roomID);
                    }
                });
    }

    private GenericDialog getForwardingAddressDialog() {
        Entry[] entries = {
            new Entry("<host>:<port>", new StringField())
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Set Forwarding Address",
                "", entries, new DialogExecutor() {
                    public void execute(List paramList) {
                        getSender().sendSetForwardingAdressRequest(paramList.get(0).toString());
                    }
                });
    }

    private GenericDialog getAdminForwardingAddressDialog() {
        Entry[] entries = {
            new Entry("<host>:<port>", new StringField())
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Set Admin Forwarding Address",
                "", entries, new DialogExecutor() {
                    public void execute(List paramList) {
                        getSender().sendSetAdminForwardingAdressRequest(paramList.get(0).toString());
                    }
                });
    }
    
    private GenericDialog getRoundForwardingDialog() {
        Entry[] entries = {
            new Entry("Host", new StringField()),
            new Entry("Port", new IntegerField()),
            new Entry("Enabled", new BooleanField(true))
        };
        final Frame parent = getParent();
        return new GenericDialog(parent, "Set Round Forwarding Address",
                "", entries, new DialogExecutor() {
                    public void execute(List paramList) {
                        getSender().sendRoundForwarder(paramList.get(0).toString(), 
                                ((Integer) paramList.get(1)).intValue(), 
                                ((Boolean) paramList.get(2)).booleanValue());
                    }
                });
    }

    // Menu items
    private JMenuItem getDisconnectItem() {
        return getMenuItem("Disconnect applet client...", KeyEvent.VK_D, new Runnable() {
            public void run() {
                disconnectClientDialog.show();
            }
        });
    }

    private JMenuItem getShutdownItem() {
        return getConfirmedMenuItem("Shutdown all listeners", KeyEvent.VK_S, "Shutdown All Listeners?",
                new Runnable() {
                    public void run() {
                        getSender().shutdownAllListeners();
                    }
                }
        );
    }
    
    private JMenuItem getShowSpecResultsItem() {
        return getConfirmedMenuItem("Show Forwarded System Test Results", KeyEvent.VK_R, "Display System Test Results?",
                new Runnable() {
                    public void run() {
                        getSender().sendShowSpecResults();
                    }
                }
        );
    }

    private JMenuItem getGarbageCollectionItem() {
        return getConfirmedMenuItem("Start garbage collection", KeyEvent.VK_G, "Start garbage collection?", new Runnable() {
            public void run() {
                getSender().sendGarbageCollection();
            }
        });
    }

    private JMenuItem getRestartEventTopicListenerItem() {
        return getConfirmedMenuItem("Restart Event Topic Listener", KeyEvent.VK_G, "Restart Event Topic Listener?", new Runnable() {
            public void run() {
                getSender().sendRestartEventTopicListener();
            }
        });
    }

    //private JMenuItem getReplayListenerItem() {
    //    return getConfirmedMenuItem("Start replay listener", KeyEvent.VK_T, "Start replay listener?", new Runnable() {
    //        public void run() {
    //            getSender().sendReplayListener();
    //        }
    //    });
    //}

    private JMenuItem getForwardingAddressItem() {
        return getMenuItem("Set forwarding address", KeyEvent.VK_F, new Runnable() {
            public void run() {
                forwardingAddressDialog.show();
            }
        });
    }

    private JMenuItem getAdminForwardingAddressItem() {
        return getMenuItem("Set admin forwarding address", KeyEvent.VK_W, new Runnable() {
            public void run() {
                adminForwardingAddressDialog.show();
            }
        });
    }
    
    private JMenuItem getRoundForwardingItem() {
        return getMenuItem("Set round forwarding address", KeyEvent.VK_R, new Runnable() {
            public void run() {
                roundForwardingDialog.show();
            }
        });
    }

    //private JMenuItem getReplayReceiverItem() {
    //    return getConfirmedMenuItem("Start replay receiver", KeyEvent.VK_V, "Start replay receiver?", new Runnable() {
    //        public void run() {
    //            getSender().sendReplayReceiver();
    //        }
    //    });
    //}

    private JMenuItem getSpecAppStopRotateItem() {
        return getConfirmedMenuItem("Stop spec app rotation", KeyEvent.VK_P, "Stop spec app rotation?", new Runnable() {
            public void run() {
                getSender().sendStopSpecAppRotation();
            }
        });
    }

    private JMenuItem getSpecAppStartRotateItem() {
        return getMenuItem("Start spec app rotation", KeyEvent.VK_R, new Runnable() {
            public void run() {
                specAppStartRotationDialog.show();
            }
        });
    }

    private JMenuItem getSpecAppShowRoomItem() {
        return getMenuItem("Set spec app room", KeyEvent.VK_M, new Runnable() {
            public void run() {
                specAppShowRoomDialog.show();
            }
        });
    }

    public JMenu getContestServerMenu() {
        return menu;
    }
}

