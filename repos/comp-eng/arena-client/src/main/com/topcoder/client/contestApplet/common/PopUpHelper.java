/*
 * PopUpHelper
 * 
 * Created 06/15/2007
 */
package com.topcoder.client.contestApplet.common;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author Diego Belfer (mural)
 * @version $Id: PopUpHelper.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PopUpHelper {

    public static JPopupMenu createPopupMenu(
            String label,
            MenuItemInfo[] menuItemInfo) {
        JPopupMenu popupMenu = new JPopupMenu(label);
        for (int i = 0; i < menuItemInfo.length; i++) {
            MenuItemInfo info = menuItemInfo[i];
            JMenuItem menuItem = new JMenuItem(info.getText());
            menuItem.addActionListener(info.getActionListener());
            popupMenu.add(menuItem);
        }
        return popupMenu;
    }
}
