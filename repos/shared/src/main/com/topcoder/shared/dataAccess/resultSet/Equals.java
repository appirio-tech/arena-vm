package com.topcoder.shared.dataAccess.resultSet;

/**
 * User: dok
 * Date: Sep 8, 2004
 * Time: 2:45:46 AM
 */
public class Equals implements ResultFilter {

    private Object o = null;
    private int col = -1;
    private String colName = null;

    public Equals(Object o, int col) {
        this.o = o;
        if (col < 0) throw new IllegalArgumentException("Column must be greater than 0");
        this.col = col;
    }

    public Equals(Object o, String colName) {
        this.o = o;
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


        return (o==null&&item.getResultData()==null)||o.equals(item.getResultData());

    }
}

