package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.MenuView;
import com.topcoder.client.mpsqasApplet.view.JMenuBarView;
import com.topcoder.client.mpsqasApplet.model.MenuModel;
import com.topcoder.client.mpsqasApplet.controller.MenuController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * An implementation of the menu view.
 *
 * @author mitalub
 */
public class MenuViewImpl extends JMenuBarView
        implements MenuView, ActionListener {

    private MenuModel model;
    private MenuController controller;
    private JMenuItem[][] menuItems;

    public void init() {
        model = MainObjectFactory.getMenuModel();
        controller = MainObjectFactory.getMenuController();
        buildMenuItems();
        model.addWatcher(this);
    }

    public void update(Object arg) {
        buildMenuItems();
        repaint();
    }

    private void buildMenuItems() {
        int i, j;
        JMenu tempMenu;
        JMenuItem tempMenuItem;
        String[] menuHeaders = model.getMenuHeaders();
        String[] menuList;
        menuItems = new JMenuItem[menuHeaders.length][];

        for (i = 0; i < menuHeaders.length; i++) {
            tempMenu = new JMenu(menuHeaders[i]);
            menuList = model.getMenuItems(i);
            menuItems[i] = new JMenuItem[menuList.length];
            for (j = 0; j < menuList.length; j++) {
                tempMenuItem = new JMenuItem(menuList[j]);
                tempMenuItem.addActionListener(this);
                tempMenu.add(tempMenuItem);
                menuItems[i][j] = tempMenuItem;
            }
            add(tempMenu);
        }
    }

    public void actionPerformed(ActionEvent e) {
        int i, j;
        boolean done = false;
        for (i = 0; !done && i < menuItems.length; i++)
            for (j = 0; !done && j < menuItems[i].length; j++)
                if (menuItems[i][j] == e.getSource()) {
                    done = true;
                    controller.processMenuChoice(model.getMenuIds(i)[j]);
                }
    }
}
