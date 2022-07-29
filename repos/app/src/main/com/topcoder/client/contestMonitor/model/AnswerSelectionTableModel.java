package com.topcoder.client.contestMonitor.model;


import com.topcoder.server.contest.AnswerData;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Iterator;

public final class AnswerSelectionTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(AnswerSelectionTableModel.class);

    private static final int ANSWER_ID = 0;
    private static final int SORT_ORDER = 1;
    private static final int CORRECT = 2;
    private static final int TEXT = 3;

    public AnswerSelectionTableModel() {
        super(new String[]{
            "ID",
            "Sort Order",
            "Correct",
            "Text"
        }, new Class[]{
            Integer.class, // id
            Integer.class, // sort order
            Boolean.class, // correct
            String.class // text
        });
        addSortElement(new SortElement(SORT_ORDER, false));
    }

    public AnswerData getAnswer(int rowIndex) {
        return (AnswerData) get(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        AnswerData answer = getAnswer(rowIndex);
        switch (columnIndex) {
        case ANSWER_ID:
            return new Integer(answer.getId());
        case TEXT:
            return answer.getText();
        case SORT_ORDER:
            return new Integer(answer.getSortOrder());
        case CORRECT:
            return new Boolean(answer.isCorrect());
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        AnswerData a1 = (AnswerData) o1;
        AnswerData a2 = (AnswerData) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case ANSWER_ID:
                {
                    int diff = a1.getId() - a2.getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case TEXT:
                {
                    String t1 = a1.getText();
                    String t2 = a2.getText();
                    int diff = t1.compareTo(t2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case SORT_ORDER:
                {
                    int diff = a1.getSortOrder() - a2.getSortOrder();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case CORRECT:
                {
                    int c1 = a1.isCorrect() ? 1 : 0;
                    int c2 = a2.isCorrect() ? 1 : 0;
                    int diff = c1 - c2;
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
