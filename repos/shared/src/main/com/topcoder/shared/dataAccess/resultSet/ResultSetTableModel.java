package com.topcoder.shared.dataAccess.resultSet;


import javax.swing.table.AbstractTableModel;

/**
 * This class is intended for use as a TableModel with the data in a given
 * <tt>ResultSetContainer</tt>.  Useful, for example, in <tt>JTable</tt>
 * construction.
 *
 * @author  Dave Pecora
 * @version 1.0, 06/22/2002
 */

public class ResultSetTableModel extends AbstractTableModel {

    private ResultSetContainer dataContainer;

    public ResultSetTableModel(ResultSetContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public int getColumnCount() {
        return dataContainer.getColumnCount();
    }

    public int getRowCount() {
        return dataContainer.getRowCount();
    }

    public String getColumnName(int col) {
        return dataContainer.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        return dataContainer.getItem(row, col);
    }

    public Class getColumnClass(int col) {
        if (getRowCount() > 0) {
            return getValueAt(0, col).getClass();
        } else {
            // If there's no data, just return any reasonable value
            return Object.class;
        }
    }
}

