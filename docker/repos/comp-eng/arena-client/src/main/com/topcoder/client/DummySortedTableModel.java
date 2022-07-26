/*
 * User: Michael Cervantes Date: Aug 16, 2002 Time: 4:30:28 PM
 */
package com.topcoder.client;

/**
 * Implements a dummy sorted table model. The rows are not sorted, and there is only one column with string values. The
 * column header is empty string.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: DummySortedTableModel.java 71772 2008-07-18 07:46:22Z qliu $
 */
public class DummySortedTableModel extends SortedTableModel {
    /**
     * Creates a new instance of <code>DummySortedTableModel</code>. It contains one column with strings as value and
     * empty string as header.
     */
    public DummySortedTableModel() {
        super(new String[] {""}, new Class[] {String.class});
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return get(rowIndex);
    }

    public int compare(Object o1, Object o2) {
        return 0;
    }
}
