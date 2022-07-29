package com.topcoder.client.contestApplet.panels.table;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.frames.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;
import com.topcoder.netCommon.contestantMessages.*;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse.ImportantMessage;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;

public final class ImportantMessageSummaryPanel extends TablePanel {

    // Column headers
    private static final String[] headers = new String[]{
        "Read", "Message"
    };

    // Pops up when right-clicking on a row
    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Open", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMessageEvent();
            }
        }),
    };
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
    public void update(GetImportantMessagesResponse resp) {
        getTableModel().update(resp.getItems());
    }
    
    public ImportantMessageSummaryPanel(ContestApplet ca) {
        super(ca, "Important Messages", new ImportantMessageSummaryTableModel());
        setContestPopup("", OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        //contestTable.setMinimumSize(new Dimension(300, 200));
        //contestTable.setPreferredSize(new Dimension(300, 200));
        contestTable.getTableHeader().setBackground(Common.BG_COLOR);
        contestTable.getColumnModel().getColumn(0).setMinWidth(159);
        contestTable.getColumnModel().getColumn(0).setPreferredWidth(159);
        contestTable.getColumnModel().getColumn(0).setResizable(false);
        contestTable.getColumnModel().getColumn(0).setMaxWidth(159);
        contestTable.getColumnModel().getColumn(0).setCellRenderer(new DateRenderer());
        contestTable.getColumnModel().getColumn(1).setMinWidth(300);
        contestTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        contestTable.getColumnModel().getColumn(1).setCellRenderer(new BroadcastMessageRenderer());

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
        ImportantMessage bc = (ImportantMessage) tableModel.get(r);
        ImportantMessageDialog md = new ImportantMessageDialog(ca, bc.getMessage());
        md.show();
    }

    // Has to be static since we pass it to super()...no this pointer yet.
    static class ImportantMessageSummaryTableModel extends SortedTableModel {

        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param bc
         */

        public ImportantMessageSummaryTableModel() {
            super(headers, new Class[]{
                Long.class,
                String.class
            });
            addSortElement(new SortElement(0, true));
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            ImportantMessage cur = (ImportantMessage) get(rowIndex);
            switch (columnIndex) {
            case 0:
                return new Long(cur.getTime());
            case 1:
                return Common.htmlEncode(cur.getMessage());
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);

            }
        }

        public int compare(Object o1, Object o2) {
            ImportantMessage bc1 = (ImportantMessage) o1;
            ImportantMessage bc2 = (ImportantMessage) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        long diff = bc1.getTime() - bc2.getTime();
                        //if (diff != 0) return sign*(diff > 0 ? 1 : -1);
                        //break;
                        return sign * (diff > 0 ? 1 : -1);
                    }
                case 1:
                    {
                        int diff = bc1.getMessage().compareTo(bc2.getMessage());
                        //if (diff != 0) return sign * diff;
                        //break;
                        return sign * diff;
                    }
                default:
                    throw new IllegalArgumentException("Bad column: " + sortElem);
                }
            }
            throw new IllegalStateException("Problem sorting broadcasts: " + getItemList());
        }

    }
}


