package com.topcoder.shared.dataAccess.resultSet;

/**
 * User: dok
 * Date: Sep 8, 2004
 * Time: 2:44:32 AM
 */
public class LessThan implements ResultFilter {

    private Number n = null;
    private int col = -1;
    private String colName = null;

    public LessThan(Number n, int col) {
        if (n==null) throw new NullPointerException("Number must not be null");
        this.n = n;
        if (col < 0) throw new IllegalArgumentException("Column must be greater than 0");
        this.col = col;
    }

    public LessThan(Number n, String colName) {
        if (n==null) throw new NullPointerException("Number must not be null");
        this.n = n;
        if (colName == null) throw new NullPointerException("Column name must not be null");
        this.colName = colName;
    }


    public boolean include(ResultSetContainer.ResultSetRow rsr) {

        TCResultItem item = null;
        if (col > -1) {
            item = rsr.getItem(col);
        } else {
            item = rsr.getItem(colName);
        }
        Number other = (Number) item.getResultData();
        if (other==null) return false;

        return ((Comparable) other).compareTo(n) < 0;

    }
}


