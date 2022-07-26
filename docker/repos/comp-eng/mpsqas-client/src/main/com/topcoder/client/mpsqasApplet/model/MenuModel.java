package com.topcoder.client.mpsqasApplet.model;

/**
 * An interface for menu models.
 *
 * @author mitalub
 */
public abstract class MenuModel extends Model {

    public abstract String[] getMenuHeaders();

    public abstract void setMenuHeaders(String[] menuHeaders);

    public abstract String[] getMenuItems(int index);

    public abstract void setMenuItems(int index, String[] menuItems);

    public abstract String[][] getMenuItems();

    public abstract void setMenuItems(String[][] menuItems);

    public abstract int[] getMenuIds(int index);

    public abstract void setMenuIds(int index, int[] menuIds);

    public abstract int[][] getMenuIds();

    public abstract void setMenuIds(int[][] menuIds);
}
