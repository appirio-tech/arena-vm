package com.topcoder.client.mpsqasApplet.view.defaultimpl.tablemodel;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.table.*;

/**
 * A table for use by mpsqas where the table is row selectectable and clicking
 * on the table header will sort the elements in the table.  Defaults to
 * a non editable table with row selection.
 *
 * @author mitalub
 */
public class SortableTable extends JTable {

    /**
     * Sets up the table by setting properties and data.
     *
     * @param columnNames The names of the columns in the table.
     * @param data Data to be stored in table.
     */
    public SortableTable(Object[] columnNames, Object[][] data) {
        tableModel = new SortableTableModel();
        cellRenderer = new CellRenderer();
        sortButtonRenderer = new SortButtonRenderer();

        TableColumnModel columnModel = getColumnModel();
        JTableHeader header = getTableHeader();

        //set properties
        tableModel.setData(data);
        tableModel.setColumnNames(columnNames);
        setModel(tableModel);
        tableModel.setEditable(false);
        header.setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (int i = 0; i < columnNames.length; i++) {
            columnModel.getColumn(i).setCellRenderer(new CellRenderer());
            columnModel.getColumn(i).setHeaderRenderer(sortButtonRenderer);
        }

        //listener for header clicks
        header.addMouseListener(new AppletMouseListener("headerClicked",
                this,
                "mouseClicked"));

        //originally, sort by first column.
        sortButtonRenderer.setPressedColumn(0);
        sortButtonRenderer.setSelectedColumn(0);
        sortCol = 0;
        if (SortButtonRenderer.DOWN == sortButtonRenderer.getState(0)) {
            isAscent = true;
        } else {
            isAscent = false;
        }
        tableModel.sortByColumn(sortCol, isAscent);
    }

    /**
     * Same as first constructor, except sets preferred table widths.
     *
     * @param columnNames The names of the columns in the table.
     * @param data Data to be stored in table.
     * @param columnWidths Preferred widths of table columns.
     */
    public SortableTable(Object[] columnNames, Object[][] data, int[] columnWidths) {
        this(columnNames, data);
        for (int i = 0; i < columnWidths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    /**
     * Same as second constructor, excepts sets editible columns.
     *
     * @param columnNames The names of the columns in the table
     * @param data Data to be stored in table.
     * @param columnWidths Preferred widths of the columns.
     * @param columnEditable Array of booleans whether each column is editable.
     */
    public SortableTable(Object[] columnNames, Object[][] data, int[] columnWidths,
            boolean[] columnEditable) {
        this(columnNames, data, columnWidths);
        tableModel.setEditable(columnEditable);
    }


    /**
     * When a header is clicked, data is sorted by that column.
     *
     * @param e The MouseEvent of clicking the column header.
     */
    public void headerClicked(MouseEvent e) {
        int col = getTableHeader().columnAtPoint(e.getPoint());
        sortCol = convertColumnIndexToModel(col);

        sortButtonRenderer.setPressedColumn(col);
        sortButtonRenderer.setSelectedColumn(col);

        getTableHeader().repaint();

        if (SortButtonRenderer.DOWN == sortButtonRenderer.getState(col)) {
            isAscent = true;
        } else {
            isAscent = false;
        }

        tableModel.sortByColumn(sortCol, isAscent);
    }

    /**
     * Returns the actual index of the selected row (actual = original).
     */
    public int getSelectedRow() {
        int selectedIndex = super.getSelectedRow();
        if (selectedIndex == -1) {
            return -1;
        } else {
            return tableModel.getIndexes()[selectedIndex];
        }
    }

    /**
     * Resets the data in the table, and sets the sort to the first column again.
     *
     * @param tableData New table data
     */
    public void setData(Object[][] tableData) {
        tableModel.setData(tableData);
        resort();
    }

    /**
     * Sorts the data in table using last column and ascent values.
     */
    public void resort() {
        tableModel.sortByColumn(sortCol, isAscent);
    }

    /**
     * Returns a handle on the table model.
     */
    public SortableTableModel getTableModel() {
        return tableModel;
    }

    public Object getAbsoluteValueAt(int row, int col) {
        return tableModel.getData()[row][col];
    }

    public void setAbsoluteValueAt(Object value, int row, int col) {
        tableModel.getData()[row][col] = value;
    }

    private int sortCol;
    private boolean isAscent;

    private CellRenderer cellRenderer;
    private SortButtonRenderer sortButtonRenderer;
    private SortableTableModel tableModel;
}
