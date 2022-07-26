package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MenuModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.common.MenuConstants;

/**
 * An implementation of the MenuModel interface.
 *
 * @author mitalub
 */
public class MenuModelImpl extends MenuModel {

    private String[] menuHeaders;
    private String[][] menuItems;
    private int[][] menuIds;

    public void init() {
        menuHeaders = new String[0];
        menuItems = new String[0][0];
        menuIds = new int[0][0];
    }

    public String[] getMenuHeaders() {
        return menuHeaders;
    }

    public void setMenuHeaders(String[] menuHeaders) {
        this.menuHeaders = menuHeaders;
    }

    public String[] getMenuItems(int index) {
        return menuItems[index];
    }

    public void setMenuItems(int index, String[] menuItems) {
        this.menuItems[index] = menuItems;
    }

    public String[][] getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(String[][] menuItems) {
        this.menuItems = menuItems;
    }

    public int[] getMenuIds(int index) {
        return menuIds[index];
    }

    public void setMenuIds(int index, int[] menuIds) {
        this.menuIds[index] = menuIds;
    }

    public int[][] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(int[][] menuIds) {
        this.menuIds = menuIds;
    }
}
