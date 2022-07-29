/*
 * SubmissionHistoryPanel
 * 
 * Created 06/14/2007
 */
package com.topcoder.client.contestApplet.panels.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestApplet.widgets.CellRendererFactory;
import com.topcoder.client.contestApplet.widgets.LongCodeViewer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.shared.language.BaseLanguage;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: SubmissionHistoryPanel.java 67962 2008-01-15 15:57:53Z mural $
 */
public final class SubmissionHistoryPanel extends TablePanel {

    // Column headers
    private static final String[] headers = new String[] {" ", "Submission",  "Time",  "Language", "Score"};
    private static final String[] exampleHeaders = new String[] {" ", "Example Submission",  "Time",  "Language"};
    private static final Class[]  classes =  new Class[] {String.class, Integer.class, Date.class, String.class, Double.class};
    private static final Class[]  exampleClasses =  new Class[] {String.class, Integer.class, Date.class, String.class, Double.class};

    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Source", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSourceEvent();
            }
        }),
    };

    private final MenuItemInfo[] EXAMPLE_POPUP = {
            new MenuItemInfo("Source", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openSourceEvent();
                }
            }),
            new MenuItemInfo("Last example results", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(ca.getModel().getRound(response.getRoundId()).getRoundTypeId().intValue() == ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID) {
                        Common.showMessage("Error", "Not available for this round", ca);
                    } else
                        openResultsEvent();
                }
            })
        };
    
    
    private SubmissionHistoryResponse response;
    
    public SubmissionHistoryPanel(ContestApplet ca, SubmissionHistoryResponse response) {
        super(ca, (response.isExampleHistory() ? "Example Submissions" : "Full Submissions"), new SubmissionHistoryTableModel(response));
        this.response = response;
        setContestPopup("", response.isExampleHistory() ? EXAMPLE_POPUP : OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colums = contestTable.getColumnModel();
        colums.getColumn(0).setMaxWidth(10);
        colums.getColumn(1).setMaxWidth(70);
        TableColumn column2 = colums.getColumn(2);
        column2.setCellRenderer(CellRendererFactory.apply(column2.getCellRenderer(), new SimpleDateFormat("MM-dd-yyyy HH:mm:ss"), SwingUtilities.CENTER));
        column2.setHeaderRenderer(CellRendererFactory.apply(column2.getHeaderRenderer(), SwingUtilities.CENTER));
        column2.setWidth(120);
        if (!response.isExampleHistory()) {
            TableColumn column4 = colums.getColumn(4);
            column4.setCellRenderer(CellRendererFactory.apply(column4.getCellRenderer(),new DecimalFormat("0.00"), SwingUtilities.RIGHT));
            column4.setHeaderRenderer(CellRendererFactory.apply(column4.getHeaderRenderer(), SwingUtilities.CENTER));
        }
        
        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseClickEvent(e);
            }
        });

        contestTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });

        contestTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    openSourceEvent();
            }
        });
        
    }

    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);

        if (SwingUtilities.isRightMouseButton(e)) {
            showContestPopup(e);
        } else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e))
            openSourceEvent();
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
        }
    }


    private synchronized void openSourceEvent() {
        int r = contestTable.getSelectedRow();
        if (r == -1) return;
        r = ((Integer) tableModel.get(r)).intValue();
        LongCodeViewer viewer = new LongCodeViewer(ca, ca.getCurrentFrame(), response.getRoundId(), response.getHandle(), response.getComponentId(), response.isExampleHistory(), response.getNumber(r), true);
        viewer.show();
    }
    
    private synchronized void openResultsEvent() {
        ca.getRequester().requestLongTestResults(response.getComponentId(), getRoom(), response.getHandle(), 0);
    }

    private long getRoom() {
        return ca.getModel().getRound(response.getRoundId()).getRoomByCoder(response.getHandle()).getRoomID().longValue();
    }
    
    private static class SubmissionHistoryTableModel extends SortedTableModel {
        private SubmissionHistoryResponse response;

        public SubmissionHistoryTableModel(SubmissionHistoryResponse response) {
            super(response.isExampleHistory() ? exampleHeaders : headers, response.isExampleHistory() ? exampleClasses : classes);
            addSortElement(new SortElement(1, true));
            addSortElement(new SortElement(2, true));
            addSortElement(new SortElement(3, false));
            if (!response.isExampleHistory()) {
                addSortElement(new SortElement(4, false));
            }
            this.response = response;
            int count = response.getCount();
            ArrayList mockItems = new ArrayList(count);
            for (int i = 0; i < count; i++) {
                mockItems.add(new Integer(i));
            }
            this.update(mockItems);
        }


        public Object getValueAt(int row, int columnIndex) {
            int rowIndex = ((Number) get(row)).intValue();
            return getDirectValueAt(rowIndex, columnIndex);
        }


        private Object getDirectValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return response.hasPendingTest(rowIndex) ? "*" : " ";
                case 1:
                    return new Integer(response.getNumber(rowIndex));
                case 2:
                    return new Date(response.getTime(rowIndex));
                case 3:
                    return BaseLanguage.getLanguage(response.getLanguageId(rowIndex)).getName();
                case 4:
                    return new Double(response.getScore(rowIndex));
                default:
                    throw new IllegalArgumentException("Bad column: " + columnIndex);
            }
        }
        
        public int compare(Object o1, Object o2) {
            int row1 = ((Integer) o1).intValue();
            int row2 = ((Integer) o2).intValue();
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                Comparable v1 = (Comparable) getDirectValueAt(row1, col);
                Comparable v2 = (Comparable) getDirectValueAt(row2, col);
                int res = v1.compareTo(v2);
                if (res != 0) return sign * res;
            }
            return 0;
        }
    }
}


