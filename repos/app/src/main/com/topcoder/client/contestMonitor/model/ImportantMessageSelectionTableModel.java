package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Date;
import java.util.Iterator;

public final class ImportantMessageSelectionTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(ImportantMessageSelectionTableModel .class);

    private static final int MESSAGE_ID = 0;
    private static final int MESSAGE = 1;
    private static final int START_DATE = 2;
    private static final int END_DATE = 3;
    private static final int STATUS = 4;


    public ImportantMessageSelectionTableModel() {
        super(new String[]{
            "ID",
            "Message",
            "Start Date",
            "End Date"
        }, new Class[]{
            String.class, // id
            String.class, // name
            Date.class, // start
            Date.class // end
        });
        addSortElement(new SortElement(START_DATE, true));
        addSortElement(new SortElement(END_DATE, true));
        addSortElement(new SortElement(MESSAGE_ID, false));
        addSortElement(new SortElement(MESSAGE, false));
    }

    public ImportantMessageData getMessage(int row) {
        return (ImportantMessageData) get(row);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ImportantMessageData message = getMessage(rowIndex);
        switch (columnIndex) {
        case MESSAGE_ID:
            return new Integer(message.getId());
        case MESSAGE:
            return message.getMessage();
        case START_DATE:
            return message.getStartDate();
        case END_DATE:
            return message.getEndDate();
        case STATUS:
            return new Integer(message.getStatus());
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }

    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        ImportantMessageData c1 = (ImportantMessageData) o1;
        ImportantMessageData c2 = (ImportantMessageData) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case MESSAGE_ID:
                {
                    int diff = c1.getId() - c2.getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case MESSAGE:
                {
                    int diff = compareStrings(c1.getMessage(), c2.getMessage());
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
                    int diff = c1.getStatus() - c2.getStatus();
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
