package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.ConnectionItem;

import javax.swing.JOptionPane;

public final class MonitorGUIUtils {

    private static final String CONFIRM_DIALOG_TITLE = "Confirm";

    private MonitorGUIUtils() {
    }

    public static boolean isConfirmed(String message) {
        int option = JOptionPane.showConfirmDialog(null, message, CONFIRM_DIALOG_TITLE, JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    public static void disconnectAppletClient(ConnectionItem item, CommandSender sender) {
        if (isConfirmed("Disconnect this user: " + item + "?")) {
            sender.disconnectAppletClient(item.getServerId().intValue(), item.getConnId().intValue());
        }
    }

}
