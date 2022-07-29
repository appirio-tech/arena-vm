package com.topcoder.client.contestMonitor.model;


import com.topcoder.server.contest.RoundData;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Iterator;


public final class RoundSelectionTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(RoundSelectionTableModel.class);

    private static String[] getColumnNames() {
        return new String[]{
            "ID",
            "Name",
            "Status"
        };
    }

    private static Class[] getColumnClasses() {
        return new Class[]{
            String.class, // id
            String.class, // name
            String.class  // status
        };
    }

    private static final int ROUND_ID = 0;
    private static final int NAME = 1;
    private static final int STATUS = 2;

    public RoundSelectionTableModel() {
        super(getColumnNames(), getColumnClasses());
        addSortElement(new SortElement(ROUND_ID, false));
        addSortElement(new SortElement(NAME, false));
        addSortElement(new SortElement(STATUS, false));
    }


    public RoundData getRound(int rowIndex) {
        return (RoundData) get(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        RoundData round = getRound(rowIndex);
        switch (columnIndex) {
        case ROUND_ID:
            return new Integer(round.getId());
        case NAME:
            return round.getName();
        case STATUS:
            return round.getStatus();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        RoundData r1 = (RoundData) o1;
        RoundData r2 = (RoundData) o2;
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
            case STATUS:
                {
                    String s1 = r1.getStatus();
                    String s2 = r2.getStatus();
                    int diff = s1.compareTo(s2);
                    if (diff != 0) {
                        return diff * sign;
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
