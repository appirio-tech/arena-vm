package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorGUIUtils;

import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract class MonitorBaseMenu {

    static final String ENTER_PARAMETERS = "Enter parameters.";

    private final Frame parent;
    private final CommandSender sender;

    MonitorBaseMenu(Frame parent, CommandSender sender) {
        this.parent = parent;
        this.sender = sender;
    }

    Frame getParent() {
        return parent;
    }

    CommandSender getSender() {
        return sender;
    }

    JMenuItem getMenuItem(String commandName, int mnemonic, final Runnable runnable) {
        JMenuItem menuItem = new JMenuItem(commandName, mnemonic);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
        return menuItem;
    }

    JMenuItem getConfirmedMenuItem(String commandName, int mnemonic, final String confirmMessage,
            final Runnable runnable) {
        return getMenuItem(commandName, mnemonic, new Runnable() {
            public void run() {
                if (isConfirmed(confirmMessage)) {
                    runnable.run();
                }
            }
        });
    }

    // Helper functions
    String getCommandMessage(String commandName) {
        return "Do you want to run this command: " + commandName + " ?";
    }

    JMenuItem getCommandMenuItem(String commandName, int mnemonic, Runnable runnable) {
        return getConfirmedMenuItem(commandName, mnemonic, getCommandMessage(commandName), runnable);
    }

    static boolean isConfirmed(String message) {
        return MonitorGUIUtils.isConfirmed(message);
    }

}
