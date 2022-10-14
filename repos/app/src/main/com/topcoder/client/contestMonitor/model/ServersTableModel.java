package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.util.BooleanUtils;

import javax.swing.table.AbstractTableModel;

final class ServersTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {
        "ServerID",
        "Status",
        "IP Address",
        "Port",
    };

    private final MonitorController controller;

    ServersTableModel(MonitorController controller) {
        this.controller = controller;
    }

    public int getRowCount() {
        return controller.getNumServers();
    }

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        MonitorServerConnection serverConn = controller.getServer(rowIndex);
        switch (columnIndex) {
        case 0:
            return new Integer(rowIndex);
        case 1:
            return BooleanUtils.valueOf(serverConn.isConnected());
        case 2:
            return serverConn.getHostAddress();
        case 3:
            return new Integer(serverConn.getPort());
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public String getColumnName(int column) {
        return COLUMN_NAME[column];
    }

    void serverStatusChanged(int id) {
        fireTableCellUpdated(id, 1);
    }

}
