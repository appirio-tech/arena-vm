package com.topcoder.client.contestMonitor.model;


import com.topcoder.server.AdminListener.response.RoundAccessItem;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Date;
import java.util.Iterator;


public final class RoundAccessTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(RoundAccessTableModel.class);

    private static String[] getColumnNames() {
        return new String[]{
            "ID",
            "Name",
            "Date"
        };
    }

    private static Class[] getColumnClasses() {
        return new Class[]{
            String.class, // id
            String.class, // name
            Date.class
        };
    }

    private static final int ROUND_ID = 0;
    private static final int NAME = 1;
    private static final int DATE = 2;

    public RoundAccessTableModel() {
        super(getColumnNames(), getColumnClasses());
        addSortElement(new SortElement(DATE, true));
        addSortElement(new SortElement(NAME, false));
        addSortElement(new SortElement(ROUND_ID, false));
    }


    public RoundAccessItem getRound(int rowIndex) {
        return (RoundAccessItem) get(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        RoundAccessItem round = getRound(rowIndex);
        switch (columnIndex) {
        case ROUND_ID:
            return new Integer(round.getId());
        case NAME:
            return round.getName();
        case DATE:
            return round.getStartDate();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        RoundAccessItem r1 = (RoundAccessItem) o1;
        RoundAccessItem r2 = (RoundAccessItem) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case ROUND_ID:
                {
                    Integer id1 = new Integer(r1.getId());
                    Integer id2 = new Integer(r2.getId());
                    int diff = id1.compareTo(id2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case NAME:
                {
                    String name1 = r1.getName();
                    String name2 = r2.getName();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case DATE:
                {
                    Date d1 = r1.getStartDate();
                    long t1 = d1 == null ? 0 : d1.getTime();
                    Date d2 = r2.getStartDate();
                    long t2 = d2 == null ? 0 : d2.getTime();
                    long diff = t1 - t2;
                    if (diff != 0) {
                        return sign * (diff < 0 ? -1 : 1);
                    }
                }
                break;
            default:
                throw new RuntimeException("not implemented, column=" + sortElem);
            }
        }
        error("problem with sorting, o1=" + o1 + ", o2" + o2);
        return 0;
    }
}
