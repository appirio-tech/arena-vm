package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.contest.ContestData;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Date;
import java.util.Iterator;

public final class ContestSelectionTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(ContestSelectionTableModel.class);

    private static final int CONTEST_ID = 0;
    private static final int NAME = 1;
    private static final int START_DATE = 2;
    private static final int END_DATE = 3;
    private static final int STATUS = 4;


    public ContestSelectionTableModel() {
        super(new String[]{
            "ID",
            "Name",
            "Start Date",
            "End Date",
            "Status"
        }, new Class[]{
            String.class, // id
            String.class, // name
            Date.class, // start
            Date.class, // end
            String.class  // status
        });
        addSortElement(new SortElement(START_DATE, true));
        addSortElement(new SortElement(END_DATE, true));
        addSortElement(new SortElement(CONTEST_ID, false));
        addSortElement(new SortElement(NAME, false));
        addSortElement(new SortElement(STATUS, false));
    }

    public ContestData getContest(int row) {
        return (ContestData) get(row);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ContestData contest = getContest(rowIndex);
        switch (columnIndex) {
        case CONTEST_ID:
            return new Integer(contest.getId());
        case NAME:
            return contest.getName();
        case START_DATE:
            return contest.getStartDate();
        case END_DATE:
            return contest.getEndDate();
        case STATUS:
            return contest.getStatus();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }

    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        ContestData c1 = (ContestData) o1;
        ContestData c2 = (ContestData) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case CONTEST_ID:
                {
                    int diff = c1.getId() - c2.getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case NAME:
                {
                    int diff = compareStrings(c1.getName(), c2.getName());
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case START_DATE:
                {
                    int diff = compareDates(c1.getStartDate(), c2.getStartDate());
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case END_DATE:
                {
                    int diff = compareDates(c1.getEndDate(), c2.getEndDate());
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case STATUS:
                {
                    int diff = compareStrings(c1.getStatus(), c2.getStatus());
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
