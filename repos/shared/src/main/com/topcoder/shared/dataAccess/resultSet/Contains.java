package com.topcoder.shared.dataAccess.resultSet;

import com.topcoder.shared.util.logging.Logger;

/**
 * User: dok
 * Date: Sep 8, 2004
 * Time: 2:48:13 AM
 */
public class Contains implements ResultFilter {
    private static Logger log = Logger.getLogger(Contains.class);

    private String s = null;
    private int col = -1;
    private String colName = null;

    public Contains(String s, int col) {
        if (s==null) throw new NullPointerException("String must not be null");
        this.s = s;
        if (col<0) throw new IllegalArgumentException("Column must be greater than 0");
        this.col = col;
    }

    public Contains(String s, String colName) {
        if (s==null) throw new NullPointerException("String must not be null");
        this.s = s;
        if (colName==null) throw new NullPointerException("Column name must not be null");
        this.colName = colName;
    }


    public boolean include(ResultSetContainer.ResultSetRow rsr) {

        TCResultItem item = null;
        if (col>-1) {
            item = rsr.getItem(col);
        } else {
            item = rsr.getItem(colName);
        }

        String other = item.getResultData().toString();
        if (other==null) return false;
        //log.debug(" other: " + other + " s: " + s);

        return other.toLowerCase().indexOf(s.toLowerCase())>-1;

    }
}
