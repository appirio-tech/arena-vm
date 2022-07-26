package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.panels.table.UserNameEntry;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;

import java.util.*;
import javax.swing.*;

public class ContestTableModel extends SortedTableModel {

    private ArrayList rowCache;
    private int primaryKey;
    private boolean cellEditableStatus = false;
    private boolean removeDuplicates = true;
    private int colCount;

    public ContestTableModel(String[] headers, Class[] clazzes) {
        super(headers, clazzes);
        rowCache = new ArrayList(0);
        this.primaryKey = 0;
        colCount = getColumnCount();
    }

    public ContestTableModel(String[] headers, Class[] clazzes, int primaryKey) {
        super(headers, clazzes);
        rowCache = new ArrayList(0);
        this.primaryKey = primaryKey;
        colCount = getColumnCount();
    }


    public Object getValueAt(int row, int col) {
        if (row < rowCache.size() && col < ((ArrayList) rowCache.get(row)).size()) {
            return ((ArrayList) rowCache.get(row)).get(col);
        } else {
            throw new IllegalArgumentException("out of bounds - tableModel.getValueAt(" + row + "," + col + ")");
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    public boolean isCellEditable(int row, int col)
            ////////////////////////////////////////////////////////////////////////////////
    {
        return cellEditableStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setCellEditable(boolean status)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.cellEditableStatus = status;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setRemoveDuplicates(boolean status)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.removeDuplicates = status;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setValueAt(Object aValue, int row, int col)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if ((row < rowCache.size()) && (col < colCount)) {
            ((ArrayList) rowCache.get(row)).set(col, aValue);
            fireTableCellUpdated(row, col);
        }
    }

//  public void setHeader(String [] h)
//  {
//    this.headers = h;
//    colCount = h.length;
//    clear();
//    fireTableStructureChanged();
//  }

    ////////////////////////////////////////////////////////////////////////////////
    public void setPrimaryKey(int pk)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.primaryKey = pk;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean hasRow(int col, Object value)
            ////////////////////////////////////////////////////////////////////////////////
    {
        boolean found = false;

        if (indexOfRow(col, value) != -1) {
            found = true;
        }

        return found;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public Object indexValueOfRow(int col, Object value)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int row = indexOfRow(col, value);
        return (getValueAt(row, col));
    }

    ////////////////////////////////////////////////////////////////////////////////
    public int indexOfRow(int col, Object value)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int index = -1;

        if (col <= colCount) {
            for (int i = 0; i < rowCache.size(); i++) {
                Object cell = ((ArrayList) rowCache.get(i)).get(col);
                if (cell instanceof JLabel) {
                    cell = ((JLabel) cell).getText();
                }

                if (cell.toString().equals(value.toString())) {
                    index = i;
                    break;
                }
            }
        }

        return (index);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void swapRows(int row1, int row2)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ArrayList rowData1 = (ArrayList) rowCache.get(row1);
        ArrayList rowData2 = (ArrayList) rowCache.get(row2);
        rowCache.set(row1, rowData2);
        rowCache.set(row2, rowData1);
        fireTableDataChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////
//  public void addEmptyRow(Object defaultData)
//  ////////////////////////////////////////////////////////////////////////////////
//  {
//    ArrayList row = new ArrayList(colCount);
//    for (int i=0; i<colCount; i++) { row.add(defaultData); }
//    rowCache.add(row);
//    fireTableDataChanged();
//  }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean addRow(ArrayList rowData)
            ////////////////////////////////////////////////////////////////////////////////
    {
        boolean status = true;

        if (rowData.size() >= colCount) {
            if (removeDuplicates) {
                Object key = rowData.get(primaryKey);
                if (key instanceof JLabel) {
                    key = ((JLabel) key).getText();
                }
                removeContestant(key.toString());
            }
            rowCache.add(rowData);
            fireTableDataChanged();
        } else {
            System.out.println("RowData size = " + rowData.size() + ", colCount = " + colCount);
            status = false;
        }

        return (status);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void addRows(ArrayList rows)
            ////////////////////////////////////////////////////////////////////////////////
    {
        rowCache.addAll(rows);
        fireTableDataChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void updateRow(int row, ArrayList rowData)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (row >= 0 && row < rowCache.size() && rowData.size() >= colCount) {
            rowCache.set(row, rowData);
            fireTableDataChanged();
        } else {
            throw new IllegalArgumentException("Bad parameters to tableModel.updateRow()!");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void removeContestant(String value)
            ////////////////////////////////////////////////////////////////////////////////
    {
        for (int i = 0; i < rowCache.size(); i++) {
            Object cell = ((ArrayList) rowCache.get(i)).get(primaryKey);

            if (cell instanceof JLabel) {
                cell = ((JLabel) cell).getText();
            } else if (cell instanceof UserNameEntry) {
                cell = ((UserNameEntry) cell).getName();
            }

            if (cell.toString().equalsIgnoreCase(value)) {
                rowCache.remove(i);
            }
            fireTableDataChanged();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void removeContestant(int row)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (row >= 0 && row < rowCache.size()) {
            rowCache.remove(row);
            fireTableDataChanged();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList getData()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (rowCache);
    }

    public ArrayList getRow(int row) {
        return (ArrayList) rowCache.get(row);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setData(ArrayList rows)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (rows.size() > 0) {
            String[] h = new String[((ArrayList) rows.get(0)).size()];
            colCount = h.length;
//      setHeader(h);
            rowCache = rows;
        }
        fireTableStructureChanged();
    }

//  ////////////////////////////////////////////////////////////////////////////////
//  public void reset(int r, int c, Object defaultData)
//  ////////////////////////////////////////////////////////////////////////////////
//  {
//    clear();
//
//    String [] h = new String[c];
//    colCount = h.length;
//    setHeader(h);
//
//    for (int i=0; i<r; i++) {
//     ArrayList row = new ArrayList(c);
//     for (int j=0; j<c; j++) {
//       row.add(defaultData);
//     }
//     rowCache.add(row);
//    }
//
//    fireTableStructureChanged();
//  }

    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        rowCache.clear();
        fireTableDataChanged();
    }

    public int compare(Object o1, Object o2) {
        List list1 = (List) o1;
        List list2 = (List) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElement = (SortElement) it.next();
            int sign = sortElement.isOpposite() ? -1 : 1;
            int col = sortElement.getColumn();
            Object item1 = list1.get(col);
            Object item2 = list2.get(col);
            if (item1 instanceof Comparable && item2 instanceof Comparable) {
                Comparable c1 = (Comparable) item1;
                Comparable c2 = (Comparable) item2;
                int diff = c1.compareTo(c2);
                if (diff != 0) return sign * diff;
            }
        }
        return 0;
    }
}
