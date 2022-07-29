package com.topcoder.client.contestMonitor.model;

/** Table model for assigned problems.
 * @author John Waymouth
 */

import com.topcoder.server.contest.Division;
import com.topcoder.server.contest.ProblemData;
import com.topcoder.server.contest.RoundProblemData;
import com.topcoder.server.contest.RoundType;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Collection;
import java.util.Iterator;

public final class AssignedProblemsTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(AssignedProblemsTableModel.class);

    private static final int PROBLEM_ID = 0;
    private static final int PROBLEM_NAME = 1;
    private static final int PROBLEM_TYPE = 2;
    private static final int STATUS = 3;
    private static final int DIVISION = 4;

    public AssignedProblemsTableModel() {
        super(new String[]{
            "ID",
            "Name",
            "Type",
            "Status",
            "Division"
        }, new Class[]{
            String.class, // id
            String.class, // class
            String.class, // method
            String.class, // status
            Division.class // division
        });
        addSortElement(new SortElement(STATUS, false));
        addSortElement(new SortElement(PROBLEM_ID, false));
        addSortElement(new SortElement(PROBLEM_NAME, false));
        addSortElement(new SortElement(PROBLEM_TYPE, false));
    }


    public RoundProblemData getRoundProblem(int rowIndex) {
        return (RoundProblemData) get(rowIndex);
    }

    public ProblemData getProblem(int rowIndex) {
        return getRoundProblem(rowIndex).getProblemData();
    }

    public Collection getRoundProblems() {
        return getItemList();
    }

    public RoundProblemData removeProblem(int rowIndex) {
        return (RoundProblemData) remove(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        RoundProblemData roundProblem = getRoundProblem(rowIndex);
        ProblemData problem = roundProblem.getProblemData();
        switch (columnIndex) {
        case PROBLEM_ID:
            return new Integer(problem.getId());
        case PROBLEM_NAME:
            return problem.getName();
        case PROBLEM_TYPE:
            return problem.getType();
        case STATUS:
            return problem.getStatus();
        case DIVISION:
            return roundProblem.getDivision();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (value != null) {
            if (columnIndex == DIVISION) {
                RoundProblemData rp = getRoundProblem(rowIndex);
                rp.setDivision((Division) value);
            } else {
                throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int colIndex) {
        return colIndex == DIVISION;
    }

    /**
     * Filter based on type (teams or normal).  This will take the type from its argument, and remove any problems
     * that are not applicable to this round (i.e. a teams problem when this is a singles SRM).
     */

    public void filterType(RoundType rt) {
        //Collection itemList = getItemList();

        //for (Iterator it = itemList.iterator(); it.hasNext();) {
        //    if (rt.isTeam() != ((ProblemData) it.next()).getType().isTeam())
        //        it.remove();
        //}
    }

    private void error(String msg) {
        log.error(msg);
    }

    public int compare(Object o1, Object o2) {
        RoundProblemData rp1 = (RoundProblemData) o1;
        RoundProblemData rp2 = (RoundProblemData) o2;
        ProblemData p1 = rp1.getProblemData();
        ProblemData p2 = rp2.getProblemData();
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case PROBLEM_ID:
                {
                    Integer id1 = new Integer(p1.getId());
                    Integer id2 = new Integer(p2.getId());
                    int diff = id1.compareTo(id2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case PROBLEM_NAME:
                {
                    String name1 = p1.getName();
                    String name2 = p2.getName();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case PROBLEM_TYPE:
                {
                    String name1 = p1.getType().getDescription();
                    String name2 = p2.getType().getDescription();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case STATUS:
                {
                    int diff = p1.getStatus().getId() - p2.getStatus().getId();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case DIVISION:
                {
                    int diff = rp1.getDivision().compareTo(rp2.getDivision());
                    if (diff != 0) {
                        return diff * sign;
                    }

                }
            default:
                throw new RuntimeException("not implemented, column=" + sortElem);
            }
        }
        return 0;
    }
}
