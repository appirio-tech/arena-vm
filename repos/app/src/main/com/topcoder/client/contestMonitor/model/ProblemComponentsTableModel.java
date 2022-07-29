package com.topcoder.client.contestMonitor.model;


import com.topcoder.server.contest.ComponentData;
import com.topcoder.server.contest.Difficulty;
import com.topcoder.server.contest.Division;
import com.topcoder.server.contest.RoundComponentData;
import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;

import java.util.Collection;
import java.util.Iterator;

public final class ProblemComponentsTableModel extends SortedTableModel {

    //private static final Logger log = Logger.getLogger(ProblemComponentsTableModel.class);


    private static final int COMPONENT_ID = 0;
    private static final int CLASS_NAME = 1;
    private static final int METHOD_NAME = 2;
    private static final int TYPE = 3;
    private static final int DIVISION = 4;
    private static final int DIFFICULTY = 5;
    private static final int POINTS = 6;
    private static final int OPEN_ORDER = 7;
    private static final int SUBMIT_ORDER = 8;


    public ProblemComponentsTableModel() {
        super(new String[]{
            "ID",
            "Class",
            "Method",
            "Type",
            "Division",
            "Difficulty",
            "Points",
            "Open Order",
            "Submit Order"
        }, new Class[]{
            String.class, // id
            String.class, // class
            String.class, // method
            String.class, // type
            Division.class, // Division
            Difficulty.class, // Difficulty
            Double.class, // points
            Integer.class, // open order
            Integer.class // submit order
        });
        addSortElement(new SortElement(DIVISION, false));
        addSortElement(new SortElement(DIFFICULTY, false));
        addSortElement(new SortElement(COMPONENT_ID, false));
    }


    public RoundComponentData getRoundComponent(int rowIndex) {
        return (RoundComponentData) get(rowIndex);
    }

    public Collection getComponents() {
        return getItemList();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        RoundComponentData component = getRoundComponent(rowIndex);
        switch (columnIndex) {
        case COMPONENT_ID:
            return new Integer(component.getComponentData().getId());
        case CLASS_NAME:
            return component.getComponentData().getClassName();
        case METHOD_NAME:
            return component.getComponentData().getMethodName();
        case TYPE:
            return component.getComponentData().getType().getDescription();
        case DIVISION:
            return component.getDivision();
        case DIFFICULTY:
            return component.getDifficulty();
        case POINTS:
            return new Double(component.getPointValue());
        case OPEN_ORDER:
            return new Integer(component.getOpenOrder());
        case SUBMIT_ORDER:
            return new Integer(component.getSubmitOrder());
        default:
            throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
        }
    }


    public int compare(Object o1, Object o2) {
        RoundComponentData c1 = (RoundComponentData) o1;
        RoundComponentData c2 = (RoundComponentData) o2;
        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            switch (col) {
            case COMPONENT_ID:
                {
                    Integer id1 = new Integer(c1.getComponentData().getId());
                    Integer id2 = new Integer(c2.getComponentData().getId());
                    int diff = id1.compareTo(id2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
            case CLASS_NAME:
                {
                    String name1 = c1.getComponentData().getClassName();
                    String name2 = c2.getComponentData().getClassName();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case METHOD_NAME:
                {
                    String name1 = c1.getComponentData().getMethodName();
                    String name2 = c2.getComponentData().getMethodName();
                    int diff = name1.compareTo(name2);
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case DIVISION:
                {
                    int diff = c1.getDivision().compareTo(c2.getDivision());
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case DIFFICULTY:
                {
                    int diff = c1.getDifficulty().compareTo(c2.getDifficulty());
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case POINTS:
                {
                    double diff = c1.getPointValue() - c2.getPointValue();
                    if (diff != 0) {
                        return sign * (diff < 0.0 ? -1 : 1);
                    }
                }
            case OPEN_ORDER:
                {
                    int diff = c1.getOpenOrder() - c2.getOpenOrder();
                    if (diff != 0) {
                        return diff * sign;
                    }
                }
                break;
            case SUBMIT_ORDER:
                {
                    int diff = c1.getSubmitOrder() - c2.getSubmitOrder();
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


    public RoundComponentData removeComponent(int row) {
        return (RoundComponentData) remove(row);
    }

    public void add(ComponentData component) {
        RoundComponentData data = new RoundComponentData();
        data.setComponentData(component);
        add(data);
    }

    public void setDivision(Division div) {
        for (int i = 0; i < getRowCount(); i++) {
            RoundComponentData component = getRoundComponent(i);
            component.setDivision(div);
            fireTableCellUpdated(i, DIVISION);
        }
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (value != null) {
            RoundComponentData component = getRoundComponent(rowIndex);
            switch (columnIndex) {
            case DIVISION:
                component.setDivision((Division) value);
                break;
            case DIFFICULTY:
                component.setDifficulty((Difficulty) value);
                break;
            case POINTS:
                component.setPointValue(((Double) value).doubleValue());
                break;
            case OPEN_ORDER:
                component.setOpenOrder(((Integer) value).intValue());
                break;
            case SUBMIT_ORDER:
                component.setSubmitOrder(((Integer) value).intValue());
                break;
            default:
                throw new IllegalArgumentException("not implemented, columnIndex=" + columnIndex);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 4;
    }
}
