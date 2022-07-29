package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
public final class BroadcastSubmenu extends MonitorBaseMenu {

    private final GenericDialog globalBroadcastDialog, componentBroadcastDialog, roundBroadcastDialog;
    private JMenu menu;
    private JMenuItem globalBroadcastItem, componentBroadcastItem, roundBroadcastItem;
    private MonitorFrame frame;

    public BroadcastSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        globalBroadcastDialog = getGlobalBroadcastDialog();
        componentBroadcastDialog = getComponentBroadcastDialog();
        roundBroadcastDialog = getRoundBroadcastDialog();

        globalBroadcastItem = getGlobalBroadcastItem();
        componentBroadcastItem = getComponentBroadcastItem();
        roundBroadcastItem = getRoundBroadcastItem();

        menu = new JMenu("Send broadcast");
        menu.setMnemonic(KeyEvent.VK_B);
        menu.add(globalBroadcastItem);
        menu.add(componentBroadcastItem);
        menu.add(roundBroadcastItem);
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
        globalBroadcastItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_GLOBAL_BROADCAST)));
        componentBroadcastItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_COMPONENT_BROADCAST)));
        roundBroadcastItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SEND_ROUND_BROADCAST)));
    }


    // Dialog boxes
    private GenericDialog getGlobalBroadcastDialog() {
        Entry[] entries = {
            new Entry("Message:", new StringField(50)),
        };
        final String commandName = "Send global broadcast";
        GenericDialog dialog = new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String message = (String) paramList.get(0);
                int roundId = frame.getRoundId();
                String cmdMessage = commandName + ", message=" + message;
                if (isConfirmed(getCommandMessage(cmdMessage))) {
                    getSender().sendGlobalBroadcast(message);
                }
            }
        });
        return dialog;
    }

    private GenericDialog getComponentBroadcastDialog() {
        Entry[] entries = {
            new Entry("Component:", new ProblemListField(frame)),
            new Entry("Message:", new StringField(50)),
        };
        final String commandName = "Send component broadcast";
        GenericDialog dialog = new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int componentId = ((Integer) paramList.get(0)).intValue();
                String message = (String) paramList.get(1);
                int roundId = frame.getRoundId();
                String cmdMessage = commandName + ", round ID=" + roundId + ", component ID=" + componentId + ", " +
                        "message=" + message;
                if (isConfirmed(getCommandMessage(cmdMessage))) {
                    getSender().sendComponentBroadcast(roundId, message, componentId);
                }
            }
        });
        return dialog;
    }

    private GenericDialog getRoundBroadcastDialog() {
        Entry[] entries = {
            new Entry("Message:", new StringField(50)),
        };
        final String commandName = "Send round broadcast";
        GenericDialog dialog = new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String message = (String) paramList.get(0);
                int roundId = frame.getRoundId();
                String cmdMessage = commandName + ", round ID=" + roundId + ", message=" + message;
                if (isConfirmed(getCommandMessage(cmdMessage))) {
                    getSender().sendRoundBroadcast(roundId, message);
                }
            }
        });
        return dialog;
    }

    // Menu items
    private JMenuItem getGlobalBroadcastItem() {
        return getMenuItem("Global...", KeyEvent.VK_G, new Runnable() {
            public void run() {
                globalBroadcastDialog.show();
            }
        });
    }

    private JMenuItem getComponentBroadcastItem() {
        return getMenuItem("Problem...", KeyEvent.VK_P, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    componentBroadcastDialog.show();
                }
            }
        });
    }

    private JMenuItem getRoundBroadcastItem() {
        return getMenuItem("Round...", KeyEvent.VK_R, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    roundBroadcastDialog.show();
                }
            }
        });
    }


    public JMenu getBroadcastSubmenu() {
        return menu;
    }
}

