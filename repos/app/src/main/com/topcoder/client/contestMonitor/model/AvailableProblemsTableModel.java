package com.topcoder.client.contestMonitor.model;

/** Store information about available problems.
 * @author John Waymouth
 */

import com.topcoder.server.contest.ProblemData;
import com.topcoder.server.contest.RoundType;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Collection;
import java.util.Iterator;

public final class AvailableProblemsTableModel extends SortedTableModel {

    private static final Logger log = Logger.getLogger(AvailableProblemsTableModel.class);

    private static final int PROBLEM_ID = 0;
    private static final int PROBLEM_NAME = 1;
    private static final int PROBLEM_TYPE = 2;
    private static final int STATUS = 3;


    public AvailableProblemsTableModel() {
        super(new String[]{
            "ID",
            "Name",
            "Type",
            "Status"
        }, new Class[]{
            String.class, // id
            String.class, // class
            String.class, // method
            String.class   // status
        });
        addSortElement(new SortElement(STATUS, false));
        addSortElement(new SortElement(PROBLEM_ID, false));
        addSortElement(new SortElement(PROBLEM_NAME, false));
        addSortElement(new SortElement(PROBLEM_TYPE, false));
    }


    public ProblemData getProblem(int rowIndex) {
        return (ProblemData) get(rowIndex);
    }

    public Collection getProblems() {
        return getItemList();
    }

    public ProblemData removeProblem(int rowIndex) {
        return (ProblemData) remove(rowIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ProblemData problem = getProblem(rowIndex);
        switch (columnIndex) {
        case PROBLEM_ID:
            return new Integer(problem.getId());
        case PROBLEM_NAME:
            return problem.getName();
        case PROBLEM_TYPE:
            return problem.getType();
        case STATUS:
            return problem.getStatus();
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
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
        ProblemData p1 = (ProblemData) o1;
        ProblemData p2 = (ProblemData) o2;
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
            default:
                throw new RuntimeException("not implemented, column=" + sortElem);
            }
        }
        return 0;
    }
}
