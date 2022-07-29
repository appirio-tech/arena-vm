package com.topcoder.client.contestMonitor.model;

import com.topcoder.client.contestMonitor.model.MonitorController.RoundAccess;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

final class RoundsTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAME = {
        "Round ID",
        "Round Name",
    };

    private final MonitorController controller;
    private List list;

    RoundsTableModel(MonitorController controller) {
        this.controller = controller;
        updateRounds();
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        RoundAccess access = (RoundAccess)list.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return new Integer(access.getRoundId());
        case 1:
            return access.getRoundName();
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

    void updateRounds() {
        Map map = controller.getRoundAccessMap();
        synchronized(map) {
            list = new ArrayList(map.values());
        }
        fireTableDataChanged();
    }

}
