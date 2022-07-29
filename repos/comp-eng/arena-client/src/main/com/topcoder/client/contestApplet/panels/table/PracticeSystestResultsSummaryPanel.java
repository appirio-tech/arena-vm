/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * This class represent a system test results summary panel.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link openMessageEvent()} to show answer checking result to use.
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li>Update {@link #headers} constant to include Memory column</li>
 *     <li>Update {@link @PracticeSystestSummaryTableModel.PracticeSystestSummaryTableModel} </li>
 *     <li>Update {@link @PracticeSystestSummaryTableModel.getValueAt(int, int)} </li>
 *     <li>Update {@link @PracticeSystestSummaryTableModel.compare(Object, Object)} </li>
 * </ol>
 * </p>
 *
 * @author gevak, dexy
 * @version 1.2
 */
public final class PracticeSystestResultsSummaryPanel extends TablePanel {
    /** The column headers. */
    private static final String[] headers = new String[]{
        "Problem", "Test Case", "Success", "Args", "Expected", "Received", "Time", "Memory"
    };

    // Pops up when right-clicking on a row
    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Details", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMessageEvent();
            }
        }),
    };

    private List items = new ArrayList();

    private boolean enabled = true;

    public void setPanelEnabled(boolean on) {
        enabled = on;
    }

    public void reset() {
        items.clear();

        //get component names
        RoomModel roomModel = ca.getModel().getCurrentRoom();
        RoundModel roundModel = roomModel.getRoundModel();
        components = roundModel.getAssignedComponents(roomModel.getDivisionID());

    }
    public void update(PracticeSystemTestResultResponse resp) {
        items.add(resp);
        getTableModel().update(items);
    }

    public PracticeSystestResultsSummaryPanel(ContestApplet ca) {
        super(ca, "Results", new PracticeSystestSummaryTableModel());
        setContestPopup("", OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        //contestTable.setMinimumSize(new Dimension(300, 200));
        //contestTable.setPreferredSize(new Dimension(300, 200));
        contestTable.getTableHeader().setBackground(Common.BG_COLOR);
        /*contestTable.getColumnModel().getColumn(0).setMinWidth(159);
        contestTable.getColumnModel().getColumn(0).setPreferredWidth(159);
        contestTable.getColumnModel().getColumn(0).setResizable(false);
        contestTable.getColumnModel().getColumn(0).setMaxWidth(159);

        contestTable.getColumnModel().getColumn(1).setMinWidth(300);
        contestTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        contestTable.getColumnModel().getColumn(1).setCellRenderer(new BroadcastMessageRenderer());
        */

        contestTable.getColumnModel().getColumn(2).setCellRenderer(new SuccessRenderer(ca));
        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    mouseClickEvent(e);
                }
            }
        });

        contestTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });

        contestTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(enabled) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        openMessageEvent();
                }
            }
        });

    }

    private static ProblemComponentModel components[] = null;


    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);

        if (SwingUtilities.isRightMouseButton(e))
            showContestPopup(e);
        else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e))
            openMessageEvent();
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
            getTable().getTableHeader().repaint();
        }
    }

    /**
     * Handles open message event.
     */
    private synchronized void openMessageEvent() {
        int r = contestTable.getSelectedRow();
        if (r == -1) return;
        PracticeSystemTestResultResponse bc = (PracticeSystemTestResultResponse) tableModel.get(r);
        String text = "";
        text += "Problem: " + getCompVal(bc.getResultData().getComponentId()) + "\n";
        text += "Test Case: " + bc.getResultData().getTestCaseIndex() + "\n";
        text += "Succeeded: " + (bc.getResultData().isSucceeded() ? "Yes" : "No") + "\n";
        text += "Execution Time: ";
        long val = bc.getResultData().getExecTime();
        if(val < 1000)
            text += "" + val + " ms\n";
        else
            text += ((double)val / 1000) + "s\n";
        long memoryUsed = bc.getResultData().getMaxMemoryUsed();
        DecimalFormat df = new DecimalFormat("0.000");
        text += "Peak memory used: " + df.format((double) memoryUsed / 1024.0) + "MB\n" ;

        text += "Args:\n" + ContestConstants.makePretty(bc.getResultData().getArgs()) + "\n\n";
        text += "Expected:\n" + ContestConstants.makePretty(bc.getResultData().getExpectedValue()) + "\n\n";
        if(bc.getResultData().getReturnValue() == null)
            text += "Received:\n" + bc.getResultData().getMessage() + "\n";
        else
            text += "Received:\n" + ContestConstants.makePretty(bc.getResultData().getReturnValue()) + "\n";
        if (!bc.getResultData().isSucceeded()) {
            text += "\nAnswer checking result:\n" + bc.getResultData().getCheckAnswerResponse() + "\n";
        }
        ca.popup(ContestConstants.TEXT_AREA, "Practice System Test Case Info", text);
    }

    public static int getCompVal(int componentID) {
        for(int i = 0; i < components.length; i++) {
            if(componentID == components[i].getID().intValue())
                return components[i].getPoints().intValue();
        }
        return 0;
    }

    // Has to be static since we pass it to super()...no this pointer yet.
    static class PracticeSystestSummaryTableModel extends SortedTableModel {
        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param bc
         */
        public PracticeSystestSummaryTableModel() {
            super(headers, new Class[]{
                Integer.class,
                Integer.class,
                Boolean.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
            });
            addSortElement(new SortElement(0, true));
            addSortElement(new SortElement(1, true));
            addSortElement(new SortElement(2, true));
            addSortElement(new SortElement(6, true));
            addSortElement(new SortElement(7, true));
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            PracticeSystemTestResultResponse cur = (PracticeSystemTestResultResponse) get(rowIndex);
            switch (columnIndex) {
            case 0:
                return new Integer(getCompVal(cur.getResultData().getComponentId()));
            case 1:
                return new Integer(cur.getResultData().getTestCaseIndex());
            case 2:
                return new Boolean(cur.getResultData().isSucceeded());
            case 3:
                return ContestConstants.makePretty(cur.getResultData().getArgs());
            case 4:
                return ContestConstants.makePretty(cur.getResultData().getExpectedValue());
            case 5:
                if(cur.getResultData().getReturnValue() == null)
                    return cur.getResultData().getMessage();
                else
                    return ContestConstants.makePretty(cur.getResultData().getReturnValue());
            case 6:
                long val = cur.getResultData().getExecTime();
                if(val < 1000)
                    return "" + val + " ms";
                else
                    return "" + ((double)val / 1000) + "s";
            case 7:
                long memoryUsed = cur.getResultData().getMaxMemoryUsed();
                DecimalFormat df = new DecimalFormat("0.000");
                if (memoryUsed < 0) {
                    return "N/A" ;
                }
                else {
                    return df.format(((double) memoryUsed / 1024.0)) + "MB";
                }
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);

            }
        }

        public int compare(Object o1, Object o2) {
            PracticeSystemTestResultResponse bc1 = (PracticeSystemTestResultResponse) o1;
            PracticeSystemTestResultResponse bc2 = (PracticeSystemTestResultResponse) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        int compval1 = getCompVal(bc1.getResultData().getComponentId());
                        int compval2 = getCompVal(bc2.getResultData().getComponentId());;
                        int diff = compval2 - compval1;
                        if(diff != 0)
                            return sign * (diff > 0 ? 1 : -1 );
                        break;
                    }
                case 1:
                    {
                        int diff = bc2.getResultData().getTestCaseIndex() - bc1.getResultData().getTestCaseIndex();
                        if(diff != 0)
                            return sign * (diff > 0 ? 1 : -1 );
                        break;
                    }
                case 2:
                {
                        int val1 = bc1.getResultData().isSucceeded() ? 1 : 0;
                        int val2 = bc2.getResultData().isSucceeded() ? 1 : 0;
                        int diff = val2 - val1;
                        if(diff != 0)
                            return sign * (diff > 0 ? 1 : -1 );
                    break;
                }
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    long val1 = bc1.getResultData().getExecTime();
                    long val2 = bc2.getResultData().getExecTime();;
                    long diff = val2 - val1;
                    if(diff != 0)
                        return sign * (diff > 0 ? 1 : -1 );
                    break;
                case 7:
                {
                    long memoryUsed1 = bc1.getResultData().getMaxMemoryUsed();
                    long memoryUsed2 = bc2.getResultData().getMaxMemoryUsed();
                    long diffMemory = memoryUsed2 - memoryUsed1;
                    if(diffMemory != 0)
                        return sign * (diffMemory > 0 ? 1 : -1 );
                    break;
                }
                default:

                    throw new IllegalArgumentException("Bad column: " + sortElem);
                }
            }
            return 0;
        }

    }
}


