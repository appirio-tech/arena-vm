package com.topcoder.client.mpsqasApplet.view.defaultimpl.tablemodel;

import java.util.*;
import javax.swing.table.*;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;

/**
 * SortableTableModel is a default table model controling the data, column names
 * and editableness of the cells.
 * Allows the rows to be sorted by a column using the sortByColumn method while
 * retaining the original indexes.
 *
 * @author mitalub
 */
public class SortableTableModel extends AbstractTableModel {

    public SortableTableModel() {
        super();
    }

    public void setEditable(boolean editable) {
        this.editable = new boolean[getColumnCount()];
        for (int i = 0; i < this.editable.length; i++) {
            this.editable[i] = editable;
        }
    }

    public void setEditable(boolean[] editable) {
        this.editable = editable;
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int column) {
/*
    System.out.println(row);
    System.out.println(column);
    System.out.println(data.length);
    System.out.println(data[0].length);
*/
        return data[indexes[row]][column];
    }

    public boolean isCellEditable(int row, int col) {
        return editable[col];
    }

    public void setData(Object[][] data) {
        this.data = data;
        indexes = new int[getRowCount()];
        for (int i = 0; i < getRowCount(); i++) {
            indexes[i] = i;
        }
    }

    public Object[][] getData() {
        return data;
    }

    public void setColumnNames(Object[] columnNames) {
        this.columnNames = columnNames;
    }

    public Object[] getColumnNames() {
        return columnNames;
    }

    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    public void sortByColumn(int col, boolean isAscent) {
        ArrayList indexesA = new ArrayList(getRowCount());
        int i;
        for (i = 0; i < getRowCount(); i++) {
            indexesA.add(new Integer(indexes[i]));
        }
        Collections.sort(indexesA, new ColumnItemComparator(col, isAscent));
        for (i = 0; i < getRowCount(); i++) {
            indexes[i] = ((Integer) indexesA.get(i)).intValue();
        }
        fireTableDataChanged();
    }

    public int[] getIndexes() {
        return indexes;
    }

    private class ColumnItemComparator implements Comparator {

        public ColumnItemComparator(int column, boolean isAscent) {
            this.column = column;
            this.isAscent = isAscent;
        }

        public int compare(Object o1, Object o2) {
            int i1 = ((Integer) o1).intValue();
            int i2 = ((Integer) o2).intValue();
            Object oo1 = data[i1][column];
            Object oo2 = data[i2][column];

            int result =
                    GUIConstants.compareForColumnSort(oo1, oo2);

            if (!isAscent) {
                result = -result;
            }

            return result;
        }

        int column;
        boolean isAscent;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setValueAt(Object value, int row, int col) {
        data[indexes[row]][col] = value;
        fireTableCellUpdated(indexes[row], col);
    }

    private int[] indexes;
    private Object[][] data;
    private Object[] columnNames;
    private boolean[] editable;
}
