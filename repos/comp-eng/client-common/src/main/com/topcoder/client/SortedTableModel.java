/*
 * User: Mike Cervantes (emcee) Date: May 18, 2002 Time: 6:52:41 PM
 */
package com.topcoder.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Defines an abstract table model, which can sort the rows according to column values. There can be multiple sorted
 * columns. The columns are listed from the most significant to the least significant.
 * 
 * @author Mike Cervantes
 * @version $Id: SortedTableModel.java 71772 2008-07-18 07:46:22Z qliu $
 */
public abstract class SortedTableModel extends AbstractTableModel implements Comparator {
    /**
     * Creates a new instance of <code>SortedTableModel</code>. The column names and the classes of the column values
     * are given.
     * 
     * @param columnNames the column names of the table.
     * @param columnClasses the classes of the column values in the table.
     */
    protected SortedTableModel(String[] columnNames, Class[] columnClasses) {
        super();
        COLUMN_NAME = columnNames;
        this.columnClasses = columnClasses;
    }

    /** Represents the column names. */
    private String[] COLUMN_NAME;

    /** Represents the column value classes. */
    private Class[] columnClasses;

    /** Represents a flag indicating if the first row can be sorted or not. */
    private boolean zeroAllowed = false;

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public Class getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public String getColumnName(int column) {
        return COLUMN_NAME[column];
    }

    /**
     * Gets the values of a row. The index of the row is given.
     * 
     * @param index the index of the row.
     * @return the values of a row.
     */
    public Object get(int index) {
        return itemList.get(index);
    }

    /**
     * Gets an iterator of the sorting columns, from most significant to least significant.
     * 
     * @return an iterator of sorting columns.
     */
    protected Iterator getSortListIterator() {
        return clonedSortList.iterator();
    }

    /**
     * Sets the column to be the most significant sorting column. The rows will be re-sorted after this changes.
     * 
     * @param column the column index to be the most significant sorting column.
     * @param opposite a flag indicating if the column values should be sorted descendingly.
     */
    public final void sort(int column, boolean opposite) {
        synchronized (sortList) {
            SortElement elem = new SortElement(column, opposite);
            int ind = sortList.indexOf(elem);
            if (ind == 0) { // two clicks to invert
                SortElement elem2 = (SortElement) sortList.get(0);
                sortList.set(0, new SortElement(column, !elem2.isOpposite()));
            } else if (ind > 0) {
                sortList.remove(ind);
                sortList.add(0, elem);
            }
            syncClone();
        }
        processItemListChanged();
    }

    /** Represents the rows in the table. */
    private final List itemList = new ArrayList();

    /** Represents the sorting columns in the table. */
    private final List sortList = new ArrayList();

    /** Represents a cloned list of sorting columns. */
    private List clonedSortList = new ArrayList();

    private void syncClone() {
        synchronized (sortList) {
            List l = new ArrayList();
            for (int i = 0; i < sortList.size(); i++) {
                l.add(sortList.get(i));
            }
            clonedSortList = l;
        }
    }

    /**
     * Gets the list of sorting columns. There is no copy.
     * 
     * @return the list of sorting columns.
     */
    public List getSortList() {
        return sortList;
    }

    /**
     * Clears the sorting columns.
     */
    public void clearSortList() {
        synchronized (sortList) {
            sortList.clear();
            syncClone();
        }
    }

    /**
     * Adds the sorting column to be the least significant sorting column.
     * 
     * @param e the sorting column to be added.
     */
    protected void addSortElement(SortElement e) {
        synchronized (sortList) {
            sortList.add(e);
            syncClone();
        }
    }

    public int getRowCount() {
        return itemList.size();
    }

    /**
     * Gets the collection of rows in the table.
     * 
     * @return the collection of rows.
     */
    protected Collection getItemList() {
        return itemList;
    }

    /**
     * Sorts the rows according to the current sorting columns.
     */
    protected final void sort() {
        Collections.sort(itemList, this);
    }

    /**
     * Compares two dates. <code>null</code> is less than everything else except <code>null</code>.
     * 
     * @param d1 a date to be compared.
     * @param d2 a date to be compared to.
     * @return 0 if the two dates are the same; a positive integer if <code>d2</code> is less than <code>d1</code>;
     *         a negative integer otherwise.
     */
    protected static int compareDates(Date d1, Date d2) {
        if (d1 == null)
            return d2 == null ? 0 : -1;
        if (d2 == null)
            return 1;
        return d1.compareTo(d2);
    }

    /**
     * Compares two strings. <code>null</code> is less than everything else except <code>null</code>. The
     * comparsion is case-insensitive.
     * 
     * @param s1 a string to be compared.
     * @param s2 a string to be compared to.
     * @return 0 if the two strings are the same; a positive integer if <code>s2</code> is less than <code>s1</code>;
     *         a negative integer otherwise.
     */
    protected static int compareStrings(String s1, String s2) {
        if (s1 == null)
            return s2 == null ? 0 : -1;
        if (s2 == null)
            return 1;

        return s1.compareToIgnoreCase(s2);
    }

    /**
     * Clears all rows in the table.
     */
    public void clear() {
        itemList.clear();
        fireTableDataChanged();
    }

    /**
     * Updates the rows in the table. All rows are replaced by the given collection.
     * 
     * @param items the new rows to replace rows in the table.
     */
    public void update(Collection items) {
        itemList.clear();
        itemList.addAll(items);
        processItemListChanged();
    }

    /**
     * Adds a row to the end of table.
     * 
     * @param item the row to be added.
     */
    public void add(Object item) {
        itemList.add(item);
        processItemListChanged();
    }

    /**
     * Removes a row from the table.
     * 
     * @param item the row to be removed.
     * @return <code>true</code> if the row is removed; <code>false</code> otherwise.
     */
    public boolean remove(Object item) {
        boolean r = itemList.remove(item);
        processItemListChanged();
        return r;
    }

    /**
     * Processes when the rows in the table has been changed. It re-sorts the table and notify the table to update.
     */
    protected void processItemListChanged() {
        sort();
        fireTableDataChanged();
    }

    /**
     * Removes the row with the given index.
     * 
     * @param row the index of the row to be removed.
     * @return the removed row.
     */
    public Object remove(int row) {
        Object r = itemList.remove(row);
        processItemListChanged();
        return r;
    }

    /**
     * Sets a flag indicating if the first row can be sorted or not.
     * 
     * @param allowed a flag indicating if the first row can be sorted or not.
     */
    public void setZeroAllowed(boolean allowed) {
        this.zeroAllowed = allowed;
    }

    /**
     * Swaps two rows with the given indices. The table is updated. This is used to sort the rows.
     * 
     * @param r1 the row index to be swapped.
     * @param r2 the row index to be swapped.
     * @throws IllegalArgumentException if <code>r1</code> or <code>r2</code> exceeds the maximum allowed index; or
     *             <code>r1</code> or <code>r2</code> is 0 while the first row cannot be sorted.
     */
    public void swapRows(int r1, int r2) {
        if (r1 <= 0 || r1 > itemList.size()) {
            if (r1 == 0 && !zeroAllowed)
                throw new IllegalArgumentException("Invalid row: " + r1);
        }
        if (r2 <= 0 || r2 > itemList.size()) {
            if (r2 == 0 && !zeroAllowed)
                throw new IllegalArgumentException("Invalid row: " + r2);
        }
        Object o1 = itemList.get(r1);
        Object o2 = itemList.get(r2);
        itemList.set(r1, o2);
        itemList.set(r2, o1);
        fireTableDataChanged();
    }
}
