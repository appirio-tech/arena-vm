package com.topcoder.client.contestMonitor.model;

import com.topcoder.client.SortElement;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class ConnectionsTableModel extends AbstractTableModel implements Comparator {

    private static final String[] COLUMN_NAME = {
        "ServerID",
        "ID",
        "IP Address",
        "Username",
        "1 sec",
        "5 sec",
        "10 sec",
        "total",
    };

    private static final int SERVER_ID = 0;
    private static final int ID = 1;
    private static final int IP_ADDRESS = 2;
    private static final int USERNAME = 3;
    private static final int NUMBERS_START = 4;

    private final MonitorController controller;
    private final List itemList = new ArrayList();
    private final List sortList = new ArrayList();

    ConnectionsTableModel(MonitorController controller) {
        this.controller = controller;
        sortList.add(new SortElement(0, false));
        sortList.add(new SortElement(1, false));
    }

    public void sort(int column, boolean opposite) {
        SortElement elem = new SortElement(column, opposite);
        int ind = sortList.indexOf(elem);
        if (ind == 0 && ((SortElement) sortList.get(0)).isOpposite() == opposite) {
            return;
        }
        if (ind > 0) {
            sortList.remove(ind);
        }
        sortList.add(0, elem);
        sort();
        fireTableDataChanged();
    }

    public synchronized int getRowCount() {
        return itemList.size();
    }

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public synchronized ConnectionItem getConnection(int rowIndex) {
        return (ConnectionItem) itemList.get(rowIndex);
    }

    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        // dpecora - Sometimes a repaint request will ask for a row no longer
        // in the table, as the table frequently shrinks.  Return dummy objects
        // in this case.
        ConnectionItem conn = null;
        if (rowIndex >= getRowCount()) {
            conn = new ConnectionItem(-1, -1, "0.0.0.0");
            conn.setUsername("Dummy connection");
        } else {
            conn = getConnection(rowIndex);
        }

        switch (columnIndex) {
        case SERVER_ID:
            return conn.getServerId();
        case ID:
            return conn.getConnId();
        case IP_ADDRESS:
            return conn.getIp();
        case USERNAME:
            return conn.getUsername();
        default:
            int totalIndex = columnIndex - NUMBERS_START;
            if (0 <= totalIndex && totalIndex < ConnectionItem.MAX_PERIODS) {
                return new Integer(conn.getTotal(totalIndex));
            }
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public String getColumnName(int column) {
        return COLUMN_NAME[column];
    }

    // dpecora - Synchronize with data access routines.  This doesn't
    // resolve the repaint race condition issue but will prevent
    // race conditions within this module.
    private synchronized void load() {
        int size = controller.getNumConnections();
        itemList.clear();
        for (int i = 0; i < size; i++) {
            itemList.add(controller.getConnection(i));
        }
    }

    private void error(String msg) {
        System.out.println("error: " + msg);
    }

    public int compare(Object o1, Object o2) {
        ConnectionItem ci1 = (ConnectionItem) o1;
        ConnectionItem ci2 = (ConnectionItem) o2;
        for (Iterator it = sortList.iterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case SERVER_ID:
                {
                    Integer serverID1 = ci1.getServerId();
                    Integer serverID2 = ci2.getServerId();
                    int diff = serverID1.compareTo(serverID2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case ID:
                {
                    Integer connID1 = ci1.getConnId();
                    Integer connID2 = ci2.getConnId();
                    int diff = connID1.compareTo(connID2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case IP_ADDRESS:
                {
                    String ip1 = ci1.getAlignedIP();
                    String ip2 = ci2.getAlignedIP();
                    int diff = ip1.compareTo(ip2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case USERNAME:
                {
                    String username1 = ci1.getUsername();
                    String username2 = ci2.getUsername();
                    int diff = username1.compareTo(username2);
                    if (diff == 0) {
                        if (!username1.equals("")) {
                            error("the same usernames, o1=" + o1 + ", o2=" + o2);
                        }
                    } else {
                        return diff * sign;
                    }
                }
                break;
            default:
                int totalIndex = col - NUMBERS_START;
                if (0 <= totalIndex && totalIndex < ConnectionItem.MAX_PERIODS) {
                    int t1 = ci1.getTotal(totalIndex);
                    int t2 = ci2.getTotal(totalIndex);
                    int diff = t2 - t1;
                    if (diff != 0) {
                        return diff * sign;
                    }
                } else {
                    throw new RuntimeException("not implemented, column=" + sortElem);
                }
            }
        }
        error("problem with sorting, o1=" + o1 + ", o2" + o2);
        return 0;
    }

    private void sort() {
        Collections.sort(itemList, this);
    }

    void updateConnectionsTable() {
        load();
        sort();
        fireTableDataChanged();
    }

}
