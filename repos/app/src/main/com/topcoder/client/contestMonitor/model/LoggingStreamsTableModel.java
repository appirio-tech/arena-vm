package com.topcoder.client.contestMonitor.model;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Date;
import java.util.Iterator;

public final class LoggingStreamsTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(LoggingStreamsTableModel.class);

    private static final int NAME = 0;
    private static final int HOST = 1;
    private static final int OWNER = 2;
    private static final int BORN_ON = 3;


    public LoggingStreamsTableModel() {
        super(new String[]{
            "Name",
            "Host",
            "Owner",
            "Born On"
        }, new Class[]{
            String.class, // name
            String.class, // host
            String.class, //owner
            Date.class // born on
        });
        addSortElement(new SortElement(HOST, false));
        addSortElement(new SortElement(OWNER, false));
        addSortElement(new SortElement(NAME, false));
        addSortElement(new SortElement(BORN_ON, false));
    }


    public StreamID getStream(int rowIndex) {
        return (StreamID) get(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        StreamID stream = getStream(rowIndex);
        switch (columnIndex) {
        case NAME:
            return stream.getName();
        case HOST:
            return stream.getHost();
        case OWNER:
            return stream.getOwner();
        case BORN_ON:
            return stream.getBornOn();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        StreamID s1 = (StreamID) o1;
        StreamID s2 = (StreamID) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case NAME:
                {
                    String name1 = s1.getName();
                    String name2 = s2.getName();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case HOST:
                {
                    String name1 = s1.getHost();
                    String name2 = s2.getHost();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case OWNER:
                {
                    String name1 = s1.getOwner();
                    String name2 = s2.getOwner();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case BORN_ON:
                {
                    int diff = s1.getBornOn().compareTo(s2.getBornOn());
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
