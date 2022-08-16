package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.MonitorGUIConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Set;

public final class HelpMenu extends MonitorBaseMenu {

    private static final String PROGRAM_NAME = MonitorGUIConstants.PROGRAM_NAME;
    private JMenu menu;
    private JMenuItem aboutItem;
    private MonitorFrame frame;

    public HelpMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;
        aboutItem = getAboutItem();
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.add(aboutItem);
    }

    public void applySecurity(Set allowedFunctions) {
    }

    private JMenuItem getAboutItem() {
        final String ABOUT_TITLE = "About " + PROGRAM_NAME;
        return getMenuItem(ABOUT_TITLE, KeyEvent.VK_A, new Runnable() {
            public void run() {
                String message = PROGRAM_NAME + " v." + MonitorGUIConstants.VERSION + ", built on " +
                        MonitorGUIConstants.BUILD_DATE;
                JOptionPane.showMessageDialog(null, message, ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public JMenu getHelpMenu() {
        return menu;
    }
}
