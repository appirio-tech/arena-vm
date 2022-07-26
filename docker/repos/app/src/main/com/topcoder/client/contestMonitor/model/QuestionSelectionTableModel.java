package com.topcoder.client.contestMonitor.model;


import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.QuestionType;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Iterator;

public final class QuestionSelectionTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(QuestionSelectionTableModel.class);

    private static final int QUESTION_ID = 0;
    private static final int KEYWORD = 1;
    private static final int TYPE = 2;
    private static final int TEXT = 3;


    public QuestionSelectionTableModel() {
        super(new String[]{
            "ID",
            "Keyword",
            "Type",
            "Text"
        }, new Class[]{
            Integer.class, // id
            String.class, // keyword
            QuestionType.class, // type
            String.class // text
        });
        addSortElement(new SortElement(KEYWORD, false));
    }

    public QuestionData getQuestion(int rowIndex) {
        return (QuestionData) get(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        QuestionData question = getQuestion(rowIndex);
        switch (columnIndex) {
        case QUESTION_ID:
            return new Integer(question.getId());
        case KEYWORD:
            return question.getKeyword();
        case TYPE:
            return question.getType();
        case TEXT:
            return question.getText();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        QuestionData q1 = (QuestionData) o1;
        QuestionData q2 = (QuestionData) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case QUESTION_ID:
                {
                    int diff = q1.getId() - q2.getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case KEYWORD:
                {
                    String k1 = q1.getKeyword();
                    String k2 = q2.getKeyword();
                    int diff = k1.compareTo(k2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case TYPE:
                {
                    int diff = q1.getType().getId() - q2.getType().getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case TEXT:
                {
                    String t1 = q1.getText();
                    String t2 = q2.getText();
                    int diff = t1.compareTo(t2);
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
