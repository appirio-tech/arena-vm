package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.components.LongCodeViewer;
import com.topcoder.client.contestApplet.uilogic.panels.table.ValueTransformDecoratorCellRenderer;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.shared.language.BaseLanguage;

public class SubmissionHistoryPanel extends TablePanel {
    // Column headers
    private static final String[] headers = new String[] {" ", "Submission",  "Time",  "Language", "Score"};
    private static final String[] exampleHeaders = new String[] {" ", "Example Submission",  "Time",  "Language"};
    private static final Class[]  classes =  new Class[] {String.class, Integer.class, Date.class, String.class, Double.class};
    private static final Class[]  exampleClasses =  new Class[] {String.class, Integer.class, Date.class, String.class, Double.class};

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("0.00");

    private SubmissionHistoryResponse response;

    protected String getTableName() {
        return "submission_history_table";
    }

    protected String getMenuName() {
        return "submission_history_menu";
    }

    public SubmissionHistoryPanel(ContestApplet ca, SubmissionHistoryResponse response, UIPage page) {
        super(ca, page, new SubmissionHistoryTableModel(response));
        this.response = response;
        page.getComponent("submission_history_menu_source").addEventListener("Action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openSourceEvent();
                }
            });

        page.getComponent("submission_history_time_column").setProperty("CellRenderer", new ValueTransformDecoratorCellRenderer((TableCellRenderer)page.getComponent("submission_history_time_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    if (value == null) return null;
                    return DATE_FORMAT.format(value);
                }
            });

        if (response.isExampleHistory()) {
            page.getComponent("submission_history_menu_last").addEventListener("Action", new UIActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openResultsEvent();
                    }
                });
        } else {
            page.getComponent("submission_history_score_column").setProperty("CellRenderer", new ValueTransformDecoratorCellRenderer((TableCellRenderer)page.getComponent("submission_history_score_column").getProperty("CellRenderer")) {
                    protected Object transform(Object value, int row, int column) {
                        if (value == null) return null;
                        return SCORE_FORMAT.format(value);
                    }
                });
        }

        table.addEventListener("Mouse", new UIMouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseClickEvent(e);
            }
        });

        ((JTableHeader) table.getProperty("TableHeader")).addMouseListener(new UIMouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });

        table.addEventListener("Key", new UIKeyAdapter() {
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
        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
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
