/*
 * LongTestResultsSummaryPanel
 * 
 * Created 07/30/2007
 */
package com.topcoder.client.contestApplet.panels.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.data.LongTestResultData;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: LongTestResultsSummaryPanel.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongTestResultsSummaryPanel extends TablePanel {

    // Column headers
    private static final String[] headers = new String[]{
        "Test Case", "Score", "Time", "Message"
    };

    // Pops up when right-clicking on a row
    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Details", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMessageEvent();
            }
        }),
    };
    
    private DecimalFormat scoreFormat = new DecimalFormat("0.00");
    private boolean enabled = true;
    private LongTestResultsResponse response;

    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
   
    public LongTestResultsSummaryPanel(ContestApplet ca, LongTestResultsResponse response) {
        super(ca, "Results", new LongTestResultsSummaryTableModel());
        this.response = response;
        setContestPopup("", OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        contestTable.getTableHeader().setBackground(Common.BG_COLOR);
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
        getTableModel().update(Arrays.asList(response.getResultData()));
        
    }

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


    private synchronized void openMessageEvent() {
        int r = contestTable.getSelectedRow();
        if (r == -1) return;
        
        LongTestResultData test = (LongTestResultData) tableModel.get(r);
        StringBuffer sb = new StringBuffer(1024);
        sb.append("<html>");
        sb.append(test.getTestCaseIndex()).append(") Score: ").append(scoreFormat.format(test.getScore())).append(" Run Time: ").append(test.getExecTime()).append(" ms.<br><br>");
        sb.append("Example Case:<br>").append(response.getArgs()[test.getTestCaseIndex()]).append("<br><br>");
        sb.append("Fatal Errors:<br>").append(Common.htmlEncodeNoHeaders(test.getMessage())).append("<br><br>");
        sb.append("Standard Out:<br>").append(Common.htmlEncodeNoHeaders(test.getStdOut())).append("<br><br>");
        sb.append("Standard Error:<br>").append(Common.htmlEncodeNoHeaders(test.getStdErr())).append("<br><br>");
        sb.append("---------------------------------------------------------------------------<br>");
        sb.append("</html>");
        ca.popup(ContestConstants.TEXT_AREA, "Test Result Info", sb.toString());
        
    }
    
    // Has to be static since we pass it to super()...no this pointer yet.
    static class LongTestResultsSummaryTableModel extends SortedTableModel {

        public LongTestResultsSummaryTableModel() {
            super(headers, new Class[]{
                Integer.class,
                Double.class,
                String.class,
                String.class,
            });
            addSortElement(new SortElement(0, true));
            addSortElement(new SortElement(1, true));
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            LongTestResultData cur = (LongTestResultData) get(rowIndex);
            switch (columnIndex) {
            case 0:
                return new Integer(cur.getTestCaseIndex());
            case 1:
                return new Double(cur.getScore());
            case 2:
                long val = cur.getExecTime();
                if(val < 1000) {
                    return "" + val + " ms";
                } else {
                    return "" + ((double)val / 1000) + "s";
                }
            case 3:
                if (cur.getMessage() != null) {
                    return cur.getMessage();
                } else {
                    return "";
                }
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);
            }
        }
        
        

        public int compare(Object o1, Object o2) {
            LongTestResultData bc1 = (LongTestResultData) o1;
            LongTestResultData bc2 = (LongTestResultData) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        int diff = bc2.getTestCaseIndex() - bc1.getTestCaseIndex();
                        if(diff != 0)
                            return sign * (diff > 0 ? 1 : -1 );
                        break;
                    }
                case 1:
                {
                    double sc1 = bc1.getScore();
                    double sc2 = bc2.getScore();
                    int diff = (sc1 > sc2 ? 1  : sc1 == sc2 ? 0 : -1);
                    if (diff != 0) {
                        return sign * diff;
                    }
                }
                case 2:
                    long val1 = bc1.getExecTime();
                    long val2 = bc2.getExecTime();;
                    long diff = val2 - val1;
                    if(diff != 0)
                        return sign * (diff > 0 ? 1 : -1 );
                    break;
                case 3:
                    break;
                default:
                    
                    throw new IllegalArgumentException("Bad column: " + sortElem);
                }
            }
            return 0;
        }

    }
}


